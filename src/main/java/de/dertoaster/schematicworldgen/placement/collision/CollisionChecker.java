package de.dertoaster.schematicworldgen.placement.collision;

import de.dertoaster.schematicworldgen.feature.config.ECollisionMode;
import de.dertoaster.schematicworldgen.feature.resolver.SpawnConditionEvaluator;
import de.dertoaster.schematicworldgen.placement.context.PlacementContext;
import de.dertoaster.schematicworldgen.schematic.ILoadedSchematic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * Validates whether a schematic can be placed
 * at the target location.
 */
public final class CollisionChecker {

    private CollisionChecker() {
    }

    /**
     * Validates placement collisions.
     *
     * @param context placement context
     * @param schematic schematic
     * @param origin world origin
     * @return true if placement is allowed
     */
    public static boolean canPlace(
            PlacementContext context,
            ILoadedSchematic schematic,
            BlockPos origin
    ) {

        if (!SpawnConditionEvaluator.canSpawn(
                context.level(),
                origin,
                context.entry()
        )) {
            return false;
        }

        ECollisionMode mode =
                context.entry()
                        .collisionMode();

        if (mode == ECollisionMode.NONE) {
            return true;
        }

        short[] paletteIds =
                schematic.paletteIds();

        long[] packedPositions =
                schematic.packedPositions();

        var palette =
                schematic.palette();

        for (int i = 0; i < paletteIds.length; i++) {
            long packed = packedPositions[i];
            BlockPos unpacked = BlockPos.of(packed);

            BlockPos worldPos = StructureTemplate.transform(unpacked, context.settings().getMirror(), context.settings().getRotation(), BlockPos.ZERO).offset(origin);

            BlockState existing =
                    context.level()
                            .getBlockState(worldPos);

            switch (mode) {

                case SOLID_BLOCKS -> {

                    if (!existing.isAir()
                            && existing.isSolid()) {

                        return false;
                    }
                }

                case ANY_BLOCK -> {

                    if (!existing.isAir()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
