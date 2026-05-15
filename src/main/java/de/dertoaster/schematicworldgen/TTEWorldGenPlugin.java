package de.dertoaster.schematicworldgen;

import de.dertoaster.schematicworldgen.feature.SchematicFeatureBootstrap;
import org.bukkit.plugin.java.JavaPlugin;

public class TTEWorldGenPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        super.onLoad();

        SchematicFeatureBootstrap.init();
    }

}
