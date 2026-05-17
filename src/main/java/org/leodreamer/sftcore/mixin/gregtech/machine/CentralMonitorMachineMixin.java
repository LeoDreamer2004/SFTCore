package org.leodreamer.sftcore.mixin.gregtech.machine;

import org.leodreamer.sftcore.common.advancement.SFTCriteriaTriggers;

import com.gregtechceu.gtceu.common.machine.multiblock.electric.CentralMonitorMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.monitor.MonitorGroup;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = CentralMonitorMachine.class, remap = false)
public abstract class CentralMonitorMachineMixin {

    @Shadow
    public abstract List<MonitorGroup> getMonitorGroups();

    @Unique
    private boolean sftcore$hadMonitorModule;

    @Inject(method = "tick", at = @At("TAIL"))
    private void sftcore$triggerWhenMonitorModuleInstalled(CallbackInfo ci) {
        boolean hasModule = getMonitorGroups().stream()
            .map(group -> group.getItemStackHandler().getStackInSlot(0))
            .anyMatch(MonitorGroup::isModule);

        if (!this.sftcore$hadMonitorModule && hasModule) {
            SFTCriteriaTriggers.CENTRAL_MONITOR_MODULE_INSTALLED.trigger(
                (CentralMonitorMachine) (Object) this
            );
        }

        this.sftcore$hadMonitorModule = hasModule;
    }
}
