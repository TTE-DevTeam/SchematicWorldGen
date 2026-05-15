package de.dertoaster.schematicworldgen.placement.processor;

import de.dertoaster.schematicworldgen.placement.context.ProcessorContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Placement processor interface.
 *
 * Processors may:
 * - modify block states
 * - replace blocks
 * - skip blocks
 * - apply contextual transformations
 *
 * Returning:
 * - null -> skip block placement
 * - state -> place transformed state
 */
public interface IPlacementProcessor {

    /**
     * Processes a block before placement.
     *
     * @param context processor context
     * @param worldPos target world position
     * @param state original block state
     * @return transformed block state or null
     */
    @Nullable
    BlockState process(
            ProcessorContext context,
            BlockPos worldPos,
            BlockState state
    );
}
