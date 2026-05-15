package de.dertoaster.schematicworldgen.feature.config;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/**
 * Collision behavior during placement.
 */
public enum ECollisionMode
        implements StringRepresentable {

    /**
     * Ignore collisions entirely.
     */
    NONE("none"),

    /**
     * Prevent placement into solid blocks.
     */
    SOLID_BLOCKS("solid_blocks"),

    /**
     * Prevent placement into any blocks.
     */
    ANY_BLOCK("any_blocks");

    public static final Codec<ECollisionMode>
            CODEC =
            StringRepresentable.fromEnum(
                    ECollisionMode::values
            );

    private final String serializedName;

    ECollisionMode(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}
