package de.dertoaster.schematicworldgen.placement.processor;

import de.dertoaster.schematicworldgen.placement.context.ProcessorContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Prevents placed leaves from decaying.
 */
public final class PersistentLeavesProcessor
        implements IPlacementProcessor {

    @Override
    public BlockState process(

            ProcessorContext context,

            BlockPos worldPos,

            BlockState state

    ) {

        if (!state.hasProperty(
                BlockStateProperties.PERSISTENT
        )) {
            return state;
        }

        return state.setValue(
                BlockStateProperties.PERSISTENT,
                true
        );
    }
}