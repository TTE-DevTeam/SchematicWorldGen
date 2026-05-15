package de.dertoaster.schematicworldgen.schematic.format.bo2;

import de.dertoaster.schematicworldgen.schematic.ILoadedSchematic;
import de.dertoaster.schematicworldgen.schematic.internal.PackedLoadedSchematic;
import de.dertoaster.schematicworldgen.schematic.internal.PackedPosition;
import de.dertoaster.schematicworldgen.schematic.internal.PaletteBuilder;
import de.dertoaster.schematicworldgen.schematic.loader.ISchematicLoader;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class BO2Loader
        implements ISchematicLoader {

    @Override
    public boolean supports(
            Identifier file
    ) {

        return file.getPath()
                .endsWith(".bo2");
    }

    @Override
    public ILoadedSchematic load(
            Identifier file,
            InputStream stream
    ) throws IOException {

        PaletteBuilder palette =
                new PaletteBuilder();

        List<Integer> positions =
                new ArrayList<>();

        List<Short> paletteIds =
                new ArrayList<>();

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(stream)
                );

        String line;

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        while ((line = reader.readLine()) != null) {

            line = line.trim();

            if (!line.contains(":")) {
                continue;
            }

            String[] split =
                    line.split(":");

            String[] coords =
                    split[0].split(",");

            int x =
                    Integer.parseInt(coords[0]);

            int y =
                    Integer.parseInt(coords[1]);

            int z =
                    Integer.parseInt(coords[2]);

            Identifier blockId =
                    Identifier.parse(
                            split[1]
                    );

            Optional<Holder.Reference<Block>> optBlockReference =
                    BuiltInRegistries.BLOCK
                            .get(blockId);
            if (optBlockReference.isEmpty())
                continue;

            Block block = optBlockReference.get().value();
            if (block == null)
                continue;

            BlockState state =
                    block.defaultBlockState();

            short paletteId =
                    palette.idFor(state);

            positions.add(
                    PackedPosition.pack(
                            x,
                            y,
                            z
                    )
            );

            paletteIds.add(paletteId);

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);

            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
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

                maxX - minX + 1,
                maxY - minY + 1,
                maxZ - minZ + 1,

                palette.build(),

                ids,

                packed,

                new Int2ObjectOpenHashMap<>()
        );
    }
}
