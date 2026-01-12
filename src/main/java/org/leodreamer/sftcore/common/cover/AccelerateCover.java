package org.leodreamer.sftcore.common.cover;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;

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
import net.minecraft.network.chat.Component;

import lombok.Getter;
import org.apache.commons.lang3.math.Fraction;

import javax.annotation.Nullable;

@DataGenScanned
public class AccelerateCover extends DetectorCover {

    public static final int[] TIERS = GTValues.tiersBetween(GTValues.LV, GTValues.LuV);

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
                .noneMatch(AccelerateCover.class::isInstance);
    }

    private static Fraction getAccelerationRate(int tier) {
        return tier < GTValues.IV ? Fraction.getFraction(tier, 2) :
            Fraction.getFraction(tier * 2, 3);
    }

    @Override
    protected void update() {
        // for every N tick, the recipe progress is increased by M ticks
        var rate = getAccelerationRate(tier);
        if (coverHolder.getOffsetTimer() % rate.getDenominator() == 0) {
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
        logic.setProgress(logic.getProgress() + rate.getNumerator());
    }

    @Nullable
    private RecipeLogic getRecipeLogic() {
        return GTCapabilityHelper.getRecipeLogic(coverHolder.getLevel(), coverHolder.getPos(), null);
    }

    @RegisterLanguage("Accelerate the recipe by §e%d%%§r")
    private static final String TOOLTIP = "item.sftcore.accelerate_cover.tooltip";

    public static SFTTooltipsBuilder getTooltips(int tier) {
        var rate = getAccelerationRate(tier);
        int accel = 100 * rate.getNumerator() / rate.getDenominator();
        return SFTTooltipsBuilder.of()
            .insert(Component.translatable(TOOLTIP, accel))
            .textureComeFrom("Thermal Expansion");
    }
}
