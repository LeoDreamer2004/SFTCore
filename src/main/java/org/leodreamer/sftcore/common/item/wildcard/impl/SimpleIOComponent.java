package org.leodreamer.sftcore.common.item.wildcard.impl;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.wildcard.feature.WildcardIOComponent;
import org.leodreamer.sftcore.common.item.wildcard.handler.GenericStackHandler;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
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
import brachy.modularui.api.drawable.Text;
import brachy.modularui.drawable.Rectangle;
import brachy.modularui.utils.Alignment;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widgets.layout.Flow;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
@DataGenScanned
public class SimpleIOComponent extends WildcardIOComponent {

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
    }, stack -> new Dynamic<>(NbtOps.INSTANCE, GenericStack.writeTag(stack)));

    public static final Codec<SimpleIOComponent> CODEC = GENERIC_STACK_CODEC.xmap(
        SimpleIOComponent::new,
        component -> component.stack
    );

    @NotNull
    @Getter
    private GenericStack stack;
    @Getter
    private long amount;
    private GenericStackHandler sample;
    private static final int GROUP_BG = 0xFF337777;

    @RegisterLanguage("Single")
    public static final String LABEL = "item.sftcore.wildcard_pattern.ui.io.single";

    @RegisterLanguage("Empty")
    public static final String EMPTY_TOOLTIP = "item.sftcore.wildcard_pattern.tooltip.empty";

    public static SimpleIOComponent empty() {
        return new SimpleIOComponent(new GenericStack(AEItemKey.of(Items.AIR), 1));
    }

    public SimpleIOComponent(@NotNull GenericStack stack) {
        this.stack = stack;
        this.amount = stack.amount();
    }

    @Override
    public GenericStack apply(Material material) {
        if (stack.what() == null) {
            return null;
        }
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
    protected void addLineContent(
        Flow row,
        PanelSyncManager syncManager,
        String lineSyncKey
    ) {
        var sampleSyncHandler = registerSampleSlot(
            new GenericStackHandler(stack), GenericStackHandler.class, syncManager, lineSyncKey
        );
        this.sample = sampleSlotHandler(sampleSyncHandler, GenericStackHandler.class);
        this.sample.setGenericStack(stack);
        row.child(createTypeButton(42, LABEL));
        row.child(createSampleSlot(sampleSyncHandler));
        row.child(Text.str("x").asWidget().width(12).textAlign(Alignment.Center).marginLeft(2));
        row.child(amountField(62, () -> Long.toString(amount), text -> amount = parseLongAmount(text)));
    }

    @Override
    public Component createTooltip() {
        if (stack.what() == null) {
            return Component.translatable(EMPTY_TOOLTIP).withStyle(ChatFormatting.GRAY);
        }
        return stack.what().getDisplayName().copy().withStyle(ChatFormatting.YELLOW)
            .append(Component.literal(" x " + amount).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onSave() {
        if (sample == null) {
            return;
        }
        var stack = sample.getStackInSlot(0);
        if (stack.isEmpty()) {
            return;
        }
        var newStack = sample.getGenericStack(amount);
        if (newStack != null) {
            this.stack = newStack;
        }
    }

    @Override
    protected Rectangle rowBackground() {
        return new Rectangle().color(GROUP_BG);
    }

    @Override
    public @NotNull String toString() {
        return "Component " + stack;
    }
}
