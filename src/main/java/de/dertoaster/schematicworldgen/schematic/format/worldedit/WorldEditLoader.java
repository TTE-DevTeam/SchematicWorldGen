package de.dertoaster.schematicworldgen.schematic.format.worldedit;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

import de.dertoaster.schematicworldgen.schematic.ILoadedSchematic;
import de.dertoaster.schematicworldgen.schematic.internal.PackedLoadedSchematic;
import de.dertoaster.schematicworldgen.schematic.internal.PackedPosition;
import de.dertoaster.schematicworldgen.schematic.internal.PaletteBuilder;
import de.dertoaster.schematicworldgen.schematic.loader.ISchematicLoader;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class WorldEditLoader
        implements ISchematicLoader {

    @Override
    public boolean supports(
            Identifier file
    ) {

        String path = file.getPath();

        return path.endsWith(".schem")
                || path.endsWith(".schematic");
    }

    @Override
    public ILoadedSchematic load(
            Identifier file,
            InputStream stream
    ) throws IOException {

        ClipboardFormat format =
                ClipboardFormats.findByAlias(
                        file.getPath()
                );

        if (format == null) {

            throw new IOException(
                    "Unknown schematic format"
            );
        }

        try (ClipboardReader reader =
                     format.getReader(stream)) {

            Clipboard clipboard =
                    reader.read();

            return convertClipboard(
                    clipboard
            );
        }
    }

    private ILoadedSchematic convertClipboard(
            Clipboard clipboard
    ) {

        Region region =
                clipboard.getRegion();

        PaletteBuilder palette =
                new PaletteBuilder();

        List<Integer> positions =
                new ArrayList<>();

        List<Short> paletteIds =
                new ArrayList<>();

        Int2ObjectOpenHashMap<CompoundTag>
                blockEntities =
                new Int2ObjectOpenHashMap<>();

        for (BlockVector3 pos : region) {

            var weState =
                    clipboard.getBlock(pos);

            String stateString =
                    weState.getAsString();

            BlockState state =
                    BlockStateParser.parseForBlock(
                            BuiltInRegistries.BLOCK.asLookup(),
                            stateString,
                            true
                    ).blockState();

            if (state.isAir()) {
                continue;
            }

            short paletteId =
                    palette.idFor(state);

            positions.add(
                    PackedPosition.pack(
                            pos.x(),
                            pos.y(),
                            pos.z()
                    )
            );

            paletteIds.add(paletteId);
        }

        short[] ids =
                new short[paletteIds.size()];

        int[] packed =
                new int[positions.size()];

        for (int i = 0; i < ids.length; i++) {

            ids[i] = paletteIds.get(i);
            packed[i] = positions.get(i);
        }

        return new PackedLoadedSchematic(

                region.getWidth(),
                region.getHeight(),
                region.getLength(),

                palette.build(),

                ids,

                packed,

                blockEntities
        );
    }
}
