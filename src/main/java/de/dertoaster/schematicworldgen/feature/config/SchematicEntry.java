package de.dertoaster.schematicworldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;

/**
 * Immutable schematic placement entry configuration.
 *
 * Defines:
 * - schematic source
 * - placement behavior
 * - transformations
 * - collision handling
 * - environment restrictions
 * - processor behavior
 */
public record SchematicEntry(

        /**
         * Path to the schematic resource.
         *
         * Examples:
         * - namespace:path/file.schem
         * - namespace:path/file.schematic
         * - namespace:path/file.bo2
         */
        Identifier file,

        /**
         * Additional placement offset.
         */
        Vec3i offset,

        /**
         * Placement mode.
         */
        EPlacementMode placementMode,

        /**
         * Fixed world height.
         *
         * Used when placement mode is FIXED_HEIGHT.
         */
        int fixedY,

        /**
         * Height offset relative to terrain.
         */
        int terrainOffset,

        /**
         * Random vertical offset provider.
         */
        IntProvider randomYOffset,

        /**
         * Whether random rotation is allowed.
         */
        boolean randomRotation,

        /**
         * Whether random mirroring is allowed.
         */
        boolean randomMirror,

        /**
         * Relative selection weight.
         */
        int weight,

        /**
         * Spawn environment restrictions.
         */
        SpawnConditions spawnConditions,

        /**
         * Collision handling mode.
         */
        ECollisionMode collisionMode,

        /**
         * Replacement behavior.
         */
        EReplaceMode replaceMode,

        /**
         * Whether leaves should become persistent.
         */
        boolean persistentLeaves,

        /**
         * Whether waterlogging should adapt.
         */
        boolean adaptWaterlogging,

        /**
         * Whether bottom blocks should extend downward.
         */
        boolean extendFoundation

) {

    /**
     * Datapack codec.
     */
    // TODO: Abstract away placement mode into its own object that then handles all the placement logic!
    public static final Codec<SchematicEntry>
            CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(

                            Identifier.CODEC

                                    .fieldOf("file")

                                    .forGetter(
                                            SchematicEntry::file
                                    ),

                            Vec3i.CODEC

                                    .optionalFieldOf(
                                            "offset",
                                            Vec3i.ZERO
                                    )

                                    .forGetter(
                                            SchematicEntry::offset
                                    ),

                            EPlacementMode.CODEC

                                    .fieldOf(
                                            "placement_mode"
                                    )

                                    .forGetter(
                                            SchematicEntry::placementMode
                                    ),

                            Codec.INT

                                    .optionalFieldOf(
                                            "fixed_y",
                                            64
                                    )

                                    .forGetter(
                                            SchematicEntry::fixedY
                                    ),

                            Codec.INT

                                    .optionalFieldOf(
                                            "terrain_offset",
                                            0
                                    )

                                    .forGetter(
                                            SchematicEntry::terrainOffset
                                    ),

                            IntProvider.CODEC

                                    .optionalFieldOf(
                                            "random_y_offset",
                                            ConstantInt.of(0)
                                    )

                                    .forGetter(
                                            SchematicEntry::randomYOffset
                                    ),

                            Codec.BOOL

                                    .optionalFieldOf(
                                            "random_rotation",
                                            true
                                    )

                                    .forGetter(
                                            SchematicEntry::randomRotation
                                    ),

                            Codec.BOOL

                                    .optionalFieldOf(
                                            "random_mirror",
                                            false
                                    )

                                    .forGetter(
                                            SchematicEntry::randomMirror
                                    ),

                            Codec.INT

                                    .optionalFieldOf(
                                            "weight",
                                            1
                                    )

                                    .forGetter(
                                            SchematicEntry::weight
                                    ),

                            SpawnConditions.CODEC

                                    .fieldOf(
                                            "spawn_conditions"
                                    )

                                    .forGetter(
                                            SchematicEntry::spawnConditions
                                    ),

                            ECollisionMode.CODEC

                                    .fieldOf(
                                            "collision_mode"
                                    )

                                    .forGetter(
                                            SchematicEntry::collisionMode
                                    ),

                            EReplaceMode.CODEC

                                    .fieldOf(
                                            "replace_mode"
                                    )

                                    .forGetter(
                                            SchematicEntry::replaceMode
                                    ),

                            Codec.BOOL

                                    .optionalFieldOf(
                                            "persistent_leaves",
                                            true
                                    )

                                    .forGetter(
                                            SchematicEntry::persistentLeaves
                                    ),

                            Codec.BOOL

                                    .optionalFieldOf(
                                            "adapt_waterlogging",
                                            true
                                    )

                                    .forGetter(
                                            SchematicEntry::adaptWaterlogging
                                    ),

                            Codec.BOOL

                                    .optionalFieldOf(
                                            "extend_foundation",
                                            false
                                    )

                                    .forGetter(
                                            SchematicEntry::extendFoundation
                                    )

                    ).apply(
                            instance,
                            SchematicEntry::new
                    )
            );
}