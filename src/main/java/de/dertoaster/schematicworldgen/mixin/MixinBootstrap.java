package de.dertoaster.schematicworldgen.mixin;

import de.dertoaster.schematicworldgen.feature.registry.SchematicFeatures;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Feature.class)
public abstract class MixinBootstrap {

  // RE-register some of the dispense behaviors AFTER bootstrap

  @Inject(
    method = "<clinit>",
    at = @At("TAIL"),
    remap = false
  )
  private static void schematicworldgen$register(
    CallbackInfo callbackInfo
  ) {
      Registry.register(BuiltInRegistries.FEATURE, "schematicworldgen:schematic", SchematicFeatures.SCHEMATIC_FEATURE.get());
  }

}
