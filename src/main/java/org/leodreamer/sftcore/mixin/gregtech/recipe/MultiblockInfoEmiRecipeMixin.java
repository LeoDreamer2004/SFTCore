package org.leodreamer.sftcore.mixin.gregtech.recipe;

import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.common.item.behavior.OrderBehavior;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.integration.emi.multipage.MultiblockInfoEmiRecipe;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import com.lowdragmc.lowdraglib.emi.ModularEmiRecipe;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.ItemEmiStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mixin(MultiblockInfoEmiRecipe.class)
public abstract class MultiblockInfoEmiRecipeMixin extends ModularEmiRecipe<WidgetGroup> {

    @Shadow(remap = false)
    @Final
    private MultiblockMachineDefinition definition;

    public MultiblockInfoEmiRecipeMixin(Supplier<WidgetGroup> widgetSupplier) {
        super(widgetSupplier);
    }

    @Override
    // ignore the @ApiStatus.Internal
    @SuppressWarnings("all")
    public List<EmiIngredient> getInputs() {
        List<EmiIngredient> stacks = new ArrayList<>();
        for (var input : super.getInputs()) {
            for (var emiStack : input.getEmiStacks()) {
                if (emiStack instanceof ItemEmiStack emiItem) {
                    var stack = emiItem.getItemStack();
                    var block = Block.byItem(stack.getItem());
                    if (block instanceof MetaMachineBlock machineBlock) {
                        // Ok, this is a little hacky.
                        var dummyTE = new MetaMachineBlockEntity(
                            BlockEntityType.FURNACE, new BlockPos(0, 0, 0), block.defaultBlockState()
                        );
                        var dummyMachine = machineBlock.definition.createMetaMachine(dummyTE);
                        if (dummyMachine instanceof MultiblockPartMachine) {
                            continue;
                        }
                    }
                }
                stacks.add(emiStack);
            }
        }
        return stacks;
    }

    @Inject(method = "getOutputs", at = @At("HEAD"), cancellable = true, remap = false)
    private void setOutputToOrder(CallbackInfoReturnable<List<EmiStack>> cir) {
        cir.cancel();
        var order = SFTItems.ORDER.asStack();
        OrderBehavior.setTarget(order, definition.asStack());
        cir.setReturnValue(List.of(EmiStack.of(order)));
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return null;
    }
}
