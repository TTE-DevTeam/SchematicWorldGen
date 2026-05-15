package de.dertoaster.schematicworldgen.feature.processor;

import de.dertoaster.schematicworldgen.placement.processor.IPlacementProcessor;
import de.dertoaster.schematicworldgen.placement.processor.PersistentLeavesProcessor;
import de.dertoaster.schematicworldgen.placement.processor.WaterloggingProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Global placement processor registry.
 */
public final class ProcessorRegistry {

    private static final Map<String,
            Supplier<IPlacementProcessor>>
            PROCESSORS =
            new HashMap<>();

    static {

        register(
                "waterlogging",
                WaterloggingProcessor::new
        );

        register(
                "persistent_leaves",
                PersistentLeavesProcessor::new
        );
    }

    private ProcessorRegistry() {
    }

    /**
     * Registers a processor factory.
     *
     * @param id processor id
     * @param supplier processor supplier
     */
    public static void register(

            String id,

            Supplier<IPlacementProcessor> supplier

    ) {

        PROCESSORS.put(id, supplier);
    }

    /**
     * Creates a processor instance.
     *
     * @param id processor id
     * @return processor instance
     */
    public static IPlacementProcessor create(
            String id
    ) {

        Supplier<IPlacementProcessor> supplier =
                PROCESSORS.get(id);

        if (supplier == null) {

            throw new IllegalStateException(
                    "Unknown processor: " + id
            );
        }

        return supplier.get();
    }
}