package org.leodreamer.sftcore.common.machine.multiblock.part;

import org.leodreamer.sftcore.api.gui.gas.GasGuiHelper;
import org.leodreamer.sftcore.common.machine.trait.gas.NotifiableGasTank;
import org.leodreamer.sftcore.integration.ae2.gui.AEGasStackDisplayWidget;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.integration.ae2.gui.AEKeyStorageSyncHandler;
import com.gregtechceu.gtceu.integration.ae2.gui.ScrollPreservingGrid;
import com.gregtechceu.gtceu.integration.ae2.utils.KeyStorage;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.sync.BooleanSyncValue;
import brachy.modularui.value.sync.DynamicLinkedSyncHandler;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widget.ParentWidget;
import brachy.modularui.widget.scroll.VerticalScrollData;
import brachy.modularui.widgets.TextWidget;
import brachy.modularui.widgets.dynamic.DynamicWidget;
import brachy.modularui.widgets.layout.Flow;
import mekanism.api.chemical.gas.GasStack;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Copy version for gas from {@link com.gregtechceu.gtceu.integration.ae2.machine.MEOutputHatchPartMachine}
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEGasOutputHatchPartMachine extends MEGasHatchPartMachine {

    @SaveField
    private KeyStorage internalBuffer;

    public MEGasOutputHatchPartMachine(BlockEntityCreationInfo info) {
        super(info, IO.OUT);
    }

    @Override
    protected NotifiableGasTank createTank(long initialCapacity, int slots) {
        this.internalBuffer = new KeyStorage();
        return new InaccessibleInfiniteGasTank();
    }

    @Override
    public void onMachineDestroyed() {
        super.onMachineDestroyed();
        var grid = getMainNode().getGrid();
        if (grid != null && !internalBuffer.isEmpty()) {
            internalBuffer.insertInventory(grid.getStorageService().getInventory(), actionSource);
        }
    }

    @Override
    protected boolean shouldSubscribe() {
        return super.shouldSubscribe() && !internalBuffer.isEmpty();
    }

    @Override
    protected void autoIO() {
        if (!shouldSyncME()) {
            return;
        }
        if (updateMEStatus()) {
            var grid = getMainNode().getGrid();
            if (grid != null && !internalBuffer.isEmpty()) {
                internalBuffer.insertInventory(grid.getStorageService().getInventory(), actionSource);
            }
            updateTankSubscription();
        }
    }

    @Override
    public void buildMainUI(
        ParentWidget<?> mainWidget, PosGuiData guiData, PanelSyncManager syncManager,
        UISettings settings
    ) {
        BooleanSyncValue isOnlineValue = new BooleanSyncValue(this::isOnline, this::setOnline);
        syncManager.syncValue("is_online", isOnlineValue);

        var flow = Flow.col().coverChildren();
        flow.child(
            Text.dynamic(
                () -> isOnlineValue.getBoolValue() ?
                    Component.translatable("gtceu.gui.me_network.online") :
                    Component.translatable("gtceu.gui.me_network.offline")
            )
                .asWidget().marginTop(2).marginBottom(4)
        );

        var storageSyncHandler = new AEKeyStorageSyncHandler(internalBuffer);
        syncManager.syncValue("ae_output_display", storageSyncHandler);

        int[] savedScroll = { 0 };
        var dynamicHandler = new DynamicLinkedSyncHandler<>(storageSyncHandler)
            .widgetProvider((sm, value) -> {
                var col = Flow.col().leftRel(0.5f).coverChildrenHeight();
                var list = value.getValue();
                if (list.isEmpty()) {
                    return col.child(new TextWidget<>(Text.lang("gtceu.gui.waiting_list_empty")));
                }
                col.child(new TextWidget<>(Text.lang("gtceu.gui.waiting_list")).margin(0, 2));
                col.child(
                    new ScrollPreservingGrid(savedScroll)
                        .size(167, 80)
                        .scrollable(new VerticalScrollData())
                        .gridOfSizeWidth(9, 1, (x, y, index) -> new AEGasStackDisplayWidget(list, index))
                );
                return col;
            });

        flow.child(
            new DynamicWidget<>()
                .syncHandler(dynamicHandler)
                .size(167, 80)
        );

        mainWidget.child(flow);
    }

    private class InaccessibleInfiniteGasTank extends NotifiableGasTank {

        public InaccessibleInfiniteGasTank() {
            super(1, Long.MAX_VALUE, IO.OUT, IO.NONE);
            internalBuffer.setOnContentsChanged(this::onContentsChanged);
        }

        @Override
        public int getTanks() {
            return 128;
        }

        @Override
        public List<Object> getContents() {
            return Collections.emptyList();
        }

        @Override
        public double getTotalContentAmount() {
            return 0;
        }

        @Override
        public boolean shouldSearchContent() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public GasStack getChemicalInTank(int tank) {
            return GasStack.EMPTY;
        }

        @Override
        public void setChemicalInTank(int tank, GasStack stack) {}

        @Override
        public long getTankCapacity(int tank) {
            return Long.MAX_VALUE;
        }

        @Override
        public boolean isValid(int tank, GasStack stack) {
            return true;
        }

        @Override
        public List<GasStack> handleRecipeInner(IO io, GTRecipe recipe, List<GasStack> left, boolean simulate) {
            if (io != IO.OUT) {
                return left;
            }

            for (var it = left.iterator(); it.hasNext();) {
                var gas = it.next();

                if (gas == null || gas.isEmpty()) {
                    it.remove();
                    continue;
                }

                long accepted = insertGas(gas, simulate);
                if (accepted <= 0) {
                    continue;
                }

                long remaining = gas.getAmount() - accepted;
                if (remaining <= 0) {
                    it.remove();
                } else {
                    gas.setAmount(remaining);
                }
            }

            return left;
        }

        private long insertGas(GasStack gas, boolean simulate) {
            var key = GasGuiHelper.getGasKey(gas);
            if (key == null) {
                return 0;
            }

            long amount = gas.getAmount();
            if (amount <= 0) {
                return 0;
            }

            long oldAmount = internalBuffer.storage.getOrDefault(key, 0L);
            long accepted = Math.min(Long.MAX_VALUE - oldAmount, amount);

            if (accepted > 0 && !simulate) {
                internalBuffer.storage.put(key, oldAmount + accepted);
                internalBuffer.onChanged();
            }

            return accepted;
        }
    }
}
