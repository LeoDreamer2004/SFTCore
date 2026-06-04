package org.leodreamer.sftcore.common.item.terminal.gui;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.MekBuilderRegistry;
import org.leodreamer.sftcore.common.item.terminal.api.MekTerminalTab;

import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@DataGenScanned
public final class MekTerminalFancyUIProvider implements IFancyUIProvider {

    @RegisterLanguage("Mekanism Terminal")
    public static final String TITLE = "item.sftcore.mek_terminal.title";

    private final ItemStack stack;
    private final Consumer<ItemStack> onSave;

    public MekTerminalFancyUIProvider(ItemStack stack, Consumer<ItemStack> onSave) {
        this.stack = stack;
        this.onSave = onSave;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(TITLE);
    }

    @Override
    public Widget createMainPage(FancyMachineUIWidget ui) {
        return selectedTab().createMainPage(ui);
    }

    @Override
    public IGuiTexture getTabIcon() {
        return selectedTab().getTabIcon();
    }

    @Override
    public List<Component> getTabTooltips() {
        return List.of(selectedTab().getTitle());
    }

    @Override
    public List<IFancyUIProvider> getSubTabs() {
        return new ArrayList<>(createOtherTabs());
    }

    @Override
    public void attachSideTabs(TabsWidget sideTabs) {
        sideTabs.setMainTab(this);
        for (var tab : createOtherTabs()) {
            sideTabs.attachSubTab(tab);
        }
    }

    private MekTerminalTab selectedTab() {
        return MekBuilderRegistry.selectedTab(stack, onSave);
    }

    private List<MekTerminalTab> createOtherTabs() {
        var selected = MekBuilderRegistry.selected(stack).id();
        var tabs = new ArrayList<MekTerminalTab>();

        for (var entry : MekBuilderRegistry.entries()) {
            if (!entry.builder().id().equals(selected)) {
                tabs.add(entry.tabFactory().create(stack, onSave));
            }
        }

        return tabs;
    }
}
