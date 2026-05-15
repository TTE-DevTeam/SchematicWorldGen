package de.dertoaster.schematicworldgen.schematic.loader;

import de.dertoaster.schematicworldgen.schematic.format.bo2.BO2Loader;
import de.dertoaster.schematicworldgen.schematic.format.worldedit.WorldEditLoader;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Global schematic loader registry.
 *
 * Responsible for resolving the correct loader
 * based on the file extension.
 */
public final class SchematicLoaders {

    /**
     * Registered schematic loaders.
     */
    private static final List<ISchematicLoader>
            LOADERS =
            List.of(

                    new WorldEditLoader(),
                    new BO2Loader()

            );

    private SchematicLoaders() {
    }

    /**
     * Resolves the appropriate loader.
     *
     * @param file schematic resource
     * @return matching loader
     */
    public static ISchematicLoader find(
            Identifier file
    ) {

        for (ISchematicLoader loader : LOADERS) {

            if (loader.supports(file)) {
                return loader;
            }
        }

        throw new IllegalStateException(
                "No schematic loader found for "
                        + file
        );
    }
}
