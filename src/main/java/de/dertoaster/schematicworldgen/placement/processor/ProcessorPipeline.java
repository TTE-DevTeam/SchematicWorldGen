package de.dertoaster.schematicworldgen.placement.processor;

import de.dertoaster.schematicworldgen.placement.context.ProcessorContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Executes placement processors sequentially.
 */
public final class ProcessorPipeline {

    private ProcessorPipeline() {
    }

    /**
     * Applies all processors to a block state.
     *
     * @param processors processor list
     * @param context processor context
     * @param worldPos target position
     * @param state initial state
     * @return final state or null
     */
    @Nullable
    public static BlockState process(

            List<IPlacementProcessor> processors,

            ProcessorContext context,

            BlockPos worldPos,

            BlockState state

    ) {

        BlockState current = state;

        for (IPlacementProcessor processor
                : processors) {

            current =
                    processor.process(
                            context,
                            worldPos,
                            current
                    );

            if (current == null) {
                return null;
            }
        }

        return current;
    }
}