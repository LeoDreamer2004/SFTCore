package org.leodreamer.sftcore.common.advancement.trigger;

import com.google.gson.JsonObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.leodreamer.sftcore.SFTCore;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MachineExplodedTrigger extends SimpleCriterionTrigger<MachineExplodedTrigger.Instance> {

    public static final ResourceLocation ID = SFTCore.id("machine_exploded");

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
     * Helper function to trigger the advancement for the player nearest to the machine when a GTM machine explodes.
     *
     * @param level The world
     * @param pos   position of the machine
     */
    public void trigger(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel)) {
            return;
        }

        var player = TriggerUtils.findNearestPlayer(level, pos);
        if (player instanceof ServerPlayer serverPlayer) {
            trigger(serverPlayer);
        }
    }

    public static class Instance extends AbstractCriterionTriggerInstance {

        public Instance(ContextAwarePredicate player) {
            super(ID, player);
        }

        public static Instance exploded() {
            return new Instance(ContextAwarePredicate.ANY);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            return super.serializeToJson(context);
        }
    }
}
