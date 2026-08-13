package org.leodreamer.sftcore.integration.ponder.element;

import brachy.modularui.screen.EmbedHandler;
import brachy.modularui.screen.ModularPanel;
import brachy.modularui.screen.ModularScreen;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.element.AnimatedOverlayElementBase;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec3;
import org.leodreamer.sftcore.SFTCore;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A render-only ModularUI embed anchored to a position in a Ponder scene.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MUIWindowElement extends AnimatedOverlayElementBase {

    private final Vec3 position;
    private final Pointing pointing;
    private final int width;
    private final int height;
    private final Supplier<? extends ModularPanel<?>> panelFactory;

    private ModularScreen muiScreen;

    public MUIWindowElement(
        Vec3 position,
        Pointing pointing,
        int width,
        int height,
        Supplier<? extends ModularPanel<?>> panelFactory
    ) {
        this.position = position;
        this.pointing = pointing;
        this.width = width;
        this.height = height;
        this.panelFactory = panelFactory;
    }

    @Override
    public void render(PonderScene scene, PonderUI screen, GuiGraphics graphics, float partialTicks, float fade) {
        if (fade < 1 / 16f) {
            return;
        }

        if (muiScreen == null) {
            var panel = Objects.requireNonNull(panelFactory.get(), "Ponder MUI panel must not be null");
            muiScreen = ModularScreen.createEmbed(SFTCore.MOD_ID, panel, width, height);
        }

        var screenPosition = scene.getTransform().sceneToScreen(position, partialTicks);
        float xFade = pointing == Pointing.RIGHT ? -1 : pointing == Pointing.LEFT ? 1 : 0;
        float yFade = pointing == Pointing.DOWN ? -1 : pointing == Pointing.UP ? 1 : 0;
        xFade *= 10 * (1 - fade);
        yFade *= 10 * (1 - fade);

        var pose = graphics.pose();
        pose.pushPose();
        pose.translate(screenPosition.x + xFade, screenPosition.y + yFade, 400);
        PonderUI.renderSpeechBox(graphics, 0, 0, width, height, false, pointing, true);
        pose.translate(0, 0, 100);
        EmbedHandler.drawEmbedNoVanillaElements(muiScreen, graphics, partialTicks);
        pose.popPose();
    }
}
