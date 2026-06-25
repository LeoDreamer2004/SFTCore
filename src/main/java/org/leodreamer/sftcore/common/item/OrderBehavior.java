package org.leodreamer.sftcore.common.item;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.machine.trait.SimpleNotifiableItemHandler;
import org.leodreamer.sftcore.util.RLUtils;

import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.ICustomDescriptionId;
import com.gregtechceu.gtceu.api.mui.IItemUIHolder;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.factory.PlayerInventoryGuiData;
import brachy.modularui.screen.ModularPanel;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.slot.ModularSlot;
import brachy.modularui.widgets.slot.PhantomItemSlot;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * These codes are from <a href=
 * "https://github.com/GregTech-Odyssey/GTOCore/blob/main/src/main/java/com/gtocore/common/item/OrderItem.java">GregTech
 * Odyssey</a>
 */
@DataGenScanned
public class OrderBehavior
    implements IItemUIHolder, ICustomDescriptionId, IAddInformation {

    private static final String ID = "marker_id";
    private static final String NBT = "marker_nbt";

    @Override
    public InteractionResultHolder<ItemStack> use(Item item, Level level, Player player, InteractionHand usedHand) {
        return IItemUIHolder.super.use(item, level, player, usedHand);
    }

    public static void setTarget(ItemStack stack, ItemStack target) {
        var tag = stack.getOrCreateTag();
        var id = RLUtils.getItemRL(target.getItem());
        tag.putString(ID, id.toString());
        if (target.hasTag()) {
            tag.put(NBT, Objects.requireNonNull(target.getTag()).copy());
        }
    }

    public static ItemStack getTarget(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        var id = tag.getString(ID);
        if (id.isEmpty()) {
            return ItemStack.EMPTY;
        }
        var nbt = tag.getCompound(NBT);
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

    public static void clearTarget(ItemStack stack) {
        if (!stack.hasTag()) {
            return;
        }
        var tag = stack.getOrCreateTag();
        tag.remove(ID);
        tag.remove(NBT);
    }

    @Override
    public Component getItemName(ItemStack stack) {
        var name = Component.empty();
        if (stack.hasTag()) {
            var target = getTarget(stack);
            if (!target.isEmpty()) {
                name = target.getHoverName().copy().append(" ");
            }
        }
        return Component.translatable(stack.getDescriptionId(), name);
    }

    @Override
    public ModularPanel<?> buildUI(PlayerInventoryGuiData<?> data, PanelSyncManager syncManager, UISettings settings) {
        var stack = data.getUsedItemStack();
        var handler = new SimpleNotifiableItemHandler(
            target -> setTarget(stack, target),
            () -> clearTarget(stack)
        );
        handler.setStackInSlot(0, getTarget(stack));

        return ModularPanel.defaultPanel("order_marker", 176, 112)
            .background(GTGuiTextures.BACKGROUND)
            .child(
                Flow.column()
                    .coverChildren()
                    .child(Text.lang(ORDER_CONFIG).asWidget().marginBottom(8))
                    .child(
                        new PhantomItemSlot()
                            .slot(new ModularSlot(handler, 0))
                            .background(GTGuiTextures.SLOT)
                    )
                    .center()
            );
    }

    @RegisterLanguage("Order Configuration")
    static final String ORDER_CONFIG = "item.sftcore.order.config";

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
