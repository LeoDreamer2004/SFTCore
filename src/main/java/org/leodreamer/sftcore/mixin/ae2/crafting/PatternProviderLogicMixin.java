package org.leodreamer.sftcore.mixin.ae2.crafting;

import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternLogic;
import org.leodreamer.sftcore.integration.ae2.feature.IPatternClear;
import org.leodreamer.sftcore.integration.ae2.logic.MemoryCardPatternInventoryProxy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.AEKey;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.util.inv.AppEngInternalInventory;
import org.spongepowered.asm.mixin.*;

import java.util.List;
import java.util.Set;

@Mixin(PatternProviderLogic.class)
public abstract class PatternProviderLogicMixin implements IPatternClear {

    @Shadow(remap = false)
    @Final
    private List<IPatternDetails> patterns;

    @Shadow(remap = false)
    @Final
    private Set<AEKey> patternInputs;

    @Shadow(remap = false)
    @Final
    private AppEngInternalInventory patternInventory;

    @Shadow(remap = false)
    @Final
    private PatternProviderLogicHost host;

    @Shadow(remap = false)
    @Final
    private IManagedGridNode mainNode;

    @Shadow(remap = false)
    protected abstract void clearPatternInventory(Player player);

    /**
     * @author LeoDreamer
     * @reason Let the pattern support wildcard patterns
     */
    @Overwrite(remap = false)
    public void updatePatterns() {
        patterns.clear();
        patternInputs.clear();
        var level = host.getBlockEntity().getLevel();

        for (var stack : patternInventory) {
            // injected by SFT
            if (stack.is(SFTItems.WILDCARD_PATTERN.asItem())) {
                WildcardPatternLogic.decodePatterns(stack, level)
                    .forEach(this::sftcore$updatePattern);
                continue;
            }
            var details = PatternDetailsHelper.decodePattern(stack, level);
            sftcore$updatePattern(details);
        }

        ICraftingProvider.requestUpdate(mainNode);
    }

    @Unique
    private void sftcore$updatePattern(IPatternDetails details) {
        if (details != null) {
            patterns.add(details);
            for (var input : details.getInputs()) {
                for (var inputCandidate : input.getPossibleInputs()) {
                    patternInputs.add(inputCandidate.what().dropSecondary());
                }
            }
        }
    }

    /**
     * @author LeoDreamer
     * @reason Use the safe proxy
     */
    @Overwrite(remap = false)
    public void exportSettings(CompoundTag output) {
        new MemoryCardPatternInventoryProxy(patternInventory, host.getBlockEntity().getLevel()).exportSettings(output);
    }

    @Unique
    @Override
    public void sftcore$clearPatterns(Player player) {
        if (player != null) {
            clearPatternInventory(player);
        }
    }

    // No need to overwrite importSettings here as it is totally same.
}
