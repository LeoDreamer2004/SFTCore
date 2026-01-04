package org.leodreamer.sftcore.common.item;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternLogic;
import org.leodreamer.sftcore.common.item.wildcard.gui.WildcardFilterFancyConfigurator;
import org.leodreamer.sftcore.common.item.wildcard.gui.WildcardIOFancyConfigurator;
import org.leodreamer.sftcore.common.item.wildcard.gui.WildcardIndexPage;

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
import com.lowdragmc.lowdraglib.gui.widget.Widget;

import java.util.function.Consumer;

@DataGenScanned
public class WildcardPatternBehavior implements IItemUIFactory, IFancyUIProvider {

    private InteractionHand hand;
    private Player player;
    private WildcardPatternLogic logic;

    @Override
    public InteractionResultHolder<ItemStack> use(Item item, Level level, Player player, InteractionHand usedHand) {
        this.hand = usedHand;
        this.player = player;
        logic = WildcardPatternLogic.on(player.getItemInHand(hand));
        return IItemUIFactory.super.use(item, level, player, usedHand);
    }

    @RegisterLanguage("Wildcard Pattern Config")
    private static final String TITLE_CONFIG = "sftcore.item.wildcard_pattern.config";

    @Override
    public Component getTitle() {
        return Component.translatable(TITLE_CONFIG);
    }

    @Override
    public Widget createMainPage(FancyMachineUIWidget ui) {
        return new WildcardIndexPage(logic, player.level(), 0, 0, 158, 80);
    }

    @Override
    public IGuiTexture getTabIcon() {
        return new ItemStackTexture(SFTItems.WILDCARD_PATTERN.asItem());
    }

    @Override
    public void attachSideTabs(TabsWidget sideTabs) {
        sideTabs.setMainTab(this);
        Consumer<ItemStack> save = (stack) -> player.setItemInHand(hand, stack);
        var inConfig = new WildcardIOFancyConfigurator(logic, WildcardPatternLogic.IO.IN, save);
        var outConfig = new WildcardIOFancyConfigurator(logic, WildcardPatternLogic.IO.OUT, save);
        var filterConfig = new WildcardFilterFancyConfigurator(logic, save);
        sideTabs.attachSubTab(inConfig);
        sideTabs.attachSubTab(outConfig);
        sideTabs.attachSubTab(filterConfig);
    }

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder heldItemHolder, Player player) {
        return new ModularUI(176, 166, heldItemHolder, player)
            .widget(new FancyMachineUIWidget(this, 176, 166));
    }
}
