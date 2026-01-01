package org.leodreamer.sftcore.common.recipe.condition;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.feature.IKineticMachine;
import org.leodreamer.sftcore.api.machine.multiblock.WorkableKineticMultiblockMachine;
import org.leodreamer.sftcore.common.data.SFTRecipeConditions;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeCondition;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;

import net.minecraft.network.chat.Component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@DataGenScanned
public class RPMCondition extends RecipeCondition {

    public static final Codec<RPMCondition> CODEC = RecordCodecBuilder.create(
        instance -> RecipeCondition.isReverse(instance)
            .and(Codec.FLOAT.fieldOf("rpm").forGetter(val -> val.rpm))
            .apply(instance, RPMCondition::new)
    );
    public static final RPMCondition INSTANCE = new RPMCondition();
    private float rpm;

    public RPMCondition(boolean isReverse, float rpm) {
        super(isReverse);
        this.rpm = rpm;
    }

    public RPMCondition(float rpm) {
        this.rpm = rpm;
    }

    @Override
    public RecipeConditionType<?> getType() {
        return SFTRecipeConditions.RPM;
    }

    @RegisterLanguage("Requires Minimum RPM: %d")
    static final String TOOLTIP = "sftcore.recipe.condition.rpm";

    @Override
    public Component getTooltips() {
        return Component.translatable(TOOLTIP, rpm);
    }

    @Override
    public boolean testCondition(@NotNull GTRecipe recipe, @NotNull RecipeLogic recipeLogic) {
        if (
            recipeLogic.machine instanceof IKineticMachine kineticMachine &&
                Math.abs(kineticMachine.getKineticHolder().getSpeed()) >= rpm
        ) {
            return true;
        }
        if (recipeLogic.machine instanceof WorkableKineticMultiblockMachine controller) {
            return controller.speed >= rpm;
        }
        return false;
    }

    @Override
    public RecipeCondition createTemplate() {
        return new RPMCondition();
    }
}
