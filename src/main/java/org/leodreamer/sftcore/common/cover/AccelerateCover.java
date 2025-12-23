package org.leodreamer.sftcore.common.cover;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IOverclockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.cover.detector.DetectorCover;

import net.minecraft.core.Direction;

import lombok.Getter;

import javax.annotation.Nullable;

@DataGenScanned
public class AccelerateCover extends DetectorCover {

    public static final int[] TIERS = GTValues.tiersBetween(GTValues.LV, GTValues.LuV);

    @RegisterLanguage("Accelerate the recipe by §e%d%%§r")
    public static final String TOOLTIP = "item.sftcore.accelerate_cover.tooltip";

    @Getter
    private final int tier;

    public AccelerateCover(
        CoverDefinition definition,
        ICoverable coverHolder,
        Direction attachedSide,
        int tier
    ) {
        super(definition, coverHolder, attachedSide);
        this.tier = tier;
    }

    @Override
    public boolean canAttach() {
        var machine = MetaMachine.getMachine(coverHolder.getLevel(), coverHolder.getPos());
        return super.canAttach() && machine instanceof IOverclockMachine &&
            machine.getCoverContainer().getCovers().stream()
                .noneMatch(cover -> cover instanceof AccelerateCover);
    }

    @Override
    protected void update() {
        if (coverHolder.getOffsetTimer() % 2 == 0) {
            return;
        }

        var logic = getRecipeLogic();
        if (logic == null || !logic.isWorking()) {
            return;
        }
        GTRecipe recipe = logic.getLastOriginRecipe();
        if (recipe == null || recipe.getOutputEUt().getTotalEU() > 0) {
            return;
        }
        logic.setProgress(logic.getProgress() + tier);
    }

    @Nullable
    private RecipeLogic getRecipeLogic() {
        return GTCapabilityHelper.getRecipeLogic(coverHolder.getLevel(), coverHolder.getPos(), null);
    }
}
