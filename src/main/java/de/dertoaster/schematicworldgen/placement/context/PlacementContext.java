package de.dertoaster.schematicworldgen.placement.context;

import de.dertoaster.schematicworldgen.feature.config.SchematicEntry;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

/**
 * Immutable placement execution context.
 *
 * Contains all runtime information required
 * for schematic placement.
 */
public record PlacementContext(

        WorldGenLevel level,

        SchematicEntry entry,

        StructurePlaceSettings settings

) {
}