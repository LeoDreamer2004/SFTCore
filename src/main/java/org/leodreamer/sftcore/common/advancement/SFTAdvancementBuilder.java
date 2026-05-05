package org.leodreamer.sftcore.common.advancement;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.advancement.trigger.FormedGTMultiblockTrigger;
import org.leodreamer.sftcore.common.advancement.trigger.RecipeExecutedTrigger;
import org.leodreamer.sftcore.util.RLUtils;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Arrays;

public class SFTAdvancementBuilder {

    private final String id;
    private final Advancement.Builder builder;
    private boolean show = true;
    private boolean hidden = false;
    private FrameType frame = FrameType.TASK;
    private static final ResourceLocation BACKGROUND = SFTCore.id("textures/gui/base/advancement_background.png");
    public static final Object2ObjectArrayMap<String, String> ADVANCEMENT_LANG = new Object2ObjectArrayMap<>();

    private SFTAdvancementBuilder(String id) {
        this.id = id;
        this.builder = Advancement.Builder.advancement();
    }

    public static SFTAdvancementBuilder create(String id) {
        return new SFTAdvancementBuilder(id);
    }

    public SFTAdvancementBuilder parent(Advancement parent) {
        builder.parent(parent);
        return this;
    }

    public Advancement build() {
        return builder.build(SFTCore.id(id));
    }

    /// ---------- Display ---------- ///

    public SFTAdvancementBuilder show(boolean show) {
        this.show = show;
        return this;
    }

    public SFTAdvancementBuilder silent() {
        return show(false);
    }

    public SFTAdvancementBuilder hidden() {
        this.hidden = true;
        return this;
    }

    public SFTAdvancementBuilder frame(FrameType frame) {
        this.frame = frame;
        return this;
    }

    public SFTAdvancementBuilder goal() {
        return frame(FrameType.GOAL);
    }

    public SFTAdvancementBuilder challenge() {
        return frame(FrameType.CHALLENGE);
    }

    public SFTAdvancementBuilder display(ItemLike icon, String title, String description) {
        var titleKey = "advancements.sftcore." + id + ".title";
        var descriptionKey = "advancements.sftcore." + id + ".description";
        ADVANCEMENT_LANG.put(titleKey, title);
        ADVANCEMENT_LANG.put(descriptionKey, description);

        builder.display(
            icon,
            Component.translatable(titleKey),
            Component.translatable(descriptionKey),
            BACKGROUND,
            frame,
            show,
            show,
            hidden
        );
        return this;
    }

    /// ---------- Criteria ---------- ///

    public SFTAdvancementBuilder criterion(String key, CriterionTriggerInstance instance) {
        builder.addCriterion(key, instance);
        return this;
    }

    public SFTAdvancementBuilder any() {
        builder.requirements(RequirementsStrategy.OR);
        return this;
    }

    public SFTAdvancementBuilder free() {
        return obtain();
    }

    private static String rlToStr(ResourceLocation rl) {
        return rl.getNamespace() + "_" + rl.getPath();
    }

    public SFTAdvancementBuilder obtain(ItemLike... what) {
        // has_{namespace}_{path}*
        var key = new StringBuilder("has_");
        for (int i = 0; i < what.length; i++) {
            if (i > 0) {
                key.append("_");
            }
            var rl = RLUtils.getItemRL(what[i]);
            key.append(rlToStr(rl));
        }

        return criterion(key.toString(), InventoryChangeTrigger.TriggerInstance.hasItems(what));
    }

    public SFTAdvancementBuilder onFormed(MachineDefinition controller) {
        var id = controller.getId();
        return criterion(
            "formed_" + rlToStr(controller.getId()),
            FormedGTMultiblockTrigger.Instance.machine(id)
        );
    }

    public SFTAdvancementBuilder onRecipeExecuted(GTRecipeType recipeType, String... recipeIds) {
        // execute_{recipeType}_{recipeId}*
        var typeId = recipeType.registryName;

        return criterion(
            "execute_" + rlToStr(typeId) + "_" + String.join("_", recipeIds),
            RecipeExecutedTrigger.Instance.recipes(
                Arrays.stream(recipeIds)
                    .map(id -> typeId.withSuffix("/" + id))
                    .toArray(ResourceLocation[]::new)
            )
        );
    }
}
