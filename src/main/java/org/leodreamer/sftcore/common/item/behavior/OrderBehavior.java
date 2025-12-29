package org.leodreamer.sftcore.common.item.behavior;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.gui.SimpleNotifiableItemHandler;
import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.util.RLUtils;

import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget;
import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.ICustomDescriptionId;
import com.gregtechceu.gtceu.api.item.component.IItemUIFactory;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.PhantomSlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@DataGenScanned
public class OrderBehavior
    implements IItemUIFactory, IFancyUIProvider, ICustomDescriptionId, IAddInformation {

    private InteractionHand hand;

    private static final String ID = "marker_id";
    private static final String NBT = "marker_nbt";

    @Override
    public InteractionResultHolder<ItemStack> use(Item item, Level level, Player player, InteractionHand usedHand) {
        this.hand = usedHand;
        return IItemUIFactory.super.use(item, level, player, usedHand);
    }

    @Override
    public ModularUI createUI(HeldItemUIFactory.HeldItemHolder heldItemHolder, Player player) {
        return new ModularUI(176, 166, heldItemHolder, player)
            .widget(new FancyMachineUIWidget(this, 176, 166));
    }

    public static ItemStack setTarget(ItemStack stack, ItemStack target) {
        var tag = stack.getOrCreateTag();
        var id = RLUtils.getItemRL(target.getItem());
        tag.putString(ID, id.toString());
        if (target.hasTag()) {
            tag.put(NBT, Objects.requireNonNull(target.getTag()).copy());
        }
        return stack;
    }

    public static ItemStack getTarget(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        var id = tag.getString(ID);
        if (id.isEmpty()) {
            return ItemStack.EMPTY;
        }
        CompoundTag nbt = tag.getCompound(NBT);
        var item = RLUtils.getItemByName(id);
        if (item == null) {
            return ItemStack.EMPTY;
        }
        var target = new ItemStack(item);
        if (!nbt.isEmpty()) {
            target.setTag(nbt.copy());
        }
        return target;
    }

    public static ItemStack clearTarget(ItemStack stack) {
        if (!stack.hasTag()) return stack;
        var tag = stack.getOrCreateTag();
        tag.remove(ID);
        tag.remove(NBT);
        return stack;
    }

    @Override
    public Component getItemName(ItemStack stack) {
        Component name = Component.empty();
        if (stack.hasTag()) {
            var target = getTarget(stack);
            if (!target.isEmpty()) {
                name = target.getHoverName().copy().append(" ");
            }
        }
        return Component.translatable(stack.getDescriptionId(), name);
    }

    @Override
    public Widget createMainPage(FancyMachineUIWidget ui) {
        WidgetGroup group = new WidgetGroup(0, 0, 34, 34);
        WidgetGroup container = new WidgetGroup(4, 4, 26, 26);
        Player player = ui.getGui() == null ? null : ui.getGui().entityPlayer;
        if (player != null) {
            var handler = new SimpleNotifiableItemHandler(
                stack -> player.setItemInHand(hand, setTarget(player.getItemInHand(hand), stack)),
                () -> player.setItemInHand(hand, clearTarget(player.getItemInHand(hand)))
            );
            container.addWidget(new PhantomSlotWidget(handler, 0, 4, 4));
        }
        group.addWidget(container);
        return group;
    }

    @Override
    public IGuiTexture getTabIcon() {
        return new ItemStackTexture(SFTItems.ORDER.asItem());
    }

    @RegisterLanguage("Order Configuration")
    static final String ORDER_CONFIG = "item.sftcore.order.config";

    @Override
    public Component getTitle() {
        return Component.translatable(ORDER_CONFIG);
    }

    public void attachSideTabs(TabsWidget sideTabs) {
        sideTabs.setMainTab(this);
    }

    @RegisterLanguage("Right click to put a virtual item")
    static final String TOOLTIP_0 = "item.sftcore.order.tooltip.0";

    @RegisterLanguage("Can be used as the product for AE2 automatic crafting")
    static final String TOOLTIP_1 = "item.sftcore.order.tooltip.1";

    @RegisterLanguage("When the crafting is completed, it will automatically cancel, no need to cancel manually")
    static final String TOOLTIP_2 = "item.sftcore.order.tooltip.2";

    @Override
    public void appendHoverText(
        ItemStack itemStack,
        @Nullable Level level,
        List<Component> list,
        TooltipFlag tooltipFlag
    ) {
        list.add(Component.translatable(TOOLTIP_0));
        list.add(Component.translatable(TOOLTIP_1));
        list.add(Component.translatable(TOOLTIP_2).withStyle(ChatFormatting.DARK_AQUA));
    }
}
