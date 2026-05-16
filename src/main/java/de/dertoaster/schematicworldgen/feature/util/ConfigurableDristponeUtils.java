package de.dertoaster.schematicworldgen.feature.util;

import de.dertoaster.schematicworldgen.feature.config.ConfigurableLargeDripstoneConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;

import java.util.function.Consumer;

public class ConfigurableDristponeUtils {

    public static double getDripstoneHeight(double radius, double maxRadius, double scale, double minRadius) {
        if (radius < minRadius) {
            radius = minRadius;
        }

        double d = 0.384;
        double d1 = radius / maxRadius * 0.384;
        double d2 = (double)0.75F * Math.pow(d1, 1.3333333333333333);
        double d3 = Math.pow(d1, 0.6666666666666666);
        double d4 = 0.3333333333333333 * Math.log(d1);
        double d5 = scale * (d2 - d3 - d4);
        d5 = Math.max(d5, (double)0.0F);
        return d5 / 0.384 * maxRadius;
    }

    public static boolean isCircleMostlyEmbeddedInStone(WorldGenLevel level, BlockPos pos, int radius) {
        if (isEmptyOrWaterOrLava(level, pos)) {
            return false;
        } else {
            float f = 6.0F;
            float f1 = 6.0F / (float)radius;

            for(float f2 = 0.0F; f2 < ((float)Math.PI * 2F); f2 += f1) {
                int i = (int)(Mth.cos((double)f2) * (float)radius);
                int i1 = (int)(Mth.sin((double)f2) * (float)radius);
                if (isEmptyOrWaterOrLava(level, pos.offset(i, 0, i1))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isEmptyOrWater(LevelAccessor level, BlockPos pos) {
        return level.isStateAtPosition(pos, ConfigurableDristponeUtils::isEmptyOrWater);
    }

    public static boolean isEmptyOrWaterOrLava(LevelAccessor level, BlockPos pos) {
        return level.isStateAtPosition(pos, ConfigurableDristponeUtils::isEmptyOrWaterOrLava);
    }

    protected static void buildBaseToTipColumn(Direction direction, int height, boolean mergeTip, Consumer<BlockState> blockSetter) {
        if (height >= 3) {
            blockSetter.accept(createPointedDripstone(direction, DripstoneThickness.BASE));

            for(int i = 0; i < height - 3; ++i) {
                blockSetter.accept(createPointedDripstone(direction, DripstoneThickness.MIDDLE));
            }
        }

        if (height >= 2) {
            blockSetter.accept(createPointedDripstone(direction, DripstoneThickness.FRUSTUM));
        }

        if (height >= 1) {
            blockSetter.accept(createPointedDripstone(direction, mergeTip ? DripstoneThickness.TIP_MERGE : DripstoneThickness.TIP));
        }

    }

    protected static void growPointedDripstone(LevelAccessor level, BlockPos pos, Direction direction, int height, boolean mergeTip, ConfigurableLargeDripstoneConfiguration config) {
        if (isDripstoneBase(level.getBlockState(pos.relative(direction.getOpposite())), config)) {
            BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();
            buildBaseToTipColumn(direction, height, mergeTip, (blockState) -> {
                if (blockState.is(Blocks.POINTED_DRIPSTONE)) {
                    blockState = (BlockState)blockState.setValue(PointedDripstoneBlock.WATERLOGGED, level.isWaterAt(mutableBlockPos));
                }

                level.setBlock(mutableBlockPos, blockState, 2);
                mutableBlockPos.move(direction);
            });
        }

    }

    protected static boolean placeDripstoneBlockIfPossible(LevelAccessor level, BlockPos pos, ConfigurableLargeDripstoneConfiguration config) {
        BlockState blockState = level.getBlockState(pos);
        if (blockState.is(config.replacableBlocks())) {
            level.setBlock(pos, Blocks.DRIPSTONE_BLOCK.defaultBlockState(), 2);
            return true;
        } else {
            return false;
        }
    }

    private static BlockState createPointedDripstone(Direction direction, DripstoneThickness dripstoneThickness) {
        return (BlockState)((BlockState)Blocks.POINTED_DRIPSTONE.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, direction)).setValue(PointedDripstoneBlock.THICKNESS, dripstoneThickness);
    }

    public static boolean isDripstoneBaseOrLava(BlockState state, ConfigurableLargeDripstoneConfiguration config) {
        return isDripstoneBase(state, config) || state.is(Blocks.LAVA);
    }

    public static boolean isDripstoneBase(BlockState state, ConfigurableLargeDripstoneConfiguration config) {
        return state.is(Blocks.DRIPSTONE_BLOCK) || state.is(config.replacableBlocks());
    }

    public static boolean isEmptyOrWater(BlockState state) {
        return state.isAir() || state.is(Blocks.WATER);
    }

    public static boolean isNeitherEmptyNorWater(BlockState state) {
        return !state.isAir() && !state.is(Blocks.WATER);
    }

    public static boolean isEmptyOrWaterOrLava(BlockState state) {
        return state.isAir() || state.is(Blocks.WATER) || state.is(Blocks.LAVA);
    }

}
