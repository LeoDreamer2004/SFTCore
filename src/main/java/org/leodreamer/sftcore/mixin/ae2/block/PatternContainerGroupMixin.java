package org.leodreamer.sftcore.mixin.ae2.block;

import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.stacks.AEItemKey;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PatternContainerGroup.class)
public class PatternContainerGroupMixin {

    @Inject(method = "fromMachine", at = @At("HEAD"), cancellable = true, remap = false)
    private static void createGroupForPartMachine(Level level, BlockPos pos, Direction side, CallbackInfoReturnable<PatternContainerGroup> cir) {
        var machine = MetaMachine.getMachine(level, pos);
        if (machine == null) return;

        if (machine instanceof MultiblockPartMachine mbMachine && mbMachine.isFormed()) {
            if (machine instanceof ItemBusPartMachine itemBus) {
                var circuitStack = itemBus.isCircuitSlotEnabled() ?
                        itemBus.getCircuitInventory().getStackInSlot(0) : ItemStack.EMPTY;
                int circuit = circuitStack.isEmpty() ? -1 : IntCircuitBehaviour.getCircuitConfiguration(circuitStack);
                cir.cancel();
                cir.setReturnValue(sftcore$buildFor(itemBus, circuit));
            } else if (machine instanceof FluidHatchPartMachine fluidHatch) {
                var circuitStack = fluidHatch.isCircuitSlotEnabled() ?
                        fluidHatch.getCircuitInventory().getStackInSlot(0) : ItemStack.EMPTY;
                int circuit = circuitStack.isEmpty() ? -1 : IntCircuitBehaviour.getCircuitConfiguration(circuitStack);
                cir.cancel();
                cir.setReturnValue(sftcore$buildFor(fluidHatch, circuit));
            }
        }
    }

    @Unique
    private static PatternContainerGroup sftcore$buildFor(MultiblockPartMachine hatch, int circuit) {
        var controller = hatch.getControllers().first().self().getDefinition();
        var desc = controller.getDescriptionId();
        Component name = circuit != -1 ?
                Component.translatable(desc).append(" - " + circuit) :
                Component.translatable(desc);
        return new PatternContainerGroup(AEItemKey.of(controller.asStack()), name,
                List.of(Component.translatable(hatch.getDefinition().getDescriptionId())));
    }
}
