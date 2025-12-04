package org.leodreamer.sftcore.common.data;

import com.gregtechceu.gtceu.common.data.models.GTModels;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;
import org.leodreamer.sftcore.api.wrapper.TooltipedBlockItem;

import java.util.function.Supplier;

import static org.leodreamer.sftcore.SFTCore.REGISTRATE;

public final class SFTBlocks {

    public static final BlockEntry<Block> MULTI_FUNCTIONAL_CASING = createCasingBlock(
            "multi_functional_casing",
            SFTCore.id("block/casings/solid/multi_functional_casing"),
            SFTTooltipsBuilder.of().textureComeFrom("GregTech Odyssey").all().toArray(new Component[0])
    );

    public static BlockEntry<Block> createCasingBlock(String name, ResourceLocation texture, Component... tooltips) {
        return createCasingBlock(name, Block::new, texture, () -> Blocks.IRON_BLOCK,
                () -> RenderType::solid, tooltips);
    }

    public static BlockEntry<Block> createCasingBlock(
            String name,
            NonNullFunction<BlockBehaviour.Properties, Block> blockSupplier,
            ResourceLocation texture,
            NonNullSupplier<? extends Block> properties,
            Supplier<Supplier<RenderType>> type,
            Component... tooltips
    ) {
        return REGISTRATE.block(name, blockSupplier)
                .initialProperties(properties)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(type)
                .exBlockstate(GTModels.cubeAllModel(texture))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item((block, property) -> new TooltipedBlockItem(block, property, tooltips))
                .build()
                .register();
    }

    public static void init() {
    }
}
