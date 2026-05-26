package de.dertoaster.schematicworldgen;

import de.dertoaster.schematicworldgen.feature.registry.SchematicFeatures;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class TTEWorldGenBootstrapper implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        Registry.register(
                BuiltInRegistries.FEATURE,
                Identifier.fromNamespaceAndPath("schematicworldgen", "schematic"),
                SchematicFeatures.SCHEMATIC_FEATURE.get()
        );

        Registry.register(
                BuiltInRegistries.FEATURE,
                Identifier.fromNamespaceAndPath("schematicworldgen", "configurable_large_dripstone"),
                SchematicFeatures.CONFIGURABLE_LARGE_DRIPSTONE.get()
        );

        Registry.register(
                BuiltInRegistries.FEATURE,
                Identifier.fromNamespaceAndPath("schematicworldgen", "surface_fragment"),
                SchematicFeatures.FRAGMENT_SURFACE.get()
        );
    }

}
