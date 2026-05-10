package org.leodreamer.sftcore.common.advancement.trigger;

import org.leodreamer.sftcore.SFTCore;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import com.google.gson.JsonObject;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SimpleAnyTrigger extends SimpleCriterionTrigger<SimpleAnyTrigger.Instance> {

    protected final ResourceLocation id;
    protected Instance instance;

    public SimpleAnyTrigger(ResourceLocation id) {
        this.id = id;
    }

    public SimpleAnyTrigger(String id) {
        this(SFTCore.id(id));
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    protected Instance createInstance(
        JsonObject json,
        ContextAwarePredicate player,
        DeserializationContext context
    ) {
        return instance();
    }

    public Instance instance() {
        if (instance == null) {
            instance = new Instance();
        }
        return instance;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, instance -> true);
    }

    public class Instance extends AbstractCriterionTriggerInstance {

        public Instance() {
            super(id, ContextAwarePredicate.ANY);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            return super.serializeToJson(context);
        }
    }
}
