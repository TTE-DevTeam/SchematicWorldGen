package de.dertoaster.schematicworldgen.feature.config;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/**
 * Controls block replacement behavior.
 */
public enum EReplaceMode
        implements StringRepresentable {

    /**
     * Always replace existing blocks.
     */
    ALWAYS("always"),

    /**
     * Replace only if target block is air.
     */
    IF_TARGET_AIR("if_target_air"),

    /**
     * Replace only if the schematic block is solid.
     */
    IF_STRUCTURE_SOLID("if_structure_solid");

    public static final Codec<EReplaceMode>
            CODEC =
            StringRepresentable.fromEnum(
                    EReplaceMode::values
            );

    private final String serializedName;

    EReplaceMode(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}
