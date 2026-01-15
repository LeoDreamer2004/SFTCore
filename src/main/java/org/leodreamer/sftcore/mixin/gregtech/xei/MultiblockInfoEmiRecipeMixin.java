package org.leodreamer.sftcore.mixin.gregtech.xei;

import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.common.item.OrderBehavior;
import org.leodreamer.sftcore.integration.emi.IMultiblockInputConsumer;
import org.leodreamer.sftcore.integration.emi.IMultiblockInputProvider;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.gui.widget.PatternPreviewWidget;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.integration.emi.multipage.MultiblockInfoEmiRecipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

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

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Mixin(MultiblockInfoEmiRecipe.class)
public abstract class MultiblockInfoEmiRecipeMixin extends ModularEmiRecipe<WidgetGroup>
    implements IMultiblockInputConsumer {

    @Shadow(remap = false)
    @Final
    private MultiblockMachineDefinition definition;

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

    @Unique
    public void sftcore$onInputUpdated(Stream<ItemStack> updated) {
        inputs = updated.filter(stack -> {
            var block = Block.byItem(stack.getItem());
            // skip the part machines
            if (block instanceof MetaMachineBlock machineBlock) {
                // Ok, this is a little hacky.
                var dummyTE = new MetaMachineBlockEntity(
                    BlockEntityType.FURNACE, new BlockPos(0, 0, 0), block.defaultBlockState()
                );
                var dummyMachine = machineBlock.definition.createMetaMachine(dummyTE);
                return !(dummyMachine instanceof MultiblockPartMachine);
            }
            return true;
        }).map(stack -> EmiIngredient.of(Ingredient.of(stack))).toList();
    }

    @Inject(method = "getOutputs", at = @At("HEAD"), cancellable = true, remap = false)
    private void setOutputToOrder(CallbackInfoReturnable<List<EmiStack>> cir) {
        cir.cancel();
        var order = SFTItems.ORDER.asStack();
        OrderBehavior.setTarget(order, definition.asStack());
        cir.setReturnValue(List.of(EmiStack.of(order)));
    }
}
