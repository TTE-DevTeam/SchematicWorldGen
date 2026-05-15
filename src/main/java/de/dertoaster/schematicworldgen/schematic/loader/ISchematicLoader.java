package de.dertoaster.schematicworldgen.schematic.loader;

import de.dertoaster.schematicworldgen.schematic.ILoadedSchematic;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.InputStream;

/**
 * Responsible for reading external schematic formats
 * and converting them into the internal representation.
 *
 * Examples:
 * - .schem
 * - .schematic
 * - .bo2
 * - .bo3
 */
public interface ISchematicLoader {

    /**
     * Checks whether this loader supports the given file.
     *
     * @param file resource location
     * @return true if supported
     */
    boolean supports(
            Identifier file
    );

    /**
     * Loads and converts the schematic.
     *
     * @param file resource location
     * @param stream file input stream
     * @return loaded immutable schematic
     * @throws IOException if loading fails
     */
    ILoadedSchematic load(
            Identifier file,
            InputStream stream
    ) throws IOException;
}