package de.dertoaster.schematicworldgen.schematic;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Immutable internal representation of a loaded schematic.
 *
 * Implementations are expected to:
 * - be fully thread-safe
 * - contain no mutable collections
 * - avoid object-heavy structures
 * - store only non-air blocks
 *
 * Storage is palette-based for memory efficiency.
 */
public interface ILoadedSchematic {

    /**
     * @return schematic width
     */
    int sizeX();

    /**
     * @return schematic height
     */
    int sizeY();

    /**
     * @return schematic length
     */
    int sizeZ();

    /**
     * @return total amount of non-air blocks
     */
    int blockCount();

    /**
     * Palette containing all unique block states.
     *
     * Palette indices are referenced by {@link #paletteIds()}.
     *
     * @return immutable block state palette
     */
    BlockState[] palette();

    /**
     * Palette ids for all stored blocks.
     *
     * Index aligned with {@link #packedPositions()}.
     *
     * @return palette id array
     */
    short[] paletteIds();

    /**
     * Packed positions for all stored blocks.
     *
     * Index aligned with {@link #paletteIds()}.
     *
     * @return packed position array
     */
    int[] packedPositions();

    /**
     * Sparse block entity storage.
     *
     * Key = block index
     * Value = block entity NBT
     *
     * Only blocks with actual block entity data are stored.
     *
     * @return immutable block entity map
     */
    Int2ObjectMap<CompoundTag> blockEntities();
}
