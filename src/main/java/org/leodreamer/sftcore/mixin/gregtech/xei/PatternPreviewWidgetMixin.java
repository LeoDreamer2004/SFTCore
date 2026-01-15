package org.leodreamer.sftcore.mixin.gregtech.xei;

import org.leodreamer.sftcore.integration.emi.IMultiblockInputProvider;

import com.gregtechceu.gtceu.api.gui.widget.PatternPreviewWidget;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Mixin(PatternPreviewWidget.class)
public class PatternPreviewWidgetMixin implements IMultiblockInputProvider {

    @Shadow(remap = false)
    @Final
    public PatternPreviewWidget.MBPattern[] patterns;

    @Unique
    private Consumer<Stream<ItemStack>> sftcore$onUpdate;

    @Unique
    @Override
    public void sftcore$setInputsUpdateResponser(Consumer<Stream<ItemStack>> onUpdate) {
        sftcore$onUpdate = onUpdate;
    }

    @Inject(method = "setPage", at = @At("TAIL"), remap = false)
    private void notifyInputsUpdate(int index, CallbackInfo ci) {
        if (sftcore$onUpdate == null) return;
        var parts = ((MBPatternAccessor) patterns[index]).getParts();
        var input = parts.stream().map(stacks -> stacks.get(0));
        sftcore$onUpdate.accept(input);
    }

    @Mixin(PatternPreviewWidget.MBPattern.class)
    interface MBPatternAccessor {

        @Accessor(value = "parts", remap = false)
        List<List<ItemStack>> getParts();
    }
}
