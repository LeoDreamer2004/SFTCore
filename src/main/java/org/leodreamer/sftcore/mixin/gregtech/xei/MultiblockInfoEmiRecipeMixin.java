package org.leodreamer.sftcore.mixin.gregtech.xei;

import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.common.item.OrderBehavior;
import org.leodreamer.sftcore.common.machine.multiblock.SFTPartAbility;
import org.leodreamer.sftcore.integration.emi.IMultiblockInputConsumer;
import org.leodreamer.sftcore.integration.emi.IMultiblockInputProvider;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.gui.widget.PatternPreviewWidget;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.integration.emi.multipage.MultiblockInfoEmiRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import com.lowdragmc.lowdraglib.emi.ModularEmiRecipe;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Mixin(value = MultiblockInfoEmiRecipe.class, remap = false)
public abstract class MultiblockInfoEmiRecipeMixin extends ModularEmiRecipe<WidgetGroup>
    implements IMultiblockInputConsumer {

    @Shadow
    @Final
    private MultiblockMachineDefinition definition;

    @Unique
    private static Set<Block> sftcore$partAbilityBlocks;

    public MultiblockInfoEmiRecipeMixin(Supplier<WidgetGroup> widgetSupplier) {
        super(widgetSupplier);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void bindInputUpdateListener(MultiblockMachineDefinition definition, CallbackInfo ci) {
        widget = () -> {
            var w = PatternPreviewWidget.getPatternWidget(definition);
            ((IMultiblockInputProvider) w).sftcore$setInputsUpdateResponser(this::sftcore$onInputUpdated);
            return w;
        };
    }

    @Override
    @Unique
    public void sftcore$onInputUpdated(Stream<ItemStack> updated) {
        this.inputs = updated
            .filter(stack -> !stack.isEmpty())
            .filter(stack -> !sftcore$isMultiblockPartAbilityStack(stack))
            .map(stack -> (EmiIngredient) EmiStack.of(stack.copy()))
            .toList();
    }

    @Unique
    private static boolean sftcore$isMultiblockPartAbilityStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Block block = Block.byItem(stack.getItem());
        return block instanceof MetaMachineBlock && sftcore$getPartAbilityBlocks().contains(block);
    }

    /**
     * Get all blocks with part abilities
     */
    @Unique
    private static Set<Block> sftcore$getPartAbilityBlocks() {
        if (sftcore$partAbilityBlocks != null) {
            // cached
            return sftcore$partAbilityBlocks;
        }

        Set<Block> blocks = new HashSet<>();
        sftcore$collectPartAbilityBlocks(PartAbility.class, blocks);
        sftcore$collectPartAbilityBlocks(SFTPartAbility.class, blocks);

        sftcore$partAbilityBlocks = blocks;
        return sftcore$partAbilityBlocks;
    }

    /**
     * Collect all part abilities in the holder class to blocks
     */
    @Unique
    private static void sftcore$collectPartAbilityBlocks(Class<?> holder, Set<Block> blocks) {
        for (Field field : holder.getDeclaredFields()) {
            int modifiers = field.getModifiers();

            if (!Modifier.isStatic(modifiers)) {
                continue;
            }

            if (!PartAbility.class.isAssignableFrom(field.getType())) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object value = field.get(null);

                if (value instanceof PartAbility ability) {
                    blocks.addAll(ability.getAllBlocks());
                }
            } catch (IllegalAccessException ignored) {}
        }
    }

    @Inject(method = "getOutputs", at = @At("HEAD"), cancellable = true, remap = false)
    private void sftcore$setOutputToOrder(CallbackInfoReturnable<List<EmiStack>> cir) {
        cir.cancel();
        var order = SFTItems.ORDER.asStack();
        OrderBehavior.setTarget(order, definition.asStack());
        cir.setReturnValue(List.of(EmiStack.of(order)));
    }
}
