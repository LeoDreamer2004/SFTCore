package org.leodreamer.sftcore.common.item.wildcard.impl;

import org.leodreamer.sftcore.common.item.wildcard.WildcardSerializers;
import org.leodreamer.sftcore.common.item.wildcard.feature.IWildcardIOComponent;
import org.leodreamer.sftcore.integration.ae2.gui.PhantomGenericSlotWidget;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.utils.GTMath;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import org.jetbrains.annotations.NotNull;

public class SimpleIOComponent implements IWildcardIOComponent {

    @NotNull
    private GenericStack stack;
    private PhantomGenericSlotWidget genericSlot;
    private TextFieldWidget amountEdit;
    private static final IGuiTexture GROUP_BG = ResourceBorderTexture.BUTTON_COMMON.copy()
        .setColor(ColorPattern.CYAN.color);

    public static SimpleIOComponent empty() {
        return new SimpleIOComponent(new GenericStack(AEItemKey.of(Items.AIR), 1));
    }

    public SimpleIOComponent(@NotNull GenericStack stack) {
        this.stack = stack;
    }

    @Override
    public GenericStack apply(Material material) {
        if (stack.what() instanceof AEItemKey item && item.getItem() instanceof BucketItem bucket) {
            return GenericStack.fromFluidStack(new FluidStack(bucket.getFluid(), GTMath.saturatedCast(stack.amount())));
        }
        return stack;
    }

    @Override
    public IWildcardSerializer<IWildcardIOComponent> getSerializer() {
        return WildcardSerializers.IO_SIMPLE;
    }

    @Override
    public void createUILine(WidgetGroup line) {
        line.setBackground(GROUP_BG);

        genericSlot = new PhantomGenericSlotWidget(new CustomItemStackHandler(), 0, 3, 3);
        genericSlot.setStack(stack);
        amountEdit = new TextFieldWidget(80, 5, 50, 15, this::getAmount, this::setAmount);
        amountEdit.setNumbersOnly(0, Integer.MAX_VALUE);

        line.addWidget(genericSlot);
        line.addWidget(new LabelWidget(70, 7, "x"));
        line.addWidget(amountEdit);
    }

    @Override
    public Component createTooltip() {
        return stack.what().getDisplayName().copy().withStyle(ChatFormatting.YELLOW)
            .append(Component.literal(" x " + getAmount()).withStyle(ChatFormatting.GRAY));
    }

    private String getAmount() {
        return Integer.toString(GTMath.saturatedCast(stack.amount()));
    }

    private void setAmount(String str) {
        stack = new GenericStack(stack.what(), Integer.parseInt(str));
    }

    @Override
    public void onSave() {
        var genericStack = genericSlot.getStack();
        int amount = Integer.parseInt(amountEdit.getCurrentString());
        stack = genericStack == null ? empty().stack : new GenericStack(genericStack.what(), amount);
    }

    @Override
    public String toString() {
        return "Component " + stack;
    }

    public static class Serializer implements IWildcardSerializer<IWildcardIOComponent> {

        @Override
        public String key() {
            return "simple";
        }

        @Override
        public @NotNull CompoundTag serialize(IWildcardIOComponent component) {
            return GenericStack.writeTag(((SimpleIOComponent) component).stack);
        }

        @Override
        public @NotNull SimpleIOComponent deserialize(CompoundTag nbt) {
            var stack = GenericStack.readTag(nbt);
            return stack == null ? empty() : new SimpleIOComponent(stack);
        }
    }
}
