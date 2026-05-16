package de.dertoaster.schematicworldgen.feature;

import com.mojang.serialization.Codec;
import de.dertoaster.schematicworldgen.feature.config.SchematicEntry;
import de.dertoaster.schematicworldgen.feature.config.SchematicFeatureConfig;
import de.dertoaster.schematicworldgen.placement.PlacementEngine;
import de.dertoaster.schematicworldgen.placement.context.PlacementContext;
import de.dertoaster.schematicworldgen.schematic.ILoadedSchematic;
import de.dertoaster.schematicworldgen.schematic.cache.SchematicCache;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;

/**
 * Main schematic world generation feature.
 */
public final class SchematicFeature
        extends Feature<SchematicFeatureConfig> {

    public SchematicFeature(
            Codec<SchematicFeatureConfig> codec
    ) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SchematicFeatureConfig> context) {
        RandomSource random = context.random();
        SchematicEntry entry = context.config().selectRandom(random);
        ILoadedSchematic schematic = SchematicCache.INSTANCE.get(entry.file());

        Rotation rotation = entry.randomRotation() ? Rotation.getRandom(random) : Rotation.NONE;
        Mirror mirror = entry.randomMirror() ? random.nextBoolean() ? Mirror.FRONT_BACK : Mirror.LEFT_RIGHT : Mirror.NONE;

        StructurePlaceSettings placeSettings = new StructurePlaceSettings();
        placeSettings.setRandom(random);
        placeSettings.setMirror(mirror);
        placeSettings.setRotation(rotation);
        placeSettings.setRotationPivot(context.origin());
        for (StructureProcessor processor : context.config().processors().value().list()) {
            placeSettings.addProcessor(processor);
        }
        placeSettings.setKnownShape(false);
        PlacementContext placementContext = new PlacementContext(context.level(), entry, placeSettings);

        // DONE: Refactor! searching for a spot to place this thing at is the decision of the placed feature, not from us!
        BlockPos placementPos = context.origin().offset(entry.offset().offset(0, entry.randomYOffset().sample(random), 0));

        // TODO: Change how this works, this isnt correct as of now
//        if (!CollisionChecker.canPlace(
//
//                placementContext,
//                schematic,
//                placementPos
//
//        )) {
//            return false;
//        }

        PlacementEngine.place(placementContext, schematic, placementPos);

        return true;
    }

}