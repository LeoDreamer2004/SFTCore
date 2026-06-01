package org.leodreamer.sftcore.common.item.wildcard.impl;

import org.leodreamer.sftcore.common.item.wildcard.feature.IWildcardIOComponent;
import org.leodreamer.sftcore.integration.ae2.gui.PhantomGenericSlotWidget;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.utils.GTMath;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
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
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import org.jetbrains.annotations.NotNull;

public class SimpleIOComponent implements IWildcardIOComponent {

    public static final Codec<GenericStack> GENERIC_STACK_CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
        var tag = dynamic.convert(NbtOps.INSTANCE).getValue();
        if (!(tag instanceof CompoundTag compoundTag)) {
            return DataResult.error(() -> "Expected CompoundTag for GenericStack, got: " + tag);
        }
        var stack = GenericStack.readTag(compoundTag);
        if (stack == null) {
            return DataResult.error(() -> "Failed to decode GenericStack from tag: " + compoundTag);
        }
        return DataResult.success(stack);
    }, stack -> new Dynamic<>(NbtOps.INSTANCE, encodeGenericStack(stack)));

    public static final Codec<SimpleIOComponent> CODEC = GENERIC_STACK_CODEC.xmap(
        SimpleIOComponent::new,
        component -> component.stack
    );

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
            return GenericStack.fromFluidStack(
                new FluidStack(
                    bucket.getFluid(),
                    GTMath.saturatedCast(stack.amount())
                )
            );
        }

        return stack;
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
        return Long.toString(stack.amount());
    }

    private void setAmount(String str) {
        if (str == null || str.isEmpty()) {
            return;
        }

        stack = new GenericStack(stack.what(), Long.parseLong(str));
    }

    @Override
    public void onSave() {
        GenericStack genericStack = genericSlot.getStack();

        if (genericStack == null) {
            stack = empty().stack;
            return;
        }

        long amount = Long.parseLong(amountEdit.getCurrentString());
        stack = new GenericStack(genericStack.what(), amount);
    }

    @Override
    public String toString() {
        return "Component " + stack;
    }

    private static CompoundTag encodeGenericStack(GenericStack stack) {
        return GenericStack.writeTag(stack);
    }
}
