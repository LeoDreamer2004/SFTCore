package org.leodreamer.sftcore.common.data;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;
import org.leodreamer.sftcore.common.block.VoidPortalBlock;

import com.gregtechceu.gtceu.common.data.models.GTModels;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import static org.leodreamer.sftcore.SFTCore.REGISTRATE;

public final class SFTBlocks {

    public static final BlockEntry<VoidPortalBlock> VOID_PORTAL = REGISTRATE
        .block("void_portal", VoidPortalBlock::new)
        .initialProperties(() -> Blocks.OBSIDIAN)
        .simpleItem()
        .register();

    public static final BlockEntry<Block> MULTI_FUNCTIONAL_CASING = createCasingBlock(
        "multi_functional_casing",
        SFTCore.id("block/casings/solid/multi_functional_casing"),
        SFTTooltipsBuilder.of().textureComeFrom("GregTech Odyssey").array()
    );

    public static BlockEntry<Block> createCasingBlock(String name, ResourceLocation texture, Component... tooltips) {
        return createCasingBlock(
            name, Block::new, texture, () -> Blocks.IRON_BLOCK, () -> RenderType::solid, tooltips
        );
    }

    public static BlockEntry<Block> createCasingBlock(
        String name,
        NonNullFunction<BlockBehaviour.Properties, Block> blockSupplier,
        ResourceLocation texture,
        NonNullSupplier<? extends Block> properties,
        Supplier<Supplier<RenderType>> type,
        Component... tooltips
    ) {
        return REGISTRATE
            .block(name, blockSupplier)
            .initialProperties(properties)
            .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
            .addLayer(type)
            .exBlockstate(GTModels.cubeAllModel(texture))
            .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
            .item(createTooltipBlockItem(tooltips))
            .build()
            .register();
    }

    private static NonNullBiFunction<Block, Item.Properties, BlockItem> createTooltipBlockItem(Component... tooltips) {
        return (block, properties) -> new BlockItem(block, properties) {

            @Override
            public void appendHoverText(
                @NotNull ItemStack stack,
                @Nullable Level level,
                @NotNull List<Component> tooltip,
                @NotNull TooltipFlag flag
            ) {
                // Add custom tooltips here if needed
                tooltip.addAll(Arrays.asList(tooltips));
                super.appendHoverText(stack, level, tooltip, flag);
            }
        };
    }

    public static void init() {}
}
