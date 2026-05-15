package de.dertoaster.schematicworldgen.placement.context;

import de.dertoaster.schematicworldgen.feature.config.SchematicEntry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;

/**
 * Shared processor execution context.
 *
 * Contains:
 * - target world
 * - random source
 * - active schematic entry
 */
public record ProcessorContext(

        WorldGenLevel level,

        RandomSource random,

        SchematicEntry entry

) {
}
