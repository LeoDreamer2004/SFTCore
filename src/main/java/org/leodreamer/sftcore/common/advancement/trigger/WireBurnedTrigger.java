package org.leodreamer.sftcore.common.advancement.trigger;

import org.leodreamer.sftcore.SFTCore;

import com.gregtechceu.gtceu.common.blockentity.CableBlockEntity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import com.google.gson.JsonObject;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WireBurnedTrigger extends SimpleCriterionTrigger<WireBurnedTrigger.Instance> {

    public static final ResourceLocation ID = SFTCore.id("wire_burned");

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
     * Helper function to trigger the advancement for the player nearest to the burned cable.
     *
     * @param cable The cable that was burned
     */
    public void trigger(CableBlockEntity cable) {
        if (!(cable.getLevel() instanceof ServerLevel level)) {
            return;
        }
        var pos = cable.getBlockPos();
        var player = TriggerUtils.findNearestPlayer(level, pos);
        if (player instanceof ServerPlayer serverPlayer) {
            trigger(serverPlayer);
        }
    }

    public static class Instance extends AbstractCriterionTriggerInstance {

        public Instance(ContextAwarePredicate player) {
            super(ID, player);
        }

        public static Instance burned() {
            return new Instance(ContextAwarePredicate.ANY);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            return super.serializeToJson(context);
        }
    }
}
