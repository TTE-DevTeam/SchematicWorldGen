package de.dertoaster.schematicworldgen.placement.transform;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

/**
 * Utility for schematic coordinate transformations.
 *
 * Handles:
 * - rotation
 * - mirroring
 */
public final class PlacementTransform {

    private PlacementTransform() {
    }

    /**
     * Transforms local schematic coordinates.
     *
     * @param x local x
     * @param y local y
     * @param z local z
     * @param sizeX schematic width
     * @param sizeZ schematic length
     * @param rotation rotation
     * @param mirror mirror
     * @return transformed local position
     */
    public static BlockPos transform(

            int x,
            int y,
            int z,

            int sizeX,
            int sizeZ,

            Rotation rotation,
            Mirror mirror

    ) {

        if (mirror == Mirror.FRONT_BACK) {
            z = sizeZ - 1 - z;
        }

        if (mirror == Mirror.LEFT_RIGHT) {
            x = sizeX - 1 - x;
        }

        return switch (rotation) {

            case NONE ->
                    new BlockPos(x, y, z);

            case CLOCKWISE_90 ->
                    new BlockPos(
                            sizeZ - 1 - z,
                            y,
                            x
                    );

            case CLOCKWISE_180 ->
                    new BlockPos(
                            sizeX - 1 - x,
                            y,
                            sizeZ - 1 - z
                    );

            case COUNTERCLOCKWISE_90 ->
                    new BlockPos(
                            z,
                            y,
                            sizeX - 1 - x
                    );
        };
    }
}