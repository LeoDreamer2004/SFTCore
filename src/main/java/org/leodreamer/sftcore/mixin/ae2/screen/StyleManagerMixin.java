package org.leodreamer.sftcore.mixin.ae2.screen;

import appeng.client.gui.style.StyleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(StyleManager.class)
public class StyleManagerMixin {

    @ModifyVariable(method = "loadStyleDoc", at = @At("HEAD"), argsOnly = true, remap = false)
    private static String loadStyleDocHooks(String path) {
        if (path.contains("wireless_pattern_encoding_terminal.json")) {
            return "/screens/wtlib/wireless_pattern_encoding_terminal_sft.json";
        } else if (path.contains("pattern_encoding_terminal.json")) {
            return "/screens/terminals/pattern_encoding_terminal_sft.json";
        } else if (path.contains("ex_pattern_provider.json")) {
            return "/screens/ex_pattern_provider_sft.json";
        } else if (path.contains("pattern_provider.json")) {
            return "/screens/pattern_provider_sft.json";
        }
        return path;
    }
}
