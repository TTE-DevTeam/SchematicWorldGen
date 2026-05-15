package de.dertoaster.schematicworldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Environment restrictions for placement.
 */
public record SpawnConditions(

        boolean onSolidGround,

        boolean inAir,

        boolean underwater,

        boolean onWater,

        boolean underLava,

        boolean onLava

) {

    public static final Codec<SpawnConditions>
            CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(

                            Codec.BOOL.optionalFieldOf(
                                    "on_solid_ground",
                                    true
                            ).forGetter(
                                    SpawnConditions::onSolidGround
                            ),

                            Codec.BOOL.optionalFieldOf(
                                    "in_air",
                                    false
                            ).forGetter(
                                    SpawnConditions::inAir
                            ),

                            Codec.BOOL.optionalFieldOf(
                                    "underwater",
                                    false
                            ).forGetter(
                                    SpawnConditions::underwater
                            ),

                            Codec.BOOL.optionalFieldOf(
                                    "on_water",
                                    false
                            ).forGetter(
                                    SpawnConditions::onWater
                            ),

                            Codec.BOOL.optionalFieldOf(
                                    "under_lava",
                                    false
                            ).forGetter(
                                    SpawnConditions::underLava
                            ),

                            Codec.BOOL.optionalFieldOf(
                                    "on_lava",
                                    false
                            ).forGetter(
                                    SpawnConditions::onLava
                            )

                    ).apply(
                            instance,
                            SpawnConditions::new
                    )
            );
}
