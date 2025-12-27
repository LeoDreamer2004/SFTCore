package org.leodreamer.sftcore.mixin.ae2.block;

import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.stacks.AEItemKey;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IHasCircuitSlot;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;
import com.hepdd.gtmthings.common.block.machine.multiblock.part.HugeBusPartMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.leodreamer.sftcore.integration.ae2.feature.HackyContainerGroupProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PatternContainerGroup.class)
public abstract class PatternContainerGroupMixin {

    @Shadow(remap = false)
    public abstract List<Component> tooltip();

    @Inject(method = "fromMachine", at = @At("HEAD"), cancellable = true, remap = false)
    private static void createGroupForGTMachine(
            Level level,
            BlockPos pos,
            Direction side,
            CallbackInfoReturnable<PatternContainerGroup> cir
    ) {
        var machine = MetaMachine.getMachine(level, pos);
        if (machine == null) return;

        if (machine instanceof IHasCircuitSlot circuitMachine) {
            var circuitStack = circuitMachine.isCircuitSlotEnabled() ?
                    circuitMachine.getCircuitInventory().getStackInSlot(0) : ItemStack.EMPTY;
            int circuit = circuitStack.isEmpty() ? 0 : IntCircuitBehaviour.getCircuitConfiguration(circuitStack);
            String desc = machine.getDefinition().getDescriptionId();

            if (machine instanceof MultiblockPartMachine mbMachine && mbMachine.isFormed()) {
                if (machine instanceof ItemBusPartMachine || machine instanceof FluidHatchPartMachine
                        || machine instanceof HugeBusPartMachine) {
                    var controllerMachine = mbMachine.getControllers().first().self();
                    var controller = controllerMachine.getDefinition();
                    var cpos = controllerMachine.getPos();
                    var group = new PatternContainerGroup(AEItemKey.of(controller.asStack()),
                            Component.translatable(controller.getDescriptionId()), List.of());
                    group = HackyContainerGroupProxy.of(group)
                            .setBlockPos(cpos)
                            .setCircuit(circuit)
                            .recordPartFrom(Component.translatable(desc))
                            .get();
                    cir.setReturnValue(group);
                    return;
                }
            }

            var group = new PatternContainerGroup(
                    AEItemKey.of(machine.getDefinition().asStack()), Component.translatable(desc), List.of());
            group = HackyContainerGroupProxy.of(group).setBlockPos(pos).setCircuit(circuit).get();
            cir.setReturnValue(group);
        }
    }
}
