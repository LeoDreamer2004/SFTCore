package org.leodreamer.sftcore.common.item.behavior.wildcard.gui;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.behavior.wildcard.feature.IWildcardIOComponent;
import org.leodreamer.sftcore.common.item.behavior.wildcard.impl.SimpleIOComponent;
import org.leodreamer.sftcore.common.item.behavior.wildcard.impl.TagPrefixIOComponent;
import org.leodreamer.sftcore.common.item.behavior.wildcard.impl.WildcardPatternLogic;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@DataGenScanned
public class WildcardIOFancyConfigurator implements IFancyUIProvider {

    private final WildcardPatternLogic logic;
    private final WildcardPatternLogic.IO io;
    private final List<IWildcardIOComponent> components;
    private final Consumer<ItemStack> onChange;

    private WidgetGroup group;
    private static final int LINE_HEIGHT = 25;
    private static final int MAX_COMPONENTS = 6;

    public WildcardIOFancyConfigurator(
        WildcardPatternLogic logic,
        WildcardPatternLogic.IO io,
        Consumer<ItemStack> onChange
    ) {
        this.logic = logic;
        this.io = io;
        var comps = logic.getComponents(io);
        this.onChange = onChange;

        List<IWildcardIOComponent> components = comps == null ? new ArrayList<>() : new ArrayList<>(comps);
        this.components = components.size() < MAX_COMPONENTS ? components : components.subList(0, MAX_COMPONENTS);
    }

    @RegisterLanguage("Save")
    private static final String SAVE = "sftcore.item.wildcard_pattern.io.save";

    @RegisterLanguage("Single")
    private static final String CREATE_SINGLE = "sftcore.item.wildcard_pattern.io.single";

    @RegisterLanguage("Tag")
    private static final String CREATE_TAG_PREFIX = "sftcore.item.wildcard_pattern.io.tag_prefix";

    @RegisterLanguage("Create an ingredient with a fixed item")
    private static final String CREATE_SINGLE_TOOLTIP = "sftcore.item.wildcard_pattern.io.single.tooltip";

    @RegisterLanguage("Create an ingredient with the GT tag")
    private static final String CREATE_TAG_PREFIX_TOOLTIP = "sftcore.item.wildcard_pattern.io.tag_prefix.tooltip";

    @RegisterLanguage("Delete this component")
    private static final String DELETE_TOOLTIP = "sftcore.item.wildcard_pattern.io.delete.tooltip";

    @Override
    public Widget createMainPage(FancyMachineUIWidget ui) {
        var global = new WidgetGroup(0, 0, 158, 180);
        group = new WidgetGroup(0, 0, 158, 0);
        refresh();

        var saveBtn = createBottomBtn(Component.translatable(SAVE), 0, this::onSave);
        var createSimple = createBottomBtn(Component.translatable(CREATE_SINGLE), 1, (cd) -> {
            components.add(SimpleIOComponent.EMPTY);
            refresh();
        }).setHoverTooltips(Component.translatable(CREATE_SINGLE_TOOLTIP));
        var createTagPrefix = createBottomBtn(Component.translatable(CREATE_TAG_PREFIX), 2, (cd) -> {
            components.add(TagPrefixIOComponent.EMPTY);
            refresh();
        }).setHoverTooltips(Component.translatable(CREATE_TAG_PREFIX_TOOLTIP));

        global.addWidget(group);
        global.addWidget(saveBtn);
        global.addWidget(createSimple);
        global.addWidget(createTagPrefix);
        return global;
    }

    @Override
    public IGuiTexture getTabIcon() {
        return new TextTexture(io.name());
    }

    @Override
    public Component getTitle() {
        return Component.literal(io.key);
    }

    private ButtonWidget createBottomBtn(Component label, int idx, Consumer<ClickData> onPressed) {
        return new ButtonWidget(
            2 + 35 * idx, 155, 30, 20, new GuiTextureGroup(
                ResourceBorderTexture.BUTTON_COMMON.copy(), new TextTexture(label.getString())
            ), onPressed
        );
    }

    private void refresh() {
        int len = components.size();
        group.setSizeHeight(10 + len * LINE_HEIGHT);
        group.clearAllWidgets();

        for (int i = 0; i < len; i++) {
            var component = components.get(i);
            var lineGroup = new WidgetGroup(0, 3 + i * LINE_HEIGHT, 156, LINE_HEIGHT);
            component.createUILine(lineGroup);
            int finalI = i;
            var deleteBtn = new ButtonWidget(138, 5, 14, 14, cd -> {
                components.remove(finalI);
                refresh();
            }).setButtonTexture(GuiTextures.CLOSE_ICON)
                .setHoverTooltips(Component.translatable(DELETE_TOOLTIP));
            lineGroup.addWidget(deleteBtn);
            group.addWidget(lineGroup);
        }
    }

    private void onSave(ClickData cd) {
        var stack = logic.setComponents(io, components);
        components.forEach(IWildcardIOComponent::onSave);
        SFTCore.LOGGER.info("Saved the new wildcard pattern with NBT: {}", stack.getTag());
        onChange.accept(stack);
    }
}
