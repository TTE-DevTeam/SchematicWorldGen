package de.dertoaster.schematicworldgen.schematic.internal;

import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a compact block state palette.
 *
 * Assigns a unique short id to each unique block state.
 *
 * Intended for one-time schematic compilation.
 */
public final class PaletteBuilder {

    /**
     * Maps block states to palette ids.
     */
    private final Object2ShortMap<BlockState>
            ids =
            new Object2ShortOpenHashMap<>();

    /**
     * Ordered palette state list.
     */
    private final List<BlockState>
            states =
            new ArrayList<>();

    /**
     * Gets or creates a palette id for the given state.
     *
     * @param state block state
     * @return palette id
     */
    public short idFor(
            BlockState state
    ) {

        short existing =
                ids.getOrDefault(
                        state,
                        (short) -1
                );

        if (existing != -1) {
            return existing;
        }

        short id =
                (short) states.size();

        states.add(state);

        ids.put(state, id);

        return id;
    }

    /**
     * Builds the final immutable palette array.
     *
     * @return palette array
     */
    public BlockState[] build() {

        return states.toArray(
                BlockState[]::new
        );
    }
}
