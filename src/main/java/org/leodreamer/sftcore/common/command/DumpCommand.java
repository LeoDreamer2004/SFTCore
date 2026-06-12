package org.leodreamer.sftcore.common.command;

import org.leodreamer.sftcore.SFTConfig;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@DataGenScanned
public class DumpCommand {

    public static final String FOLDER = "dumps";
    public static final String ID_FILENAME = "vocabulary.json";
    public static final String MULTI_BLOCK_FILENAME = "multiblock.txt";
    public static final String TRANSLATIONS_FILENAME = "translations.json";

    private static final String ALIASES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz#$%";

    private static final Gson GSON = new GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create();

    private static final Comparator<ResourceLocation> RESOURCE_LOCATION_ORDER = Comparator
        .comparing(ResourceLocation::getNamespace)
        .thenComparing(ResourceLocation::getPath);

    @RegisterLanguage("Start dumping...")
    static final String START = "commands.sftcore.dump.start";

    @RegisterLanguage("Dump finished.")
    static final String SUCCESS = "commands.sftcore.dump.success";

    @RegisterLanguage("[Open the file]")
    static final String LINK = "commands.sftcore.dump.success.link";

    @RegisterLanguage("Dump failed.")
    static final String FAILURE = "commands.sftcore.dump.failure";

    @RegisterLanguage("No complete dump area selected.")
    static final String NO_SELECTION = "commands.sftcore.dump.no_selection";

    @RegisterLanguage("There are too many distinct block types in the selected area.")
    static final String TOO_MANY_BLOCK_TYPES = "commands.sftcore.dump.too_many_block_types";

    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType(
        (limit, actual) -> Component.translatable("commands.fill.toobig", limit, actual)
    );

    private static final SimpleCommandExceptionType ERROR_NO_SELECTION = new SimpleCommandExceptionType(
        Component.translatable(NO_SELECTION)
    );

    private static final SimpleCommandExceptionType ERROR_TOO_MANY_BLOCK_TYPES = new SimpleCommandExceptionType(
        Component.translatable(TOO_MANY_BLOCK_TYPES)
    );

