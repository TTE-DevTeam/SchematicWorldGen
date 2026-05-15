package de.dertoaster.schematicworldgen.placement.transform;

import de.dertoaster.schematicworldgen.feature.config.EReplaceMode;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Handles block replacement rules during placement.
 */
public final class ReplaceModeHandler {

    private ReplaceModeHandler() {
    }

    public static boolean shouldPlace(
            EReplaceMode mode,
            BlockState existing,
            BlockState incoming
    ) {

        return switch (mode) {

            case ALWAYS ->
                    true;

            case IF_TARGET_AIR ->
                    existing.isAir();

            case IF_STRUCTURE_SOLID ->
                    incoming.isSolid();
        };
    }
}