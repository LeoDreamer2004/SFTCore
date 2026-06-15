package org.leodreamer.sftcore.common.item.cepattern;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

public class CEPatternEditorWidget extends WidgetGroup {

    private static final int ADD_RECIPE = 20;
    private static final int REMOVE_RECIPE = 21;
    private static final int SET_MULTIPLIER = 22;

    private final CEPatternUIProvider provider;

    public CEPatternEditorWidget(
        CEPatternUIProvider provider,
        int x,
        int y,
        int width,
        int height
    ) {
        super(x, y, width, height);
        this.provider = provider;
        setId(CEPatternUIProvider.EDITOR_WIDGET_ID);
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

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        switch (id) {
            case ADD_RECIPE -> provider.addRecipe(buffer.readResourceLocation());
            case REMOVE_RECIPE -> provider.removeRecipe(buffer.readVarInt());
            case SET_MULTIPLIER -> provider.setMultiplier(buffer.readVarInt(), buffer.readUtf());
            default -> super.handleClientAction(id, buffer);
        }
    }
}
