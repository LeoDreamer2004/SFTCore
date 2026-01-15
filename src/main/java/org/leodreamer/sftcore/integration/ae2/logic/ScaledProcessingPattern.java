package org.leodreamer.sftcore.integration.ae2.logic;

import net.minecraft.world.level.Level;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ScaledProcessingPattern(IPatternDetails original, long multiplier) implements IPatternDetails {

    public ScaledProcessingPattern {
        if (multiplier <= 0) throw new IllegalArgumentException("multiplier must be positive");
    }

    @Override
    public AEItemKey getDefinition() {
        return original.getDefinition();
    }

    @Override
    public IInput[] getInputs() {
        var original = this.original.getInputs();
        var scaled = new IInput[original.length];
        for (int i = 0; i < original.length; i++) {
            scaled[i] = new ScaledInput(original[i], multiplier);
        }
        return scaled;
    }

    @Override
    public GenericStack[] getOutputs() {
        var original = this.original.getOutputs();
        var scaled = new GenericStack[original.length];
        for (int i = 0; i < original.length; i++) {
            if (original[i] != null) {
                scaled[i] = new GenericStack(original[i].what(), original[i].amount() * multiplier);
            }
        }
        return scaled;
    }

    @Override
    public int hashCode() {
        return 31 * original.hashCode() + Long.hashCode(multiplier);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ScaledProcessingPattern sp)) return false;
        return sp.original.equals(this.original) && sp.multiplier == this.multiplier;
    }

    @Override
    public @NotNull String toString() {
        return "Scaled [Multiplier=" + multiplier + " x " + original + "]";
    }

    private record ScaledInput(IInput original, long multiplier) implements IInput {

        @Override
        public GenericStack[] getPossibleInputs() {
            return original.getPossibleInputs();
        }

        @Override
        public long getMultiplier() {
            return original.getMultiplier() * multiplier;
        }

        @Override
        public boolean isValid(AEKey input, Level level) {
            return original.isValid(input, level);
        }

        @Override
        public @Nullable AEKey getRemainingKey(AEKey template) {
            return original.getRemainingKey(template);
        }
    }
}
