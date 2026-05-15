package de.dertoaster.schematicworldgen.placement.processor;

import de.dertoaster.schematicworldgen.placement.context.ProcessorContext;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Automatically adapts waterlogged states
 * to the target environment.
 */
public final class WaterloggingProcessor
        implements IPlacementProcessor {

    @Override
    public BlockState process(

            ProcessorContext context,

            BlockPos worldPos,

            BlockState state

    ) {

        if (!state.hasProperty(
                BlockStateProperties.WATERLOGGED
        )) {
            return state;
        }

        boolean waterlogged =
                context.level()
                        .getFluidState(worldPos)
                        .is(FluidTags.WATER);

        return state.setValue(
                BlockStateProperties.WATERLOGGED,
                waterlogged
        );
    }
}