package org.leodreamer.sftcore.mixin.ae2.screen;

import org.leodreamer.sftcore.common.data.lang.MixinTooltips;
import org.leodreamer.sftcore.integration.ae2.feature.IPatternMultiply;
import org.leodreamer.sftcore.integration.ae2.feature.IVirtualCatalystEncoding;
import org.leodreamer.sftcore.integration.ae2.gui.TinyTextButton;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import appeng.client.gui.WidgetContainer;
import appeng.client.gui.me.items.EncodingModePanel;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.me.items.ProcessingEncodingPanel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ProcessingEncodingPanel.class, remap = false)
public abstract class ProcessingEncodingPanelMixin extends EncodingModePanel {

    @Unique
    private Button sftcore$x2Btn;

    @Unique
    private Button sftcore$x64Btn;

    @Unique
    private Button sftcore$halfBtn;

    @Unique
    private TinyTextButton sftcore$virtualCatalystBtn;

    private ProcessingEncodingPanelMixin(
        PatternEncodingTermScreen<?> screen,
        WidgetContainer widgets
    ) {
        super(screen, widgets);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initWithNewBtn(PatternEncodingTermScreen<?> screen, WidgetContainer widgets, CallbackInfo ci) {
        sftcore$x2Btn = sftcore$createMultiplyPatternBtn(Component.literal("x2"), 2);
        sftcore$x64Btn = sftcore$createMultiplyPatternBtn(Component.literal("x64"), 64);
        sftcore$halfBtn = sftcore$createMultiplyPatternBtn(Component.literal("÷2"), -2);
        widgets.add("x2Pattern", sftcore$x2Btn);
        widgets.add("x64Pattern", sftcore$x64Btn);
        widgets.add("halfPattern", sftcore$halfBtn);
        sftcore$virtualCatalystBtn = new TinyTextButton(
            Component.literal("V"), button -> {
                var encoding = (IVirtualCatalystEncoding) menu;
                encoding.sftcore$setVirtualCatalystsEnabled(!encoding.sftcore$getVirtualCatalystsEnabled());
            }
        );
        widgets.add("virtualCatalysts", sftcore$virtualCatalystBtn);
    }

    @Inject(method = "updateBeforeRender", at = @At("TAIL"), remap = false)
    private void sftcore$updateVirtualCatalystButton(CallbackInfo ci) {
        boolean enabled = ((IVirtualCatalystEncoding) menu).sftcore$getVirtualCatalystsEnabled();
        sftcore$virtualCatalystBtn.setTooltips(
            List.of(
                Component.translatable(
                    enabled ? MixinTooltips.VIRTUAL_CATALYSTS_ON :
                        MixinTooltips.VIRTUAL_CATALYSTS_OFF
                ),
                Component.translatable(
                    enabled ? MixinTooltips.VIRTUAL_CATALYSTS_ON_DESC :
                        MixinTooltips.VIRTUAL_CATALYSTS_OFF_DESC
                )
            )
        );
    }

    @Unique
    private Button sftcore$createMultiplyPatternBtn(Component text, int multiplier) {
        var btn = new TinyTextButton(
            text, (b) -> ((IPatternMultiply) menu).sftcore$multiplyPattern(multiplier)
        );

        btn.setTooltips(
            List.of(
                Component.translatable(MixinTooltips.CHANGE_PATTERN),
                multiplier > 0 ? Component.translatable(MixinTooltips.MULTIPLY_PATTERN, multiplier) :
                    Component.translatable(MixinTooltips.DIVIDE_PATTERN, -multiplier)
            )
        );

        return btn;
    }

    @Inject(method = "setVisible", at = @At("TAIL"))
    private void setVisibleWithNewBtn(boolean visible, CallbackInfo ci) {
        if (sftcore$x2Btn != null) {
            sftcore$x2Btn.visible = visible;
            sftcore$x64Btn.visible = visible;
            sftcore$halfBtn.visible = visible;
            sftcore$virtualCatalystBtn.visible = visible;
        }
    }
}
