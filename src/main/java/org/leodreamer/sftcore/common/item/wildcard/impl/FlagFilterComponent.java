package org.leodreamer.sftcore.common.item.wildcard.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.wildcard.WildcardCodecUtils;
import org.leodreamer.sftcore.common.item.wildcard.feature.IWildcardFilterComponent;
import org.leodreamer.sftcore.integration.ae2.gui.PhantomGTMaterialSlot;
import org.leodreamer.sftcore.mixin.gregtech.data.MaterialFlagsAccessor;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlag;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.SelectorWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@DataGenScanned
public class FlagFilterComponent implements IWildcardFilterComponent {

    public static final Codec<FlagFilterComponent> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            WildcardCodecUtils.MATERIAL_FLAG_CODEC.optionalFieldOf("flag")
                .forGetter(component -> Optional.ofNullable(component.flag)),
            WildcardCodecUtils.MATERIAL_CODEC.optionalFieldOf("example", GTMaterials.NULL)
                .forGetter(component -> component.example),
            Codec.BOOL.optionalFieldOf("whitelist", false)
                .forGetter(component -> component.whitelist)
        ).apply(instance, (flag, example, whitelist) -> new FlagFilterComponent(flag.orElse(null), example, whitelist))
    );

    private Material example;

    @Nullable
    private MaterialFlag flag;

    @Getter
    private boolean whitelist;

    private PhantomGTMaterialSlot exampleSlot;
    private SelectorWidget flagSelector;
    private WidgetGroup parent = null;

    private static final IGuiTexture GROUP_BG_WHITE = ResourceBorderTexture.BUTTON_COMMON.copy()
        .setColor(ColorPattern.PINK.color);

    private static final IGuiTexture GROUP_BG_BLACK = ResourceBorderTexture.BUTTON_COMMON.copy()
        .setColor(ColorPattern.PURPLE.color);

    public static FlagFilterComponent empty() {
        return new FlagFilterComponent(null, GTMaterials.NULL, false);
    }

    public FlagFilterComponent(@Nullable MaterialFlag flag, Material example, boolean whitelist) {
        this.example = example;
        this.flag = flag;
        this.whitelist = whitelist;
    }

    @Override
    public void setWhitelist(boolean whiteList) {
        this.whitelist = whiteList;

        if (parent != null) {
            parent.setBackground(whiteList ? GROUP_BG_WHITE : GROUP_BG_BLACK);
        }
    }

    @Override
    public boolean test(Material material) {
        if (flag == null) {
            return true;
        }

        return whitelist == material.hasFlag(flag);
    }

    @Override
    public void createUILine(WidgetGroup line) {
        line.setBackground(whitelist ? GROUP_BG_WHITE : GROUP_BG_BLACK);
        parent = line;

        exampleSlot = new PhantomGTMaterialSlot(new CustomItemStackHandler(), 0, 3, 3, this::changeExample);

        flagSelector = new MySelectorWidget(25, 5, 80, 15, getMaterialFlagNames(example));
        flagSelector.setOnChanged(this::updateFlag);

        if (example != GTMaterials.NULL) {
            exampleSlot.setMaterial(example);
        }

        if (flag != null) {
            flagSelector.setValue(flag.toString());
        }

        line.addWidget(exampleSlot);
        line.addWidget(flagSelector);
    }

    @RegisterLanguage("Flag")
    private static final String FLAG_TOOLTIP_KEY = "item.sftcore.wildcard_pattern.tooltip.filter.flag";

    @Override
    public Component createTooltip() {
        return whitelistTooltip().append(" ").append(Component.translatable(FLAG_TOOLTIP_KEY)).append(" ")
            .append(
                flag == null ? Component.translatable(NO_FLAG).withStyle(ChatFormatting.RED) :
                    Component.literal(flag.toString()).withStyle(ChatFormatting.RED)
            );
    }

    private boolean changeExample(Material material) {
        var ok = material != GTMaterials.NULL;

        if (ok) {
            this.example = material;

            var flags = getMaterialFlagNames(material);
            flagSelector.setCandidates(flags);
            flagSelector.setValue(flags.get(0));
            updateFlag(flags.get(0));
        }

        return ok;
    }

    private void updateFlag(String flagName) {
        if (flagName == null || flagName.isEmpty() || flagName.equals(Component.translatable(NO_FLAG).getString())) {
            flag = null;
            return;
        }

        flag = MaterialFlag.getByName(flagName);
    }

    @Override
    public void onSave() {
        example = exampleSlot.getMaterial();
        updateFlag(flagSelector.getValue());
    }

    @RegisterLanguage("no flag")
    private static final String NO_FLAG = "item.sftcore.wildcard_pattern.filter.flag.no_flag";

    private static List<String> getMaterialFlagNames(Material material) {
        var flags = ((MaterialFlagsAccessor) material.getFlags()).getFlags();

        if (flags.isEmpty()) {
            return List.of(Component.translatable(NO_FLAG).getString());
        }

        return flags.stream().map(MaterialFlag::toString).toList();
    }

    private static class MySelectorWidget extends SelectorWidget {

        public MySelectorWidget(int x, int y, int width, int height, List<String> candidates) {
            super(x, y, width, height, candidates, ColorPattern.WHITE.color);
            button.setBackground(GuiTextures.BUTTON);
            textTexture.setColor(ColorPattern.DARK_GRAY.color);
        }
    }
}
