package de.dertoaster.schematicworldgen.feature.processor;

import de.dertoaster.schematicworldgen.placement.processor.IPlacementProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Global registry for schematic placement processors.
 *
 * Uses string identifiers to allow datapack extensions.
 */
public final class SchematicProcessors {

    private static final Map<String, Supplier<IPlacementProcessor>>
            REGISTRY = new HashMap<>();

    private SchematicProcessors() {
    }

    /**
     * Registers a processor factory.
     */
    public static void register(
            String id,
            Supplier<IPlacementProcessor> factory
    ) {
        REGISTRY.put(id, factory);
    }

    /**
     * Creates a processor instance.
     */
    public static IPlacementProcessor create(String id) {

        Supplier<IPlacementProcessor> factory =
                REGISTRY.get(id);

        if (factory == null) {
            throw new IllegalStateException(
                    "Unknown schematic processor: " + id
            );
        }

        return factory.get();
    }

    /**
     * Clears registry (used during reload).
     */
    public static void clear() {
        REGISTRY.clear();
    }
}
