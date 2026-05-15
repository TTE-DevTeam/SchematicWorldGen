package de.dertoaster.schematicworldgen.feature;

import de.dertoaster.schematicworldgen.feature.processor.SchematicProcessors;
import de.dertoaster.schematicworldgen.placement.processor.PersistentLeavesProcessor;
import de.dertoaster.schematicworldgen.placement.processor.WaterloggingProcessor;

/**
 * Central bootstrap for schematic system.
 *
 * Called once during mod initialization.
 */
public final class SchematicFeatureBootstrap {

    private SchematicFeatureBootstrap() {
    }

    public static void init() {

        // Built-in processors
        SchematicProcessors.register(
                "waterlogging",
                WaterloggingProcessor::new
        );

        SchematicProcessors.register(
                "persistent_leaves",
                PersistentLeavesProcessor::new
        );
    }
}
