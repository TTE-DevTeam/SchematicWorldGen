package de.dertoaster.schematicworldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import java.util.List;

/**
 * Root feature configuration.
 *
 * Contains:
 * - schematic entries
 * - processor references
 */
public record SchematicFeatureConfig(
        List<SchematicEntry> entries,
        Holder<StructureProcessorList> processors
) implements FeatureConfiguration {

    public static final Codec<SchematicFeatureConfig>
            CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(

                            SchematicEntry.CODEC

                                    .listOf()

                                    .fieldOf("entries")

                                    .forGetter(
                                            SchematicFeatureConfig::entries
                                    ),

                            StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter((config) -> config.processors)

                    ).apply(
                            instance,
                            SchematicFeatureConfig::new
                    )
            );

    /**
     * Selects a weighted random entry.
     *
     * @param random random source
     * @return selected entry
     */
    public SchematicEntry selectRandom(
            RandomSource random
    ) {

        int totalWeight = 0;

        for (SchematicEntry entry
                : entries) {

            totalWeight += entry.weight();
        }

        int randomWeight =
                random.nextInt(totalWeight);

        for (SchematicEntry entry
                : entries) {

            randomWeight -= entry.weight();

            if (randomWeight < 0) {
                return entry;
            }
        }

        return entries.getFirst();
    }
}