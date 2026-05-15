package de.dertoaster.schematicworldgen.schematic.format.bo2;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sk89q.worldedit.world.registry.LegacyMapper;
import de.dertoaster.schematicworldgen.schematic.ILoadedSchematic;
import de.dertoaster.schematicworldgen.schematic.internal.PackedLoadedSchematic;
import de.dertoaster.schematicworldgen.schematic.internal.PackedPosition;
import de.dertoaster.schematicworldgen.schematic.internal.PaletteBuilder;
import de.dertoaster.schematicworldgen.schematic.loader.ISchematicLoader;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            // BO2 can be integer values!
            final BlockState state = this.loadBlockState(split[1]);
            if (state == null)
                continue;

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

    static final String legacyIdRegex = "^\\d+(\\.\\d+)?$";
    static final Pattern pattern = Pattern.compile(legacyIdRegex, Pattern.MULTILINE);

    private BlockState loadBlockState(String blockData) {
        final Matcher matcher = pattern.matcher(blockData);
        if (matcher.find()) {
            // Legacy format
            int block;
            int data = 0;
            if (blockData.indexOf('.') > 0) {
                String[] split = blockData.split("\\.");
                try {
                    block = Integer.parseInt(split[0]);
                    data = Integer.parseInt(split[1]);
                } catch(Exception ex) {
                    return null;
                }
            } else {
                try {
                    block = Integer.parseInt(blockData);
                } catch(Exception ex) {
                    return null;
                }
            }
            // We abuse FAWE
            com.sk89q.worldedit.world.block.BlockState weState = LegacyMapper.getInstance().getBlockFromLegacy(block, data);
            try {
                return BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK, weState.toString(), true).blockState();
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
                return Blocks.AIR.defaultBlockState();
            }
        } else {
            // Modern
            Identifier blockId =
                    Identifier.parse(
                            blockData
                    );

            Optional<Holder.Reference<Block>> optBlockReference =
                    BuiltInRegistries.BLOCK
                            .get(blockId);
            if (optBlockReference.isEmpty())
                return null;

            Block block = optBlockReference.get().value();
            if (block == null)
                return null;

            BlockState state =
                    block.defaultBlockState();
            return state;
        }
    }
}