    public static LiteralArgumentBuilder<CommandSourceStack> builder() {
        return Commands.literal("dump")
            .executes(context -> dumpIdentifiers(context.getSource(), Mode.ALL))
            .then(identifierDump("items", Mode.ITEM))
            .then(identifierDump("blocks", Mode.BLOCK))
            .then(identifierDump("fluid", Mode.FLUID))
            .then(
                Commands.literal("translations")
                    .executes(context -> dumpTranslations(context.getSource()))
            )
            .then(
                Commands.literal("multiblock")
                    .executes(context -> dumpMultiblock(context.getSource()))
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> identifierDump(String name, Mode mode) {
        return Commands.literal(name)
            .executes(context -> dumpIdentifiers(context.getSource(), mode));
    }

    private static int dumpIdentifiers(CommandSourceStack stack, Mode mode) {
        return write(stack, ID_FILENAME, getIdentifierString(mode));
    }

    private static int dumpTranslations(CommandSourceStack stack) {
        return write(stack, TRANSLATIONS_FILENAME, getTranslationString());
    }

    private static int dumpMultiblock(CommandSourceStack stack) throws CommandSyntaxException {
        var player = stack.getPlayerOrException();

        var area = SelectedData.getCompleteSelectedArea(player)
            .orElseThrow(ERROR_NO_SELECTION::create);

        var box = BoundingBox.fromCorners(area.pos1, area.pos2);

        int volume = box.getXSpan() * box.getYSpan() * box.getZSpan();
        int limit = stack.getLevel()
            .getGameRules()
            .getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);

        if (volume > limit) {
            throw ERROR_AREA_TOO_LARGE.create(limit, volume);
        }

        return write(stack, MULTI_BLOCK_FILENAME, getMultiblockString(stack, box));
    }

    private static int write(CommandSourceStack stack, String filename, String content) {
        stack.sendSystemMessage(Component.translatable(START));

        try {
            var dir = FMLPaths.GAMEDIR.get().resolve(FOLDER);
            Files.createDirectories(dir);

            var path = dir.resolve(filename);
            Files.writeString(path, content, StandardCharsets.UTF_8);

            var link = Component.translatable(LINK)
                .withStyle(ChatFormatting.UNDERLINE)
                .withStyle(
                    style -> style.withClickEvent(
                        new ClickEvent(
                            ClickEvent.Action.OPEN_FILE,
                            path.toAbsolutePath().toString()
                        )
                    )
                );

            stack.sendSuccess(
                () -> Component.translatable(SUCCESS)
                    .append(Component.literal(" "))
                    .append(link),
                true
            );

            return 1;
        } catch (IOException e) {
            stack.sendFailure(Component.translatable(FAILURE));
            return 0;
        }
    }

    private static String getIdentifierString(Mode mode) {
        var result = new LinkedHashMap<String, Map<String, List<String>>>();

        for (var target : mode.getTargets()) {
            result.put(target.getTypeName(), target.identifiers());
            result.put(target.getTypeName() + "Tags", target.tags());
        }

        if (mode == Mode.ALL && SFTConfig.keepDistinct) {
            // remove block items
            var blockMap = result.get("Block");
            var itemMap = result.get("Item");

            if (blockMap == null || itemMap == null) {
                return GSON.toJson(result);
            }

            for (var entry : blockMap.entrySet()) {
                var itemNames = itemMap.get(entry.getKey());
                if (itemNames == null) {
                    continue;
                }

                var blockNames = new HashSet<>(entry.getValue());
                itemNames.removeIf(blockNames::contains);
            }
        }

        return GSON.toJson(result);
    }

    private static String getTranslationString() {
        var result = new LinkedHashMap<String, String>();

        for (var target : DumpTarget.values()) {
            target.translations().forEach(result::putIfAbsent);
        }

        return GSON.toJson(result);
    }

    private static String getMultiblockString(
        CommandSourceStack stack,
        BoundingBox box
    ) throws CommandSyntaxException {
        var aliases = new LinkedHashMap<String, Character>();
        char[][][] blocks = new char[box.getXSpan()][box.getYSpan()][box.getZSpan()];

        // create an empty block array
        for (int x = 0; x < box.getXSpan(); x++) {
            for (int y = 0; y < box.getYSpan(); y++) {
                for (int z = 0; z < box.getZSpan(); z++) {
                    blocks[x][y][z] = ' ';
                }
            }
        }

        for (
            var pos : BlockPos.betweenClosed(
                box.minX(), box.minY(), box.minZ(),
                box.maxX(), box.maxY(), box.maxZ()
            )
        ) {
            var state = stack.getLevel().getBlockState(pos);

            if (state.isAir()) {
                continue;
            }

            var id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
            if (id == null) {
                continue;
            }

            var blockName = id.toString();
            var alias = aliases.get(blockName);

            if (alias == null) {
                alias = nextAlias(aliases.size());
                aliases.put(blockName, alias);
            }

            // record the block with the alias
            blocks[pos.getX() - box.minX()][pos.getY() - box.minY()][pos.getZ() - box.minZ()] = alias;
        }

        if (aliases.isEmpty()) {
            return "";
        }

        var builder = new StringBuilder();

        // record the alias map
        aliases.forEach(
            (blockName, alias) -> builder.append(alias)
                .append(":\"")
                .append(blockName)
                .append("\",\n")
        );

        builder.setLength(builder.length() - 2);
        builder.append('\n');

        // record the multiblock pattern
        for (int x = 0; x < box.getXSpan(); x++) {
            builder.append('[');

            for (int y = 0; y < box.getYSpan(); y++) {
                builder.append('"');

                for (int z = 0; z < box.getZSpan(); z++) {
                    builder.append(blocks[x][y][z]);
                }

                builder.append("\",");
            }

            builder.setLength(builder.length() - 1);
            builder.append("]\n");
        }

        return builder.toString();
    }

    private static char nextAlias(int index) throws CommandSyntaxException {
        if (index >= ALIASES.length()) {
            throw ERROR_TOO_MANY_BLOCK_TYPES.create();
        }

        return ALIASES.charAt(index);
    }

    private enum Mode {

        ALL(EnumSet.allOf(DumpTarget.class)),
        BLOCK(EnumSet.of(DumpTarget.BLOCK)),
        ITEM(EnumSet.of(DumpTarget.ITEM)),
        FLUID(EnumSet.of(DumpTarget.FLUID));

        @Getter
        private final EnumSet<DumpTarget> targets;

        Mode(EnumSet<DumpTarget> targets) {
            this.targets = targets;
        }
    }

    private enum DumpTarget {

        BLOCK("Block", ForgeRegistries.BLOCKS),
        ITEM("Item", ForgeRegistries.ITEMS),
        FLUID("Fluid", ForgeRegistries.FLUIDS);

        @Getter
        private final String typeName;
        private final IForgeRegistry<?> registry;

        DumpTarget(String typeName, IForgeRegistry<?> registry) {
            this.typeName = typeName;
            this.registry = registry;
        }

        public Map<String, List<String>> identifiers() {
            return groupByNamespace(registry.getKeys().stream(), false);
        }

        public Map<String, List<String>> tags() {
            var tagManager = registry.tags();

            if (tagManager == null) {
                return new LinkedHashMap<>();
            }

            return groupByNamespace(
                tagManager.getTagNames().map(TagKey::location),
                true
            );
        }

        private static Map<String, List<String>> groupByNamespace(
            Stream<ResourceLocation> locations,
            boolean tag
        ) {
            var result = new LinkedHashMap<String, List<String>>();

            locations.sorted(RESOURCE_LOCATION_ORDER)
                .forEach(location -> {
                    var namespace = tag ? "#" + location.getNamespace() : location.getNamespace();

                    result.computeIfAbsent(namespace, ignored -> new ArrayList<>())
                        .add(location.getPath());
                });

            return result;
        }

        public Map<String, String> translations() {
            var result = new LinkedHashMap<String, String>();

            registry.getKeys().stream()
                .sorted(RESOURCE_LOCATION_ORDER)
                .forEach(location -> {
                    var name = getTranslation(location);
                    if (name != null) {
                        result.put(location.toString(), name);
                    }
                });

            return result;
        }

        private String getTranslation(ResourceLocation location) {
            return switch (this) {
                case BLOCK -> {
                    var block = ForgeRegistries.BLOCKS.getValue(location);
                    yield block == null ? null : translate(block.getDescriptionId());
                }
                case ITEM -> {
                    var item = ForgeRegistries.ITEMS.getValue(location);
                    yield item == null ? null : translate(item.getDescriptionId());
                }
                case FLUID -> {
                    var fluid = ForgeRegistries.FLUIDS.getValue(location);
                    yield fluid == null ? null : fluid.getFluidType()
                        .getDescription(new FluidStack(fluid, 1))
                        .getString();
                }
            };
        }

        private static String translate(String key) {
            return Language.getInstance().getOrDefault(key);
        }
    }

    public static class SelectedData {

        private static final Map<UUID, SelectedArea> AREA_MAP = new HashMap<>();

        public static void setSelectedPos1(@NotNull Player player, BlockPos pos) {
            var area = getOrCreate(player);
            area.updateDimension(player);
            area.pos1 = pos.immutable();
        }

        public static void setSelectedPos2(@NotNull Player player, BlockPos pos) {
            var area = getOrCreate(player);
            area.updateDimension(player);
            area.pos2 = pos.immutable();
        }

        public static Optional<SelectedArea> getCompleteSelectedArea(@NotNull Player player) {
            var area = AREA_MAP.get(player.getUUID());
            if (area == null || !area.isComplete() || !area.isInDimension(player.level())) {
                return Optional.empty();
            }
            return Optional.of(area);
        }

        private static SelectedArea getOrCreate(Player player) {
            return AREA_MAP.computeIfAbsent(player.getUUID(), ignored -> new SelectedArea());
        }

        public static final class SelectedArea {

            public BlockPos pos1;
            public BlockPos pos2;
            public ResourceKey<Level> dimension;

            private void updateDimension(Player player) {
                var current = player.level().dimension();
                if (dimension != null && dimension != current) {
                    pos1 = null;
                    pos2 = null;
                }
                dimension = current;
            }

            public boolean isInDimension(Level level) {
                return dimension == null || dimension == level.dimension();
            }

            public boolean isComplete() {
                return pos1 != null && pos2 != null;
            }
        }
    }
}
