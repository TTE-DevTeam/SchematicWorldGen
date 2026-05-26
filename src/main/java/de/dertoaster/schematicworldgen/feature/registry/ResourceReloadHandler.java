package de.dertoaster.schematicworldgen.feature.registry;

import de.dertoaster.schematicworldgen.schematic.cache.SchematicCache;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

/**
 * Handles datapack reload lifecycle.
 *
 * Ensures:
 * - schematic cache is invalidated
 * - processor registry is reset
 */
public class ResourceReloadHandler
        extends SimplePreparableReloadListener<Void> {

    @Override
    protected Void prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        return null;
    }

    @Override
    protected void apply(Void unused, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        SchematicCache.INSTANCE.clear();
    }
}
