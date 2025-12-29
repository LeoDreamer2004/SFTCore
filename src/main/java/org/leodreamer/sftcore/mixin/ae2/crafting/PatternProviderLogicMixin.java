package org.leodreamer.sftcore.mixin.ae2.crafting;

import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.common.item.behavior.wildcard.impl.WildcardPatternLogic;

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
public class PatternProviderLogicMixin {

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
                var parser = WildcardPatternLogic.on(stack);
                parser.generateAllPatterns(level).forEach(this::sftcore$updatePattern);
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
}
