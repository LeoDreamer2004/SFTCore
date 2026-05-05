package org.leodreamer.sftcore.common.advancement.trigger;

import org.leodreamer.sftcore.SFTCore;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipeExecutedTrigger extends SimpleCriterionTrigger<RecipeExecutedTrigger.Instance> {

    public static final ResourceLocation ID = SFTCore.id("recipe_executed");
    // Cache of recipe IDs that are currently being watched by any instance of this trigger,
    // used to quickly skip triggering when a recipe is executed that no instance cares about
    private static final Set<ResourceLocation> WATCHED_RECIPES = ConcurrentHashMap.newKeySet();

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
        Set<ResourceLocation> recipes = new LinkedHashSet<>();

        if (json.has("recipe")) {
            recipes.add(ResourceLocation.parse(GsonHelper.getAsString(json, "recipe")));
        }

        if (json.has("recipes")) {
            JsonArray array = GsonHelper.getAsJsonArray(json, "recipes");

            for (var element : array) {
                recipes.add(ResourceLocation.parse(GsonHelper.convertToString(element, "recipe id")));
            }
        }

        if (recipes.isEmpty()) {
            throw new JsonSyntaxException("Expected at least one recipe id in 'recipe' or 'recipes'");
        }

        return new Instance(player, recipes);
    }

    public void trigger(ServerPlayer player, ResourceLocation recipeId) {
        this.trigger(player, instance -> instance.matches(recipeId));
    }

    public void trigger(MetaMachine machine, ResourceLocation recipeId) {
        if (!WATCHED_RECIPES.contains(recipeId)) {
            return; // cheap check first
        }

        var player = TriggerUtils.findOwnerOrNearestPlayer(machine);

        if (player != null) {
            trigger(player, recipeId);
        }
    }

    public static class Instance extends AbstractCriterionTriggerInstance {

        private final Set<ResourceLocation> recipes;

        public Instance(ContextAwarePredicate player, Collection<ResourceLocation> recipes) {
            super(ID, player);

            if (recipes.isEmpty()) {
                throw new IllegalArgumentException("RecipeExecutedTrigger requires at least one recipe id");
            }

            this.recipes = Set.copyOf(recipes);
            WATCHED_RECIPES.addAll(this.recipes);
        }

        public static Instance recipes(ResourceLocation... recipes) {
            return new Instance(ContextAwarePredicate.ANY, Arrays.asList(recipes));
        }

        public boolean matches(ResourceLocation recipeId) {
            return recipes.contains(recipeId);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);

            JsonArray array = new JsonArray();
            recipes.stream()
                .map(ResourceLocation::toString)
                .sorted()
                .forEach(array::add);

            json.add("recipes", array);
            return json;
        }
    }
}
