package de.dertoaster.schematicworldgen.feature.resolver;

import de.dertoaster.schematicworldgen.feature.config.EPlacementMode;
import de.dertoaster.schematicworldgen.feature.config.SchematicEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Resolves final schematic placement positions.
 */
public final class PlacementResolver {

    private PlacementResolver() {
    }

    /**
     * Resolves the final world origin.
     *
     * @param level world
     * @param origin initial origin
     * @param entry schematic config
     * @return final placement position
     */
    public static BlockPos resolve(

            WorldGenLevel level,

            BlockPos origin,

            SchematicEntry entry

    ) {

        int y;

        if (entry.placementMode()
                == EPlacementMode.FIXED_HEIGHT) {

            y = entry.fixedY();

        } else {

            y = level.getHeight(

                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,

                    origin.getX(),
                    origin.getZ()

            );

            y += entry.terrainOffset();
        }

        return new BlockPos(

                origin.getX(),
                y,
                origin.getZ()

        ).offset(entry.offset());
    }
}