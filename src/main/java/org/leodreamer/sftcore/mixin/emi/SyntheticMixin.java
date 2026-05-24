package org.leodreamer.sftcore.mixin.emi;

import com.google.common.collect.Lists;
import com.gregtechceu.gtceu.integration.emi.recipe.GTEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.render.EmiTooltipComponents;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.FluidEmiStack;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiFavorite;
import dev.emi.emi.screen.MicroTextRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.common.data.lang.MixinTooltips;
import org.leodreamer.sftcore.integration.emi.EmiCraftingProgress;
import org.leodreamer.sftcore.integration.emi.IGTEmiRecipe;
import org.leodreamer.sftcore.util.GTMachineUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static org.leodreamer.sftcore.integration.emi.EmiRecipeAutocraft.openedMachine;

@Mixin(value = EmiFavorite.Synthetic.class, remap = false)
public class SyntheticMixin extends EmiFavorite {
    @Shadow
    @Final
    public long amount;

    public SyntheticMixin(EmiIngredient stack, @Nullable EmiRecipe recipe) {
        super(stack, recipe);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void sftcore$renderCraftingProgress(
        GuiGraphics raw,
        int x,
        int y,
        float delta,
        int flags,
        CallbackInfo ci
    ) {
        if (!EmiCraftingProgress.isInProgressSynthetic((EmiFavorite.Synthetic) (Object) this)) {
            return;
        }

        var context = EmiDrawContext.wrap(raw);
        stack.render(raw, x, y, delta, flags & (~EmiIngredient.RENDER_AMOUNT));

        boolean volume = !stack.getEmiStacks().isEmpty()
            && stack.getEmiStacks().get(0) instanceof FluidEmiStack;

        // render the crafting num at the right above
        MicroTextRenderer.render(
            context,
            amount,
            volume,
            18,
            x + 17,
            y + 7,
            EmiCraftingProgress.COLOR
        );
    }

    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true, remap = false)
    private void sftcore$addCraftingProgressTooltip(CallbackInfoReturnable<List<ClientTooltipComponent>> cir) {
        if (!EmiCraftingProgress.isInProgressSynthetic((EmiFavorite.Synthetic) (Object) this)) {
            return;
        }

        var list = Lists.newArrayList(cir.getReturnValue());
        list.add(EmiTooltipComponents.of(
            Component.translatable(MixinTooltips.EMI_CRAFTING, amount)
                .withStyle(ChatFormatting.AQUA)
        ));
        cir.setReturnValue(list);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void highlightCurrentMachineAvailable(
        GuiGraphics raw,
        int x,
        int y,
        float delta,
        int flags,
        CallbackInfo ci
    ) {
        if (openedMachine != null && recipe instanceof GTEmiRecipe gtRecipe) {
            var recipeType = ((IGTEmiRecipe) gtRecipe).sftcore$recipe().recipeType;
            if (GTMachineUtils.guessRecipe(openedMachine, recipeType).ok()) {
                var context = EmiDrawContext.wrap(raw);
                context.fill(x - 1, y - 1, 18, 18, 0x50FFD700);
            }
        }
    }
}
