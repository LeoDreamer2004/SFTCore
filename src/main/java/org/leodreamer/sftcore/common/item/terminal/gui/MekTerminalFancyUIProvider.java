package org.leodreamer.sftcore.common.item.terminal.gui;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.MekBuilderRegistry;

import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@DataGenScanned
public final class MekTerminalFancyUIProvider implements IFancyUIProvider {

    @RegisterLanguage("Mekanism Terminal")
    public static final String TITLE = "item.sftcore.mek_terminal.title";

    private final MekTerminalTab<?> selectedTab;
    private final List<MekTerminalTab<?>> otherTabs;

    public MekTerminalFancyUIProvider(ItemStack terminal, Consumer<ItemStack> onSave) {
        var selected = MekBuilderRegistry.selected(terminal);
        this.selectedTab = selected.createTab(terminal, onSave);

        this.otherTabs = new ArrayList<>();
        for (var entry : MekBuilderRegistry.entries()) {
            if (!entry.id().equals(selected.id())) {
                otherTabs.add(entry.createTab(terminal, onSave));
            }
        }
    }

    @Override
    public Component getTitle() {
        return Component.translatable(TITLE);
    }

    @Override
    public Widget createMainPage(FancyMachineUIWidget ui) {
        return selectedTab.createMainPage(ui);
    }

    @Override
    public IGuiTexture getTabIcon() {
        return selectedTab.getTabIcon();
    }

    @Override
    public List<Component> getTabTooltips() {
        return List.of(selectedTab.getTitle());
    }

    @Override
    public List<IFancyUIProvider> getSubTabs() {
        return new ArrayList<>(otherTabs);
    }

    @Override
    public void attachSideTabs(TabsWidget sideTabs) {
        sideTabs.setMainTab(this);
        for (var tab : otherTabs) {
            sideTabs.attachSubTab(tab);
        }
    }
}
