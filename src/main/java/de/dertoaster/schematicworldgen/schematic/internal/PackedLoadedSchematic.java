package de.dertoaster.schematicworldgen.schematic.internal;

import de.dertoaster.schematicworldgen.schematic.ILoadedSchematic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Compact immutable schematic implementation.
 *
 * Uses:
 * - packed primitive arrays
 * - block state palette
 * - sparse block entity storage
 *
 * This structure is designed for:
 * - minimal GC pressure
 * - cache-friendly iteration
 * - fast world generation
 */
public record PackedLoadedSchematic(
        int sizeX,
        int sizeY,
        int sizeZ,
        BlockState[] palette,
        short[] paletteIds,
        long[] packedPositions,
        Int2ObjectMap<CompoundTag> blockEntities
) implements ILoadedSchematic {
    @Override
    public int blockCount() {
        return paletteIds.length;
    }

}
