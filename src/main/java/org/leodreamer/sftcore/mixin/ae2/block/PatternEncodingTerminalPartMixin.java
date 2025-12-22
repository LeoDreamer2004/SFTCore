package org.leodreamer.sftcore.mixin.ae2.block;

import appeng.api.parts.IPartItem;
import appeng.parts.encoding.PatternEncodingLogic;
import appeng.parts.encoding.PatternEncodingTerminalPart;
import appeng.parts.reporting.AbstractTerminalPart;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PatternEncodingTerminalPart.class)
public class PatternEncodingTerminalPartMixin extends AbstractTerminalPart {
    @Shadow(remap = false)
    @Final
    private PatternEncodingLogic logic;

    private PatternEncodingTerminalPartMixin(IPartItem<?> partItem) {
        super(partItem);
    }

    @Inject(method = "addAdditionalDrops", at = @At("HEAD"), cancellable = true, remap = false)
    private void removeBlankPatternDrop(List<ItemStack> drops, boolean wrenched, CallbackInfo ci) {
        super.addAdditionalDrops(drops, wrenched);

        for (var is : logic.getEncodedPatternInv()) {
            drops.add(is);
        }

        ci.cancel();
    }
}
