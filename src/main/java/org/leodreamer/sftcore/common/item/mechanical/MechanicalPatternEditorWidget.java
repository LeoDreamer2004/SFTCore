package org.leodreamer.sftcore.common.item.mechanical;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.leodreamer.sftcore.integration.emi.gui.MechanicalPatternEmiViewer;

public class MechanicalPatternEditorWidget extends WidgetGroup {

    private static final int ADD_RECIPE = 20;
    private static final int REMOVE_RECIPE = 21;
    private static final int SET_MULTIPLIER = 22;
    private static final int GENERATE_PATTERN = 23;

    private final MechanicalEncapsulationPatternUIProvider provider;

    public MechanicalPatternEditorWidget(
        MechanicalEncapsulationPatternUIProvider provider,
        int x,
        int y,
        int width,
        int height
    ) {
        super(x, y, width, height);
        this.provider = provider;
        setId(MechanicalEncapsulationPatternUIProvider.EDITOR_WIDGET_ID);
    }

    public void requestAddRecipe(ResourceLocation id) {
        provider.addRecipe(id);
        if (isRemote()) {
            writeClientAction(ADD_RECIPE, buffer -> buffer.writeResourceLocation(id));
        }
    }

    public void requestRemoveRecipe(int index) {
        provider.removeRecipe(index);
        if (isRemote()) {
            writeClientAction(REMOVE_RECIPE, buffer -> buffer.writeVarInt(index));
        }
    }

    public void requestSetMultiplier(int index, String text) {
        provider.setMultiplier(index, text);
        if (isRemote()) {
            writeClientAction(SET_MULTIPLIER, buffer -> {
                buffer.writeVarInt(index);
                buffer.writeUtf(text);
            });
        }
    }

    public void requestGeneratePattern() {
        if (isRemote()) {
            writeClientAction(GENERATE_PATTERN, buffer -> {});
        } else {
            provider.generatePattern();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void openRecipe(ResourceLocation id) {
        MechanicalPatternEmiViewer.openRecipe(id);
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        if (id == ADD_RECIPE) {
            provider.addRecipe(buffer.readResourceLocation());
        } else if (id == REMOVE_RECIPE) {
            provider.removeRecipe(buffer.readVarInt());
        } else if (id == SET_MULTIPLIER) {
            provider.setMultiplier(buffer.readVarInt(), buffer.readUtf());
        } else if (id == GENERATE_PATTERN) {
            provider.generatePattern();
        } else {
            super.handleClientAction(id, buffer);
        }
    }
}
