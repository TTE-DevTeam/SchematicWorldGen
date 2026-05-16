package de.dertoaster.schematicworldgen.placement;

import de.dertoaster.schematicworldgen.placement.context.PlacementContext;
import de.dertoaster.schematicworldgen.schematic.ILoadedSchematic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.List;

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
        short[] paletteIds = schematic.paletteIds();
        long[] packedPositions = schematic.packedPositions();
        BlockState[] palette = schematic.palette();

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        List<StructureTemplate.StructureBlockInfo> structureBlockInfos = new ArrayList<>(paletteIds.length);
        List<StructureTemplate.StructureBlockInfo> worldBlockInfos = new ArrayList<>(paletteIds.length);

        for (int i = 0; i < paletteIds.length; i++) {
            long packed = packedPositions[i];
            BlockPos unpacked = BlockPos.of(packed);
            BlockPos transformed = StructureTemplate.transform(unpacked, context.settings().getMirror(), context.settings().getRotation(), BlockPos.ZERO).offset(origin);
            mutablePos.set(transformed);
            BlockState state = palette[paletteIds[i]].mirror(context.settings().getMirror()).rotate(context.settings().getRotation());

            final BlockPos pos = mutablePos.immutable();
            CompoundTag nbt = null;
            if (context.level().getBlockEntity(pos) != null) {
                nbt = context.level().getBlockEntity(pos).getUpdateTag(context.level().registryAccess());
            }
            StructureTemplate.StructureBlockInfo worldInfo = new StructureTemplate.StructureBlockInfo(pos, context.level().getBlockState(pos), nbt);

            StructureTemplate.StructureBlockInfo structureInfo = new StructureTemplate.StructureBlockInfo(pos, state, schematic.blockEntities().getOrDefault(i, null));
            for (StructureProcessor processor : context.settings().getProcessors()) {
                structureInfo = processor.processBlock(context.level(), origin, pos, worldInfo, structureInfo, context.settings());
                if (structureInfo == null) {
                    break;
                }
            }

            if (structureInfo != null) {
                structureBlockInfos.add(structureInfo);
                worldBlockInfos.add(worldInfo);
            }
        }

        for (StructureProcessor processor : context.settings().getProcessors()) {
            structureBlockInfos = processor.finalizeProcessing(context.level(), origin, BlockPos.ZERO, worldBlockInfos, structureBlockInfos, context.settings());
        }

        if (!structureBlockInfos.isEmpty()) {
            for (StructureTemplate.StructureBlockInfo info : structureBlockInfos) {
                // TODO: Block Entities
                context.level().setBlock(info.pos(), info.state(), 2 | 16);
            }
        }
    }
}