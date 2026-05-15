package de.dertoaster.schematicworldgen.feature;

import com.mojang.serialization.Codec;
import de.dertoaster.schematicworldgen.feature.config.SchematicEntry;
import de.dertoaster.schematicworldgen.feature.config.SchematicFeatureConfig;
import de.dertoaster.schematicworldgen.feature.processor.ProcessorRegistry;
import de.dertoaster.schematicworldgen.feature.processor.SchematicProcessors;
import de.dertoaster.schematicworldgen.feature.resolver.PlacementResolver;
import de.dertoaster.schematicworldgen.placement.PlacementEngine;
import de.dertoaster.schematicworldgen.placement.collision.CollisionChecker;
import de.dertoaster.schematicworldgen.placement.context.PlacementContext;
import de.dertoaster.schematicworldgen.placement.processor.IPlacementProcessor;
import de.dertoaster.schematicworldgen.schematic.ILoadedSchematic;
import de.dertoaster.schematicworldgen.schematic.cache.SchematicCache;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Main schematic world generation feature.
 */
public final class SchematicFeature
        extends Feature<SchematicFeatureConfig> {

    public SchematicFeature(
            Codec<SchematicFeatureConfig> codec
    ) {
        super(codec);
    }

    @Override
    public boolean place(
            FeaturePlaceContext<SchematicFeatureConfig> context
    ) {

        RandomSource random =
                context.random();

        SchematicEntry entry =
                context.config()
                        .selectRandom(random);

        ILoadedSchematic schematic =
                SchematicCache.INSTANCE
                        .get(entry.file());

        Rotation rotation =
                entry.randomRotation()
                        ? Rotation.getRandom(random)
                        : Rotation.NONE;

        Mirror mirror =
                entry.randomMirror()
                        ? random.nextBoolean()
                        ? Mirror.FRONT_BACK
                        : Mirror.LEFT_RIGHT
                        : Mirror.NONE;

        List<IPlacementProcessor>
                processors =
                new ArrayList<>();

        for (String id : context.config().processors()) {
            processors.add(
                    SchematicProcessors.create(id)
            );
        }

        PlacementContext placementContext =
                new PlacementContext(

                        context.level(),

                        random,

                        entry,

                        rotation,

                        mirror,

                        processors

                );

        // TODO: This doesnt provide proper results as of now!
        BlockPos placementPos =
                PlacementResolver.resolve(

                        context.level(),

                        context.origin(),

                        entry

                );

        // TODO: Change how this works, this isnt correct as of now
//        if (!CollisionChecker.canPlace(
//
//                placementContext,
//                schematic,
//                placementPos
//
//        )) {
//            return false;
//        }

        PlacementEngine.place(

                placementContext,
                schematic,
                placementPos

        );

        return true;
    }

}