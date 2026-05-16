package de.dertoaster.schematicworldgen.feature;

import com.mojang.serialization.Codec;
import de.dertoaster.schematicworldgen.feature.config.ConfigurableLargeDripstoneConfiguration;
import de.dertoaster.schematicworldgen.feature.util.ConfigurableDristponeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class ConfigurableLargeDripstoneFeature extends Feature<ConfigurableLargeDripstoneConfiguration> {
    public ConfigurableLargeDripstoneFeature(Codec<ConfigurableLargeDripstoneConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<ConfigurableLargeDripstoneConfiguration> context) {
        WorldGenLevel worldGenLevel = context.level();
        BlockPos blockPos = context.origin();
        ConfigurableLargeDripstoneConfiguration largeDripstoneConfiguration = (ConfigurableLargeDripstoneConfiguration)context.config();
        RandomSource randomSource = context.random();
        if (!ConfigurableDristponeUtils.isEmptyOrWater(worldGenLevel, blockPos)) {
            return false;
        } else {
            Optional<Column> optional = Column.scan(worldGenLevel, blockPos, largeDripstoneConfiguration.floorToCeilingSearchRange(), ConfigurableDristponeUtils::isEmptyOrWater, (state) -> {
                return ConfigurableDristponeUtils.isDripstoneBaseOrLava(state, largeDripstoneConfiguration);
            });
            if (!optional.isEmpty() && optional.get() instanceof Column.Range) {
                Column.Range range = (Column.Range)optional.get();
                if (range.height() < 4) {
                    return false;
                } else {
                    int i = (int)((float)range.height() * largeDripstoneConfiguration.maxColumnRadiusToCaveHeightRatio());
                    int i1 = Mth.clamp(i, largeDripstoneConfiguration.columnRadius().getMinValue(), largeDripstoneConfiguration.columnRadius().getMaxValue());
                    int i2 = Mth.randomBetweenInclusive(randomSource, largeDripstoneConfiguration.columnRadius().getMinValue(), i1);
                    LargeDripstone largeDripstone = makeDripstone(blockPos.atY(range.ceiling() - 1), false, randomSource, i2, largeDripstoneConfiguration.stalactiteBluntness(), largeDripstoneConfiguration.heightScale(), largeDripstoneConfiguration);
                    LargeDripstone largeDripstone1 = makeDripstone(blockPos.atY(range.floor() + 1), true, randomSource, i2, largeDripstoneConfiguration.stalagmiteBluntness(), largeDripstoneConfiguration.heightScale(), largeDripstoneConfiguration);
                    WindOffsetter windOffsetter;
                    if (largeDripstone.isSuitableForWind(largeDripstoneConfiguration) && largeDripstone1.isSuitableForWind(largeDripstoneConfiguration)) {
                        windOffsetter = new WindOffsetter(blockPos.getY(), randomSource, largeDripstoneConfiguration.windSpeed());
                    } else {
                        windOffsetter = WindOffsetter.noWind();
                    }

                    boolean flag = largeDripstone.moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(worldGenLevel, windOffsetter);
                    boolean flag1 = largeDripstone1.moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(worldGenLevel, windOffsetter);
                    if (flag) {
                        largeDripstone.placeBlocks(worldGenLevel, randomSource, windOffsetter);
                    }

                    if (flag1) {
                        largeDripstone1.placeBlocks(worldGenLevel, randomSource, windOffsetter);
                    }

                    return true;
                }
            } else {
                return false;
            }
        }
    }

    private static LargeDripstone makeDripstone(BlockPos root, boolean pointingUp, RandomSource random, int radius, FloatProvider bluntnessBase, FloatProvider scaleBase, ConfigurableLargeDripstoneConfiguration config) {
        return new LargeDripstone(root, pointingUp, radius, (double)bluntnessBase.sample(random), (double)scaleBase.sample(random), config);
    }

    static final class LargeDripstone {
        private BlockPos root;
        private final boolean pointingUp;
        private int radius;
        private final double bluntness;
        private final double scale;
        private final ConfigurableLargeDripstoneConfiguration configuration;

        LargeDripstone(BlockPos root, boolean pointingUp, int radius, double bluntness, double scale, ConfigurableLargeDripstoneConfiguration configuration) {
            this.root = root;
            this.pointingUp = pointingUp;
            this.radius = radius;
            this.bluntness = bluntness;
            this.scale = scale;
            this.configuration = configuration;
        }

        private int getHeight() {
            return this.getHeightAtRadius(0.0F);
        }

        private int getMinY() {
            return this.pointingUp ? this.root.getY() : this.root.getY() - this.getHeight();
        }

        private int getMaxY() {
            return !this.pointingUp ? this.root.getY() : this.root.getY() + this.getHeight();
        }

        boolean moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(WorldGenLevel level, WindOffsetter windOffsetter) {
            while(this.radius > 1) {
                BlockPos.MutableBlockPos mutableBlockPos = this.root.mutable();
                int min = Math.min(10, this.getHeight());

                for(int i = 0; i < min; ++i) {
                    if (level.getBlockState(mutableBlockPos).is(Blocks.LAVA)) {
                        return false;
                    }

                    if (ConfigurableDristponeUtils.isCircleMostlyEmbeddedInStone(level, windOffsetter.offset(mutableBlockPos), this.radius)) {
                        this.root = mutableBlockPos;
                        return true;
                    }

                    mutableBlockPos.move(this.pointingUp ? Direction.DOWN : Direction.UP);
                }

                this.radius /= 2;
            }

            return false;
        }

        private int getHeightAtRadius(float radius) {
            return (int)ConfigurableDristponeUtils.getDripstoneHeight((double)radius, (double)this.radius, this.scale, this.bluntness);
        }

        void placeBlocks(WorldGenLevel level, RandomSource random, WindOffsetter windOffsetter) {
            for(int i = -this.radius; i <= this.radius; ++i) {
                for(int i1 = -this.radius; i1 <= this.radius; ++i1) {
                    float squareRoot = Mth.sqrt((float)(i * i + i1 * i1));
                    if (!(squareRoot > (float)this.radius)) {
                        int heightAtRadius = this.getHeightAtRadius(squareRoot);
                        if (heightAtRadius > 0) {
                            if ((double)random.nextFloat() < 0.2) {
                                heightAtRadius = (int)((float)heightAtRadius * Mth.randomBetween(random, 0.8F, 1.0F));
                            }

                            BlockPos.MutableBlockPos mutableBlockPos = this.root.offset(i, 0, i1).mutable();
                            boolean flag = false;
                            int i2 = this.pointingUp ? level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, mutableBlockPos.getX(), mutableBlockPos.getZ()) : Integer.MAX_VALUE;

                            for(int i3 = 0; i3 < heightAtRadius && mutableBlockPos.getY() < i2; ++i3) {
                                BlockPos blockPos = windOffsetter.offset(mutableBlockPos);
                                if (ConfigurableDristponeUtils.isEmptyOrWaterOrLava(level, blockPos)) {
                                    flag = true;
                                    BlockState block = configuration.fullBlock().getState(random, blockPos);
                                    level.setBlock(blockPos, block, 2);
                                } else if (flag && level.getBlockState(blockPos).is(configuration.surroundingMaterialStopTag())) {
                                    break;
                                }

                                mutableBlockPos.move(this.pointingUp ? Direction.UP : Direction.DOWN);
                            }
                        }
                    }
                }
            }

        }

        boolean isSuitableForWind(ConfigurableLargeDripstoneConfiguration config) {
            return this.radius >= config.minRadiusForWind() && this.bluntness >= (double)config.minBluntnessForWind();
        }
    }

    static final class WindOffsetter {
        private final int originY;
        private final @Nullable Vec3 windSpeed;

        WindOffsetter(int originY, RandomSource random, FloatProvider magnitude) {
            this.originY = originY;
            float f = magnitude.sample(random);
            float f1 = Mth.randomBetween(random, 0.0F, (float)Math.PI);
            this.windSpeed = new Vec3((double)(Mth.cos((double)f1) * f), (double)0.0F, (double)(Mth.sin((double)f1) * f));
        }

        private WindOffsetter() {
            this.originY = 0;
            this.windSpeed = null;
        }

        static WindOffsetter noWind() {
            return new WindOffsetter();
        }

        BlockPos offset(BlockPos pos) {
            if (this.windSpeed == null) {
                return pos;
            } else {
                int i = this.originY - pos.getY();
                Vec3 vec3 = this.windSpeed.scale((double)i);
                return pos.offset(Mth.floor(vec3.x), 0, Mth.floor(vec3.z));
            }
        }
    }
}
