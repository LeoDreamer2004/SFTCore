package org.leodreamer.sftcore.common.item.wildcard.feature;

import org.leodreamer.sftcore.api.gui.SFTGuiTextures;
import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternUIProvider;

import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.network.chat.Component;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.api.widget.IWidget;
import brachy.modularui.drawable.GuiTextures;
import brachy.modularui.drawable.Rectangle;
import brachy.modularui.utils.Alignment;
import brachy.modularui.utils.Color;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.value.sync.PhantomItemSlotSyncHandler;
import brachy.modularui.widget.EmptyWidget;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.slot.ModularSlot;
import brachy.modularui.widgets.slot.PhantomItemSlot;

public abstract class WildcardComponentUI {

    /**
     * Add the content to the UI line
     */
    protected abstract void addLineContent(
        Flow row,
        PanelSyncManager syncManager,
        String lineSyncKey
    );

    /**
     * Background of this line
     */
    protected abstract Rectangle rowBackground();

    /**
     * Generate a tooltip to the wildcard item
     */
    public abstract Component createTooltip();

    /**
     * Save callback
     */
    public void onSave() {}

    public IWidget createLine(
        int index,
        Runnable onRemove,
        PanelSyncManager syncManager,
        String lineSyncKey
    ) {
        var row = Flow.row()
            .width(WildcardPatternUIProvider.ROW_WIDTH)
            .height(24)
            .background(rowBackground());
        row.child(Text.str(Integer.toString(index)).asWidget().width(10).textAlign(Alignment.Center));

        // content
        addLineContent(row, syncManager, lineSyncKey);

        // stretch
        var spacer = new EmptyWidget();
        spacer.resizer().expanded(true);
        row.child(spacer);

        // remove
        var remove = WildcardPatternUIProvider.createButton(18, SFTGuiTextures.CLOSE, onRemove)
            .background(GuiTextures.MC_BUTTON)
            .hoverBackground(GuiTextures.MC_BUTTON_HOVERED);
        row.child(remove);
        return row;
    }

    protected IWidget createTypeButton(int width, String labelKey) {
        return Text.lang(labelKey)
            .asWidget()
            .width(width)
            .height(16)
            .textAlign(Alignment.Center)
            .color(Color.WHITE.main);
    }

    protected <T extends CustomItemStackHandler> PhantomItemSlotSyncHandler registerSampleSlot(
        T sampleSlot,
        Class<T> handlerType,
        PanelSyncManager syncManager,
        String lineSyncKey
    ) {
        var sample = new ModularSlot(sampleSlot, 0)
            .changeListener((stack, amount, client, init) -> sampleSlot.setStackInSlot(0, stack))
            .accessibility(true, false);
        var handler = syncManager.getOrCreateSyncHandler(
            lineSyncKey + "_" + handlerType.getSimpleName(),
            PhantomItemSlotSyncHandler.class,
            () -> new PhantomItemSlotSyncHandler(sample)
        );
        var itemHandler = handler.getSlot().getItemHandler();
        if (!handlerType.isInstance(itemHandler)) {
            throw new IllegalStateException(
                "Sample slot " + lineSyncKey + " expected " + handlerType.getName() +
                    ", got " + itemHandler.getClass().getName()
            );
        }
        return handler;
    }

    protected <T extends CustomItemStackHandler> T sampleSlotHandler(
        PhantomItemSlotSyncHandler syncHandler,
        Class<T> handlerType
    ) {
        return handlerType.cast(syncHandler.getSlot().getItemHandler());
    }

    protected IWidget createSampleSlot(PhantomItemSlotSyncHandler syncHandler) {
        return new PhantomItemSlot()
            .size(18)
            .background(GTGuiTextures.SLOT)
            .syncHandler(syncHandler);
    }
}
