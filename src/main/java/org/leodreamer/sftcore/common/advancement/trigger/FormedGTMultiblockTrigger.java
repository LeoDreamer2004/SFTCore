package org.leodreamer.sftcore.common.advancement.trigger;

import com.google.gson.JsonObject;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;
import org.leodreamer.sftcore.SFTCore;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FormedGTMultiblockTrigger extends SimpleCriterionTrigger<FormedGTMultiblockTrigger.Instance> {

    public static final ResourceLocation ID = SFTCore.id("formed_gt_multiblock");
    private static final double FORMED_MULTIBLOCK_TRIGGER_RANGE = 32.0D;

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
        var machine = ResourceLocation.parse(GsonHelper.getAsString(json, "machine"));
        return new Instance(player, machine);
    }

    public void trigger(ServerPlayer player, ResourceLocation machineId) {
        this.trigger(player, instance -> instance.matches(machineId));
    }

    /**
     * Helper function to trigger the advancement for the player nearest to the machine when a GTM multiblock is formed.
     *
     * @param controller The multiblock controller that was formed
     */
    public void trigger(IMultiController controller) {
        var machine = controller.self();
        if (!(machine.getLevel() instanceof ServerLevel level)) {
            return;
        }

        var pos = machine.getPos();
        var player = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), FORMED_MULTIBLOCK_TRIGGER_RANGE, true);

        if (player instanceof ServerPlayer serverPlayer) {
            trigger(serverPlayer, machine.getDefinition().getId());
        }
    }

    public static class Instance extends AbstractCriterionTriggerInstance {

        private final @NotNull ResourceLocation machine;

        public Instance(ContextAwarePredicate player, ResourceLocation machine) {
            super(ID, player);
            this.machine = machine;
        }

        public static Instance machine(ResourceLocation machine) {
            return new Instance(ContextAwarePredicate.ANY, machine);
        }

        public boolean matches(ResourceLocation machineId) {
            return machine.equals(machineId);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            json.addProperty("machine", machine.toString());
            return json;
        }
    }
}
