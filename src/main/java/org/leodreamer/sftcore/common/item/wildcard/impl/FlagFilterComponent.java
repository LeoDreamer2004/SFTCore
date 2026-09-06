package org.leodreamer.sftcore.common.item.wildcard.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.wildcard.feature.WildcardFilterComponent;
import org.leodreamer.sftcore.common.item.wildcard.handler.GTFlagHandler;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlag;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import brachy.modularui.drawable.Rectangle;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widgets.layout.Flow;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@DataGenScanned
public class FlagFilterComponent extends WildcardFilterComponent {

    public static final Codec<FlagFilterComponent> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            GTFlagHandler.MATERIAL_FLAG_CODEC.optionalFieldOf("flag")
                .forGetter(component -> Optional.ofNullable(component.flag)),
            GTFlagHandler.MATERIAL_CODEC.optionalFieldOf("example", Optional.empty())
                .forGetter(component -> Optional.ofNullable(component.example)),
            Codec.BOOL.optionalFieldOf("whitelist", false)
                .forGetter(component -> component.whitelist)
        ).apply(
            instance,
            (flag, example, whitelist) -> new FlagFilterComponent(flag.orElse(null), example.orElse(null), whitelist)
        )
    );

    @Getter
    @Nullable
    private Material example;
    @Getter
    @Nullable
    private MaterialFlag flag;
    private GTFlagHandler exampleSlot;
    private String detail;
    private static final int GROUP_BG_WHITE = 0xFFFF33FF;
    private static final int GROUP_BG_BLACK = 0xFF9933FF;

    @RegisterLanguage("Flag")
    public static final String LABEL = "item.sftcore.wildcard_pattern.ui.filter.flag";

    public static FlagFilterComponent empty() {
        return new FlagFilterComponent(null, null, false);
    }

    public FlagFilterComponent(@Nullable MaterialFlag flag, @Nullable Material example, boolean whitelist) {
        super(whitelist);
        this.example = example;
        this.flag = flag;
        this.detail = flag == null ? "" : flag.toString();
    }

    @Override
    public boolean test(Material material) {
        if (flag == null) {
            return true;
        }

        return whitelist == material.hasFlag(flag);
    }

    @Override
    protected void addLineContent(
        Flow row,
        PanelSyncManager syncManager,
        String lineSyncKey
    ) {
        var sampleSyncHandler = registerSampleSlot(
            new GTFlagHandler(flag, example), GTFlagHandler.class, syncManager, lineSyncKey
        );
        this.exampleSlot = sampleSlotHandler(sampleSyncHandler, GTFlagHandler.class);
        this.exampleSlot.setMaterial(example);
        this.exampleSlot.setFlag(flag);
        row.child(createTypeButton(52, LABEL));
        row.child(createWhitelistButton(row));
        row.child(createSampleSlot(sampleSyncHandler));
        row.child(detailField(() -> detail, text -> detail = text));
    }

    @Override
    public void onSave() {
        if (exampleSlot == null) {
            return;
        }
        exampleSlot.setFlagName(detail);
        example = exampleSlot.getMaterial();
        flag = exampleSlot.getFlag();
    }

    @Override
    public Component createTooltip() {
        return whitelistTooltip().append(" ").append(Component.translatable(LABEL)).append(" ")
            .append(
                flag == null ? Component.translatable(NO_FLAG).withStyle(ChatFormatting.RED) :
                    Component.literal(flag.toString()).withStyle(ChatFormatting.RED)
            );
    }

    @RegisterLanguage("no flag")
    private static final String NO_FLAG = "item.sftcore.wildcard_pattern.filter.flag.no_flag";

    @Override
    protected Rectangle rowBackground() {
        return new Rectangle().color(whitelist ? GROUP_BG_WHITE : GROUP_BG_BLACK);
    }
}
