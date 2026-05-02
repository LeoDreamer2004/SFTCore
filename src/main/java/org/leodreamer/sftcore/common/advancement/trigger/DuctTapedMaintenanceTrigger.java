package org.leodreamer.sftcore.common.advancement.trigger;

import com.google.gson.JsonObject;
import com.gregtechceu.gtceu.common.machine.multiblock.part.MaintenanceHatchPartMachine;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.leodreamer.sftcore.SFTCore;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DuctTapedMaintenanceTrigger extends SimpleCriterionTrigger<DuctTapedMaintenanceTrigger.Instance> {

    public static final ResourceLocation ID = SFTCore.id("duct_taped_maintenance");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Instance createInstance(
        JsonObject json,
        ContextAwarePredicate player,
        DeserializationContext context
    ) {
        return new Instance(player);
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, instance -> true);
    }

    /**
     * Helper function to trigger when a player right-clicks the
     * maintenance hatch with duct tape in hand. It will trigger the advancement for the player.
     * @param player The player fix the hatch
     */
    public void trigger(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            trigger(serverPlayer);
        }
    }

    /**
     * Helper function to trigger when a player placed some tapes in a maintenance hatch/
     * No player context here, so use the nearest.
     * @param hatch The hatch which got more tapes
     */
    public void trigger(MaintenanceHatchPartMachine hatch) {
        if (!(hatch.getLevel() instanceof ServerLevel level)) {
            return;
        }

        var player = TriggerUtils.findNearestPlayer(level, hatch.getBlockPos());

        if (player instanceof ServerPlayer serverPlayer) {
            trigger(serverPlayer);
        }
    }

    public static class Instance extends AbstractCriterionTriggerInstance {

        public Instance(ContextAwarePredicate player) {
            super(ID, player);
        }

        public static Instance taped() {
            return new Instance(ContextAwarePredicate.ANY);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            return super.serializeToJson(context);
        }
    }
}
