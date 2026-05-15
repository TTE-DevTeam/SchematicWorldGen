package de.dertoaster.schematicworldgen;

import de.dertoaster.schematicworldgen.feature.ResourceReloadHandler;
import de.dertoaster.schematicworldgen.feature.registry.SchematicFeatures;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

public class TTEWorldGenPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        super.onLoad();

        MinecraftServer server = ((CraftServer) this.getServer()).getServer();
        ReloadableResourceManager reloadableResourceManager = (ReloadableResourceManager) server.getResourceManager();
        reloadableResourceManager.registerReloadListener(new ResourceReloadHandler());

        Registry.register(
                BuiltInRegistries.FEATURE,
                Identifier.fromNamespaceAndPath("schematicworldgen", "schematic"),
                SchematicFeatures.SCHEMATIC_FEATURE.get()
        );
    }

}
