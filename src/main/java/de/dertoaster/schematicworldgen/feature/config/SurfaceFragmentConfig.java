package de.dertoaster.schematicworldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record SurfaceFragmentConfig(
        // Floodfill params
        int minMaxFloodRadius,
        int maxMaxFloodRadius,
        float floodBoundaryNoiseFrequency,
        // Block filters
        BlockStateProvider blockStateProvider,
        TagKey<Block> blockFilter,
        // Fragment noise parameters
        float fragmentSeparation,
        float fragmentNoiseFrequency,
        float domainWarpFrequency,
        float domainWarpAmplitude,
        // Seeds
        long floodBoundaryNoiseSeedFlip,
        long warpNoiseSeedFlip,
        long cellNoiseSeedFlip
) implements FeatureConfiguration {

    public static final Codec<SurfaceFragmentConfig> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                        Codec.INT.optionalFieldOf("minMaxFloodRadius", 8).forGetter(SurfaceFragmentConfig::minMaxFloodRadius),
                        Codec.INT.optionalFieldOf("maxMaxFloodRadius", 12).forGetter(SurfaceFragmentConfig::maxMaxFloodRadius),
                        Codec.FLOAT.optionalFieldOf("floodBoundaryNoiseFrequency", 1.0F / 10.0F).forGetter(SurfaceFragmentConfig::floodBoundaryNoiseFrequency),
                        BlockStateProvider.CODEC.optionalFieldOf("blockStateProvider", BlockStateProvider.simple(Blocks.ICE)).forGetter(SurfaceFragmentConfig::blockStateProvider),
                        TagKey.codec(Registries.BLOCK).optionalFieldOf("blockFilter", BlockTags.ICE).forGetter(SurfaceFragmentConfig::blockFilter),
                        Codec.FLOAT.optionalFieldOf("fragmentSeparation", 0.15F).forGetter(SurfaceFragmentConfig::fragmentSeparation),
                        Codec.FLOAT.optionalFieldOf("fragmentNoiseFrequency", 1.0F / 12.0F).forGetter(SurfaceFragmentConfig::fragmentNoiseFrequency),
                        Codec.FLOAT.optionalFieldOf("domainWarpFrequency", 1.0F / 12.0F).forGetter(SurfaceFragmentConfig::domainWarpFrequency),
                        Codec.FLOAT.optionalFieldOf("domainWarpAmplitude", 4F).forGetter(SurfaceFragmentConfig::domainWarpAmplitude),
                        Codec.LONG.optionalFieldOf("floodBoundaryNoiseSeedFlip", -0x3086FA4ADCEE58ADL).forGetter(SurfaceFragmentConfig::floodBoundaryNoiseSeedFlip),
                        Codec.LONG.optionalFieldOf("warpNoiseSeedFlip", 0x34E3E6E2D932825FL).forGetter(SurfaceFragmentConfig::warpNoiseSeedFlip),
                        Codec.LONG.optionalFieldOf("cellNoiseSeedFlip", 0x5AAEE84593A93C88L).forGetter(SurfaceFragmentConfig::cellNoiseSeedFlip)
                    ).apply(
                            instance,
                            SurfaceFragmentConfig::new
                    )
            );

    public int effectiveMaxFloodRadius() {
        return maxMaxFloodRadius() - 1;
    }

    public int effectiveMaxFloodDiameter() {
        return effectiveMaxFloodRadius() * 2 + 1;
    }

    public int maxFloodRadiusRange() {
        return maxMaxFloodRadius() - minMaxFloodRadius();
    }

}
