package de.dertoaster.schematicworldgen.placement;

import de.dertoaster.schematicworldgen.placement.context.PlacementContext;
import de.dertoaster.schematicworldgen.placement.context.ProcessorContext;
import de.dertoaster.schematicworldgen.placement.processor.ProcessorPipeline;
import de.dertoaster.schematicworldgen.placement.transform.PlacementTransform;
import de.dertoaster.schematicworldgen.placement.transform.ReplaceModeHandler;
import de.dertoaster.schematicworldgen.schematic.ILoadedSchematic;
import de.dertoaster.schematicworldgen.schematic.internal.PackedPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Core schematic placement engine.
 *
 * Responsible for:
 * - coordinate transforms
 * - processor execution
 * - final block placement
 */
public final class PlacementEngine {

    private PlacementEngine() {
    }

    /**
     * Places a schematic into the world.
     *
     * @param context placement context
     * @param schematic schematic
     * @param origin world origin
     */
    public static void place(

            PlacementContext context,

            ILoadedSchematic schematic,

            BlockPos origin

    ) {

        short[] paletteIds =
                schematic.paletteIds();

        int[] packedPositions =
                schematic.packedPositions();

        BlockState[] palette =
                schematic.palette();

        ProcessorContext processorContext =
                new ProcessorContext(

                        context.level(),
                        context.random(),
                        context.entry()

                );

        BlockPos.MutableBlockPos mutablePos =
                new BlockPos.MutableBlockPos();

        for (int i = 0; i < paletteIds.length; i++) {

            int packed =
                    packedPositions[i];

            int x =
                    PackedPosition.unpackX(packed);

            int y =
                    PackedPosition.unpackY(packed);

            int z =
                    PackedPosition.unpackZ(packed);

            BlockPos transformed =
                    PlacementTransform.transform(

                            x,
                            y,
                            z,

                            schematic.sizeX(),
                            schematic.sizeZ(),

                            context.rotation(),
                            context.mirror()

                    );

            mutablePos.set(

                    origin.getX()
                            + transformed.getX(),

                    origin.getY()
                            + transformed.getY(),

                    origin.getZ()
                            + transformed.getZ()

            );

            BlockState state =
                    palette[paletteIds[i]];

            state =
                    ProcessorPipeline.process(

                            context.processors(),

                            processorContext,

                            mutablePos,

                            state

                    );

            if (state == null) {
                continue;
            }

            if (!ReplaceModeHandler.shouldPlace(
                    context.entry().replaceMode(),
                    context.level().getBlockState(mutablePos),
                    state
            )) {
                continue;
            }

            context.level().setBlock(

                    mutablePos,

                    state,

                    2 | 16

            );
        }
    }
}