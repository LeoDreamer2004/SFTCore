package org.leodreamer.sftcore.integration.emi;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import dev.emi.emi.api.EmiStackProvider;
import dev.emi.emi.api.stack.EmiStackInteraction;
import net.minecraft.client.gui.screens.Screen;
import org.leodreamer.sftcore.api.gui.GasTankWidget;

public class SFTGasStackProvider implements EmiStackProvider<Screen> {
    @Override
    public EmiStackInteraction getStackAt(Screen screen, int x, int y) {
        if (!(screen instanceof ModularUIGuiContainer modular)) {
            return EmiStackInteraction.EMPTY;
        }

        Widget hovered = modular.modularUI.mainGroup.getHoverElement(x, y);
        if (!(hovered instanceof GasTankWidget gasWidget)) {
            return EmiStackInteraction.EMPTY;
        }

        var gas = gasWidget.getGas();
        if (gas == null || gas.isEmpty()) {
            return EmiStackInteraction.EMPTY;
        }

        return new EmiStackInteraction(new SFTGasEmiStack(gas), null, false);
    }
}
