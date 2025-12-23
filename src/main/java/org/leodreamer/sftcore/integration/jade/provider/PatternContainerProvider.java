package org.leodreamer.sftcore.integration.jade.provider;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.util.RLUtils;

import com.gregtechceu.gtceu.integration.jade.GTElementHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.helpers.patternprovider.PatternContainer;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.fluid.JadeFluidObject;

@DataGenScanned
public class PatternContainerProvider implements
    IBlockComponentProvider,
    IServerDataProvider<BlockAccessor> {

    private static final String ITEM_PATTERNS = "ae_patterns_item";
    private static final String FLUID_PATTERNS = "ae_patterns_fluid";
    private static final int MAX_DISPLAY = 6;

    public static final PatternContainerProvider INSTANCE = new PatternContainerProvider();

    @RegisterLanguage("Pattern Providers")
    static final String JADE_CONFIG = "config.jade.plugin_sftcore.pattern_container_provider";

    @Override
    public ResourceLocation getUid() {
        return SFTCore.id("pattern_container_provider");
    }

    @RegisterLanguage("Patterns: ")
    private static final String TOOLTIP_PATTERNS = "sftcore.jade.ae2.pattern_provider.patterns";

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag tag = accessor.getServerData();
        if (!tag.contains(ITEM_PATTERNS)) return;

        var itemPatterns = tag.getList(ITEM_PATTERNS, Tag.TAG_STRING);
        var fluidPatterns = tag.getList(FLUID_PATTERNS, Tag.TAG_STRING);

        if (itemPatterns.isEmpty() && fluidPatterns.isEmpty()) return;

        tooltip.add(Component.translatable(TOOLTIP_PATTERNS));

        for (var pattern : itemPatterns) {
            var item = RLUtils.getItemByName(pattern.getAsString());
            if (item == null) continue;
            var icon = tooltip.getElementHelper().smallItem(new ItemStack(item));
            tooltip.append(icon);
        }

        for (var pattern : fluidPatterns) {
            var fluid = RLUtils.getFluidByName(pattern.getAsString());
            if (fluid == null) continue;
            var icon = GTElementHelper.smallFluid(JadeFluidObject.of(fluid));
            tooltip.append(icon);
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof PatternContainer container) {
            var itemPatterns = new ListTag();
            var fluidPatterns = new ListTag();
            int cnt = 0;
            for (var stack : container.getTerminalPatternInventory()) {
                var detail = PatternDetailsHelper.decodePattern(stack, accessor.getLevel());
                if (detail == null) continue;

                var key = detail.getOutputs()[0].what();
                var id = key.getId().toString();
                if (key instanceof AEItemKey) {
                    itemPatterns.add(StringTag.valueOf(id));
                } else if (key instanceof AEFluidKey) {
                    fluidPatterns.add(StringTag.valueOf(id));
                }
                if (++cnt >= MAX_DISPLAY) break;
            }
            tag.put(ITEM_PATTERNS, itemPatterns);
            tag.put(FLUID_PATTERNS, fluidPatterns);
        }
    }
}
