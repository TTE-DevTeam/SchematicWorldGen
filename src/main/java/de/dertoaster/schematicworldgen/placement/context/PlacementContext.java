package de.dertoaster.schematicworldgen.placement.context;

import de.dertoaster.schematicworldgen.feature.config.SchematicEntry;
import de.dertoaster.schematicworldgen.placement.processor.IPlacementProcessor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

import java.util.List;

/**
 * Immutable placement execution context.
 *
 * Contains all runtime information required
 * for schematic placement.
 */
public record PlacementContext(

        WorldGenLevel level,

        RandomSource random,

        SchematicEntry entry,

        Rotation rotation,

        Mirror mirror,

        List<IPlacementProcessor> processors

) {
}