package org.leodreamer.sftcore.mixin.ae2.block;

import org.leodreamer.sftcore.common.data.lang.MixinTooltips;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IHasCircuitSlot;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.stacks.AEItemKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PatternContainerGroup.class)
public class PatternContainerGroupMixin {

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
            String circuitSuffix = circuit == 0 ? "" : " - " + circuit;

            if (machine instanceof MultiblockPartMachine mbMachine && mbMachine.isFormed()) {
                if (machine instanceof ItemBusPartMachine || machine instanceof FluidHatchPartMachine) {
                    var controller = mbMachine.getControllers().first().self().getDefinition();
                    var name = Component.translatable(controller.getDescriptionId()).append(circuitSuffix);
                    var group = new PatternContainerGroup(
                        AEItemKey.of(controller.asStack()),
                        name,
                        List.of(
                            Component.translatable(MixinTooltips.PART_FROM)
                                .append(Component.translatable(desc))
                        )
                    );
                    cir.setReturnValue(group);
                    return;
                }
            }

            var name = Component.translatable(desc).append(circuitSuffix);
            var group = new PatternContainerGroup(
                AEItemKey.of(machine.getDefinition().asStack()), name, List.of()
            );
            cir.setReturnValue(group);
        }
    }
}
