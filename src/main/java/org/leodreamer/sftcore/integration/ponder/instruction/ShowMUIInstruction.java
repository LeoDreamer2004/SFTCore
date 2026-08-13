package org.leodreamer.sftcore.integration.ponder.instruction;

import org.leodreamer.sftcore.integration.ponder.element.MUIWindowElement;

import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.FadeInOutInstruction;

public class ShowMUIInstruction extends FadeInOutInstruction {

    private final MUIWindowElement element;

    public ShowMUIInstruction(MUIWindowElement element, int duration) {
        super(duration);
        this.element = element;
    }

    @Override
    protected void show(PonderScene scene) {
        scene.addElement(element);
        element.setVisible(true);
    }

    @Override
    protected void hide(PonderScene scene) {
        element.setVisible(false);
    }

    @Override
    protected void applyFade(PonderScene scene, float fade) {
        element.setFade(fade);
    }
}
