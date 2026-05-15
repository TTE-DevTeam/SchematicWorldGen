package de.dertoaster.schematicworldgen.feature.config;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/**
 * Controls how the schematic Y position is resolved.
 */
public enum EPlacementMode
        implements StringRepresentable {

    /**
     * Uses terrain heightmap placement.
     */
    TERRAIN("terrain"),

    /**
     * Uses a fixed Y level.
     */
    FIXED_HEIGHT("fixed_height");

    public static final Codec<EPlacementMode>
            CODEC =
            StringRepresentable.fromEnum(
                    EPlacementMode::values
            );

    private final String serializedName;

    EPlacementMode(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}