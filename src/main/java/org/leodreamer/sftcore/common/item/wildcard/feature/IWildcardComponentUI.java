package org.leodreamer.sftcore.common.item.wildcard.feature;

import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternUIProvider;

import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.network.chat.Component;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.api.widget.IWidget;
import brachy.modularui.drawable.Rectangle;
import brachy.modularui.utils.Alignment;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.value.sync.PhantomItemSlotSyncHandler;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.slot.ModularSlot;
import brachy.modularui.widgets.slot.PhantomItemSlot;

public abstract class IWildcardComponentUI {

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
        addLineContent(row, syncManager, lineSyncKey);
        row.child(createDeleteButton(onRemove));
        return row;
    }

    protected abstract void addLineContent(
        Flow row,
        PanelSyncManager syncManager,
        String lineSyncKey
    );

    protected abstract Rectangle rowBackground();

    protected abstract String deleteTooltipKey();

    protected IWidget createTypeButton(int width, String labelKey) {
        return WildcardPatternUIProvider.createButton(width, Text.lang(labelKey).scale(0.55f), () -> {});
    }

    protected <T extends CustomItemStackHandler> PhantomItemSlotSyncHandler registerSampleSlot(
        T sampleSlot,
        Class<T> handlerType,
        PanelSyncManager syncManager,
        String lineSyncKey
    ) {
        var handler = syncManager.getOrCreateSyncHandler(
            typedLineSyncKey(lineSyncKey, handlerType), PhantomItemSlotSyncHandler.class,
            () -> new PhantomItemSlotSyncHandler(createSampleModularSlot(sampleSlot))
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
            .syncHandler(syncHandler)
            .background(GTGuiTextures.SLOT);
    }

    private static String typedLineSyncKey(
        String lineSyncKey,
        Class<? extends CustomItemStackHandler> handlerType
    ) {
        return lineSyncKey + "_" + handlerType.getSimpleName();
    }

    private static ModularSlot createSampleModularSlot(CustomItemStackHandler sampleSlot) {
        return new ModularSlot(sampleSlot, 0)
            .changeListener((stack, amount, client, init) -> sampleSlot.setStackInSlot(0, stack))
            .ignoreMaxStackSize(true)
            .accessibility(true, false);
    }

    protected IWidget createDeleteButton(Runnable onRemove) {
        return WildcardPatternUIProvider.createButton(
            18, Text.str("X").scale(0.55f), onRemove
        ).tooltipDynamic(tooltip -> tooltip.addLine(Text.lang(deleteTooltipKey())));
    }

    public abstract Component createTooltip();

    public void onSave() {}
}
