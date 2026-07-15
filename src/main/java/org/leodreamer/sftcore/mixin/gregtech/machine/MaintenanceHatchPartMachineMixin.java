package org.leodreamer.sftcore.mixin.gregtech.machine;

import org.leodreamer.sftcore.common.advancement.SFTCriteriaTriggers;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.notifiable.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.machine.multiblock.part.MaintenanceHatchPartMachine;
import com.gregtechceu.gtceu.utils.ExtendedUseOnContext;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MaintenanceHatchPartMachine.class, remap = false)
public abstract class MaintenanceHatchPartMachineMixin extends TieredPartMachine {

    @Shadow
    @Final
    private NotifiableItemStackHandler itemStackHandler;

    @Unique
    private boolean sftcore$trackDuctTapeInsertion;

    @Unique
    private int sftcore$lastDuctTapeCount;

    public MaintenanceHatchPartMachineMixin(BlockEntityCreationInfo info, int tier) {
        super(info, tier);
    }

    @Inject(method = "createInventory", at = @At("RETURN"), remap = false)
    private void sftcore$wrapMaintenanceInventory(
        CallbackInfoReturnable<NotifiableItemStackHandler> cir
    ) {
        NotifiableItemStackHandler handler = cir.getReturnValue();
        Runnable originalListener = handler.storage.getOnContentsChanged();

        handler.storage.setOnContentsChanged(() -> {
            originalListener.run();
            sftcore$onMaintenanceInventoryChanged(handler);
        });
    }

    @Inject(method = "onLoad", at = @At("RETURN"), remap = false)
    private void sftcore$startTrackingAfterLoad(CallbackInfo ci) {
        MaintenanceHatchPartMachine self = (MaintenanceHatchPartMachine) (Object) this;

        if (self.isRemote()) {
            return;
        }

        this.sftcore$lastDuctTapeCount = sftcore$getDuctTapeCount(this.itemStackHandler);
        this.sftcore$trackDuctTapeInsertion = true;
    }

    @Inject(method = "onUseWithItem", at = @At("RETURN"), remap = false)
    private void sftcore$triggerWhenHeldDuctTapeUsed(
        ExtendedUseOnContext context,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (cir.getReturnValue() != InteractionResult.SUCCESS) {
            return;
        }

        if (context.getPlayer() instanceof ServerPlayer player) {
            SFTCriteriaTriggers.DUCT_TAPED_MAINTENANCE.trigger(player);
        }
    }

    @Unique
    private void sftcore$onMaintenanceInventoryChanged(NotifiableItemStackHandler handler) {
        if (isRemote()) {
            return;
        }

        int newCount = sftcore$getDuctTapeCount(handler);

        if (!this.sftcore$trackDuctTapeInsertion) {
            this.sftcore$lastDuctTapeCount = newCount;
            return;
        }

        // Only trigger when tape become MORE
        if (newCount > this.sftcore$lastDuctTapeCount) {
            SFTCriteriaTriggers.DUCT_TAPED_MAINTENANCE.trigger(
                (MaintenanceHatchPartMachine) (Object) this
            );
        }
        this.sftcore$lastDuctTapeCount = newCount;
    }

    @Unique
    private static int sftcore$getDuctTapeCount(NotifiableItemStackHandler handler) {
        var stack = handler.getStackInSlot(0);

        if (!stack.isEmpty() && stack.is(GTItems.DUCT_TAPE.get())) {
            return stack.getCount();
        }

        return 0;
    }
}
