package de.dertoaster.schematicworldgen.feature.resolver;

import de.dertoaster.schematicworldgen.feature.config.SchematicEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.WorldGenLevel;

/**
 * Evaluates whether a schematic is allowed
 * to spawn at a given position.
 */
public final class SpawnConditionEvaluator {

    private SpawnConditionEvaluator() {
    }

    public static boolean canSpawn(
            WorldGenLevel level,
            BlockPos pos,
            SchematicEntry entry
    ) {

        var c = entry.spawnConditions();

        boolean solid = level.getBlockState(pos.below()).isSolid();

        boolean air = level.getBlockState(pos).isAir();

        boolean water = level.getFluidState(pos).is(FluidTags.WATER);

        boolean lava = level.getFluidState(pos).is(FluidTags.LAVA);

        if (c.onSolidGround() && !solid) return false;
        if (c.inAir() && !air) return false;
        if (c.underwater() && !water) return false;
        if (c.onWater() && !water) return false;
        if (c.underLava() && !lava) return false;
        if (c.onLava() && !lava) return false;

        return true;
    }
}
