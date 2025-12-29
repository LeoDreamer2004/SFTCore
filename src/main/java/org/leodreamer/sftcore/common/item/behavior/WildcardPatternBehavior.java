package org.leodreamer.sftcore.common.item.behavior;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.common.item.behavior.wildcard.gui.WildcardIOFancyConfigurator;
import org.leodreamer.sftcore.common.item.behavior.wildcard.impl.WildcardPatternLogic;

import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

@DataGenScanned
public class WildcardPatternBehavior implements IItemUIFactory, IFancyUIProvider {

    private InteractionHand hand;
    private Player player;

    @Override
    public InteractionResultHolder<ItemStack> use(Item item, Level level, Player player, InteractionHand usedHand) {
        this.hand = usedHand;
        this.player = player;
        return IItemUIFactory.super.use(item, level, player, usedHand);
    }

    @RegisterLanguage("Wildcard Pattern Config")
    static final String TITLE_CONFIG = "sftcore.item.wildcard_pattern.config";

    @Override
    public Component getTitle() {
        return Component.translatable(TITLE_CONFIG);
    }

    @RegisterLanguage("%d Patterns Available")
    static final String PATTERNS_AVAILABLE = "sftcore.item.wildcard_pattern.patterns_available";

    @Override
    public Widget createMainPage(FancyMachineUIWidget ui) {
        var group = new WidgetGroup(0, 0, 100, 80);
        var text = new LabelWidget(0, 0, Component.translatable(PATTERNS_AVAILABLE, 2));
        group.addWidget(text);
        return group;
    }

    @Override
    public IGuiTexture getTabIcon() {
        return new ItemStackTexture(SFTItems.WILDCARD_PATTERN.asItem());
    }

    @Override
    public void attachSideTabs(TabsWidget sideTabs) {
        sideTabs.setMainTab(this);
        var logic = WildcardPatternLogic.on(player.getItemInHand(hand));
        var inConfig = new WildcardIOFancyConfigurator(
            logic, WildcardPatternLogic.IO.IN,
            (stack) -> player.setItemInHand(hand, stack)
        );
        var outConfig = new WildcardIOFancyConfigurator(
            logic, WildcardPatternLogic.IO.OUT,
            (stack) -> player.setItemInHand(hand, stack)
        );
        sideTabs.attachSubTab(inConfig);
        sideTabs.attachSubTab(outConfig);
    }

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder heldItemHolder, Player player) {
        return new ModularUI(176, 166, heldItemHolder, player)
            .widget(new FancyMachineUIWidget(this, 176, 166));
    }
}
