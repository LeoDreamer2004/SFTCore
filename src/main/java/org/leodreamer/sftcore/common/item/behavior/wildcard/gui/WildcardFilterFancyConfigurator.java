package org.leodreamer.sftcore.common.item.behavior.wildcard.gui;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.behavior.wildcard.WildcardPatternLogic;
import org.leodreamer.sftcore.common.item.behavior.wildcard.feature.IWildcardFilterComponent;
import org.leodreamer.sftcore.common.item.behavior.wildcard.impl.SimpleFilterComponent;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib.gui.texture.*;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import java.util.function.Consumer;

@DataGenScanned
public class WildcardFilterFancyConfigurator implements IFancyUIProvider {

    private final WildcardPatternLogic logic;
    private final Consumer<ItemStack> onSave;

    private WildcardComponentListGroup<IWildcardFilterComponent> componentList;

    public WildcardFilterFancyConfigurator(
        WildcardPatternLogic logic,
        Consumer<ItemStack> onSave
    ) {
        this.logic = logic;
        this.onSave = onSave;
    }

    @RegisterLanguage("Material Filter Configuration")
    private static final String TITLE = "sftcore.item.wildcard_pattern.filter.title";

    @RegisterLanguage("Save")
    private static final String SAVE = "sftcore.item.wildcard_pattern.filter.save";

    @RegisterLanguage("Single")
    private static final String CREATE_SINGLE = "sftcore.item.wildcard_pattern.filter.single";

    @RegisterLanguage("Create a filter of a fixed material")
    private static final String CREATE_SINGLE_TOOLTIP = "sftcore.item.wildcard_pattern.filter.single.tooltip";

    @RegisterLanguage("Delete this filter")
    private static final String DELETE_TOOLTIP = "sftcore.item.wildcard_pattern.filter.delete.tooltip";

    @RegisterLanguage("Toggle the filter to a whitelist")
    private static final String TO_WHITELIST = "sftcore.item.wildcard_pattern.filter.to_whitelist";

    @RegisterLanguage("Toggle the filter to a blacklist")
    private static final String TO_BLACKLIST = "sftcore.item.wildcard_pattern.filter.to_blacklist";

    @Override
    public Widget createMainPage(FancyMachineUIWidget ui) {
        var global = new WidgetGroup(0, 0, 158, 180);

        componentList = new WildcardComponentListGroup<>(
            logic.getFilterComponents(), 0, 0, 158, 0
        );

        componentList.setLineStyle(
            (i, group) -> {
                group.addWidget(
                    new ButtonWidget(138, 5, 14, 14, cd -> componentList.removeComponent(i))
                        .setBackground(GuiTextures.BUTTON, GuiTextures.CLOSE_ICON)
                        .setHoverTooltips(Component.translatable(DELETE_TOOLTIP))
                );
                var component = componentList.getComponents().get(i);
                group.addWidget(createWhitelistButton(component));
            }
        );

        var saveBtn = createBottomBtn(Component.translatable(SAVE), 126, cd -> save());
        var createSimple = createBottomBtn(Component.translatable(CREATE_SINGLE), 2, (cd) -> {
            componentList.addComponent(SimpleFilterComponent.empty());
        }).setHoverTooltips(Component.translatable(CREATE_SINGLE_TOOLTIP));

        global.addWidget(componentList);
        global.addWidget(saveBtn);
        global.addWidget(createSimple);
        return global;
    }

    @Override
    public IGuiTexture getTabIcon() {
        return new ItemStackTexture(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Aluminium));
    }

    @Override
    public Component getTitle() {
        return Component.translatable(TITLE);
    }

    private ButtonWidget createBottomBtn(Component label, int x, Consumer<ClickData> onPressed) {
        return new ButtonWidget(
            x, 155, 30, 20, new GuiTextureGroup(
                ResourceBorderTexture.BUTTON_COMMON.copy(), new TextTexture(label.getString())
            ), onPressed
        );
    }

    private ButtonWidget createWhitelistButton(IWildcardFilterComponent component) {
        var toggle = new ButtonWidget(120, 5, 14, 14, cd -> {});
        toggle.setOnPressCallback(cd -> {
            boolean cur = component.isWhitelist();
            component.setWhitelist(!cur);
            setButtonWhitelist(toggle, !cur);
        });
        component.setWhitelist(false);
        setButtonWhitelist(toggle, false);
        return toggle;
    }

    private void setButtonWhitelist(ButtonWidget button, boolean whitelist) {
        button.setBackground(GuiTextures.BUTTON, new TextTexture(whitelist ? "W" : "B"));
        button
            .setHoverTooltips(whitelist ? Component.translatable(TO_BLACKLIST) : Component.translatable(TO_WHITELIST));
    }

    private void save() {
        var components = componentList.getComponents();
        var stack = logic.setFilterComponents(components);
        components.forEach(IWildcardFilterComponent::onSave);
        onSave.accept(stack);
    }
}
