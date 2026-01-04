package org.leodreamer.sftcore.mixin.ae2.block;

import org.leodreamer.sftcore.integration.ae2.feature.IPatternClear;
import org.leodreamer.sftcore.integration.ae2.item.MemoryCardUtils;

import net.minecraft.world.entity.player.Player;

import appeng.parts.AEBasePart;
import appeng.parts.crafting.PatternProviderPart;
import com.glodblock.github.extendedae.common.parts.PartExPatternProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AEBasePart.class)
public class AEBasePartMixin {

    @Inject(method = "useMemoryCard", at = @At("HEAD"), remap = false, cancellable = true)
    public void cancelCutIfCutting(Player player, CallbackInfoReturnable<Boolean> cir) {
        System.out.println("Using mode " + MemoryCardUtils.isCutting(player));
        if (MemoryCardUtils.isCutting(player) == MemoryCardUtils.CuttingResult.DANGER) {
            MemoryCardUtils.sendDangerousWarning(player);
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "useMemoryCard", at = @At("RETURN"), remap = false)
    public void allowCut(Player player, CallbackInfoReturnable<Boolean> cir) {
        System.out.println(MemoryCardUtils.isCutting(player));
        if (MemoryCardUtils.isCutting(player) != MemoryCardUtils.CuttingResult.NOT) {
            var self = (Object) this;
            System.out.println(self.getClass());
            System.out.println(self instanceof PatternProviderPart patternProvider);
            if (self instanceof PatternProviderPart patternProvider) {
                System.out.println("Here clearing");
                ((IPatternClear) patternProvider.getLogic()).sftcore$clearPatterns(player);
            } else if (self instanceof PartExPatternProvider patternProvider) {
                ((IPatternClear) patternProvider.getLogic()).sftcore$clearPatterns(player);
            }
        }
    }
}
