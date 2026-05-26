package de.dertoaster.schematicworldgen.feature;

import com.mojang.serialization.Codec;
import de.dertoaster.schematicworldgen.feature.config.SurfaceFragmentConfig;
import de.dertoaster.schematicworldgen.feature.util.noise.CellularNoise;
import de.dertoaster.schematicworldgen.feature.util.noise.OpenSimplex2S;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.Queue;

// Source: https://github.com/YUNG-GANG/YUNGs-Cave-Biomes/blob/1.21.1/Common/src/main/java/com/yungnickyoung/minecraft/yungscavebiomes/world/feature/WaterSurfaceIceFragmentFeature.java
public class SurfaceFragmentFeature extends Feature<SurfaceFragmentConfig> {

    public SurfaceFragmentFeature(Codec<SurfaceFragmentConfig> codec) {
        super(codec);
    }

    // Flood-fill configuration.
    private static final int MAX_MAX_FLOOD_RADIUS = 12;
    private static final int EFFECTIVE_MAX_FLOOD_RADIUS = MAX_MAX_FLOOD_RADIUS - 1;
    private static final int EFFECTIVE_MAX_FLOOD_DIAMETER = EFFECTIVE_MAX_FLOOD_RADIUS * 2 + 1;

    @Override
    public boolean place(FeaturePlaceContext<SurfaceFragmentConfig> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        // Call the thread-local allocation to perform the flood-fill.
        iceFragmentFillerThreadLocal.get().execute(origin, level, context.config());

        return false;
    }

    private static final ThreadLocal<FragmentFiller> iceFragmentFillerThreadLocal = ThreadLocal.withInitial(FragmentFiller::new);

    private static class FragmentFiller {
        private final Queue<BlockPos> floodQueue;
//        private final boolean[] visited;
        private final Vector3f domainWarpVector;

        public FragmentFiller() {
            this.floodQueue = new ArrayDeque<>(EFFECTIVE_MAX_FLOOD_DIAMETER * EFFECTIVE_MAX_FLOOD_DIAMETER);
//            this.visited = new boolean[EFFECTIVE_MAX_FLOOD_DIAMETER * EFFECTIVE_MAX_FLOOD_DIAMETER];
            this.domainWarpVector = new Vector3f();
        }

        public void execute(BlockPos origin, WorldGenLevel level, SurfaceFragmentConfig config) {
            long seed = level.getSeed();
            long noiseSeedWarp = seed ^ config.warpNoiseSeedFlip();
            long noiseSeedCell = seed ^ config.cellNoiseSeedFlip();
            long noiseSeedFlood = seed ^ config.floodBoundaryNoiseSeedFlip();

            // Initialize for a new flood-fill over the ice surface fragment noise
            this.floodQueue.clear();
//            Arrays.fill(visited, false);

            // Start flood-fill at the feature origin
            this.floodQueue.add(origin);

            // Perform the flood-fill
            while (!this.floodQueue.isEmpty()) {
                BlockPos currentPos = this.floodQueue.poll();
                BlockState currentState = level.getBlockState(currentPos);

                // Nothing to do here if it's not water or ice.
                if (!currentState.is(config.blockFilter())) continue;

                // Get domain warping vector for cellular noise using a purpose-designed noise function.
                OpenSimplex2S.vec3Noise3_ImproveXZ(
                        noiseSeedWarp,
                        currentPos.getX() * config.domainWarpFrequency(),
                        currentPos.getY() * config.domainWarpFrequency(),
                        currentPos.getZ() * config.domainWarpFrequency(),
                        this.domainWarpVector
                );

                // Evaluate cellular noise at the warped position.
                // 3D noise is used so that two water bodies offset only vertically don't show identical ice patterns.
                float iceShardNoiseValue = CellularNoise.sampleDistance2Sub_ImproveXZ(
                        noiseSeedCell,
                        currentPos.getX() * config.fragmentNoiseFrequency() + this.domainWarpVector.x() * (config.fragmentNoiseFrequency() * config.domainWarpAmplitude()),
                        currentPos.getY() * config.fragmentNoiseFrequency() + this.domainWarpVector.y() * (config.fragmentNoiseFrequency() * config.domainWarpAmplitude()),
                        currentPos.getZ() * config.fragmentNoiseFrequency() + this.domainWarpVector.z() * (config.fragmentNoiseFrequency() * config.domainWarpAmplitude())
                );

                // If we're in the boundary between two ice shards, nothing to do here.
                if (iceShardNoiseValue <= config.fragmentSeparation()) continue;

                // We know this one is ice now.
                level.setBlock(currentPos, config.blockStateProvider().getState(level.getRandom(), currentPos), 2);

                // Now consider propagating into the four neighbors.
                considerNeighbor(noiseSeedFlood, origin, currentPos.east(), config);
                considerNeighbor(noiseSeedFlood, origin, currentPos.west(), config);
                considerNeighbor(noiseSeedFlood, origin, currentPos.south(), config);
                considerNeighbor(noiseSeedFlood, origin, currentPos.north(), config);
            }
        }

        private void considerNeighbor(long noiseSeedFlood, BlockPos origin, BlockPos neighborPos, SurfaceFragmentConfig config) {

            // Delta from feature placement center.
            int dx = neighborPos.getX() - origin.getX();
            int dz = neighborPos.getZ() - origin.getZ();

            // Initial out-of-range check.
            if (dx < -config.effectiveMaxFloodRadius() || dx > config.effectiveMaxFloodRadius() || dz < -config.effectiveMaxFloodRadius() || dz > config.effectiveMaxFloodRadius()) return;

            // Don't add to the queue if it's already been visited. Otherwise mark it as such.
//            int visitedIndex = (dz + config.effectiveMaxFloodRadius()) * config.effectiveMaxFloodDiameter() + (dx + config.effectiveMaxFloodRadius());
//            if (this.visited[visitedIndex]) return;
//            this.visited[visitedIndex] = true;

            if (this.floodQueue.contains(neighborPos)) {
                return;
            }

            // Keep out-of-range borders looking nice in all directions by bounding the search to a noisy circle.
            float distSq = dx * dx + dz * dz;
            if (distSq > config.effectiveMaxFloodRadius() * config.effectiveMaxFloodRadius()) return;
            if (distSq > config.minMaxFloodRadius() * config.minMaxFloodRadius()) {
                float noiseValue = OpenSimplex2S.noise3_ImproveXZ(
                        noiseSeedFlood,
                        (origin.getX() + dx) * config.floodBoundaryNoiseFrequency(),
                        origin.getY() * config.floodBoundaryNoiseFrequency(),
                        (origin.getZ() + dz) * config.floodBoundaryNoiseFrequency()
                );
                float noisyRadius = noiseValue * (0.5f * config.maxFloodRadiusRange()) + (0.5f * config.maxFloodRadiusRange() + config.minMaxFloodRadius());
                if (distSq > noisyRadius * noisyRadius) return;
            }

            // We've figured out we do want to process this position in the flood-fill.
            this.floodQueue.add(neighborPos);
        }
    }
}
