package de.dertoaster.schematicworldgen.schematic.internal;

/**
 * Utility for compact 3D coordinate packing.
 *
 * Uses:
 * 10 bits X
 * 10 bits Y
 * 10 bits Z
 *
 * Total:
 * 30 bits packed into one integer.
 *
 * Coordinate range:
 * 0 - 1023
 *
 * This is sufficient for almost all schematic use cases.
 */
public class PackedPosition {

    private PackedPosition() {
    }

    /**
     * Packs x/y/z coordinates into a single integer.
     *
     * @param x local x coordinate
     * @param y local y coordinate
     * @param z local z coordinate
     * @return packed coordinate
     */
    public static int pack(int x, int y, int z) {
       return (x & 0x3ff) | ((y & 0x3ff) << 10) | ((z & 0x3ff) << 20);
    }

    /**
     * Extracts x coordinate from packed integer.
     *
     * @param packed packed coordinate
     * @return unpacked x
     */
    public static int unpackX(int packed) {
        return packed & 0x3FF;
    }

    /**
     * Extracts y coordinate from packed integer.
     *
     * @param packed packed coordinate
     * @return unpacked y
     */
    public static int unpackY(int packed) {
        return (packed >> 10) & 0x3FF;
    }

    /**
     * Extracts z coordinate from packed integer.
     *
     * @param packed packed coordinate
     * @return unpacked z
     */
    public static int unpackZ(int packed) {
        return (packed >> 20) & 0x3FF;
    }

}
