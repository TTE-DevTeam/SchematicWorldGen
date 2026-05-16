package de.dertoaster.schematicworldgen.feature.registry;

import de.dertoaster.schematicworldgen.feature.ConfigurableLargeDripstoneFeature;
import de.dertoaster.schematicworldgen.feature.SchematicFeature;
import de.dertoaster.schematicworldgen.feature.config.ConfigurableLargeDripstoneConfiguration;
import de.dertoaster.schematicworldgen.feature.config.SchematicFeatureConfig;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.function.Supplier;

/**
 * Holds feature instance before registry bootstrap.
 *
 * Avoids static init order issues.
 */
public final class SchematicFeatures {

    public static final Supplier<Feature<SchematicFeatureConfig>>
            SCHEMATIC_FEATURE =
            () -> new SchematicFeature(
                    SchematicFeatureConfig.CODEC
            );

    public static final Supplier<Feature<ConfigurableLargeDripstoneConfiguration>>
            CONFIGURABLE_LARGE_DRIPSTONE =
            () -> new ConfigurableLargeDripstoneFeature(
                    ConfigurableLargeDripstoneConfiguration.CODEC
            );

    private SchematicFeatures() {
    }
}
