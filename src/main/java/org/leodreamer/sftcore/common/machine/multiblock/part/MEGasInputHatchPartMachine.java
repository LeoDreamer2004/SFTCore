package org.leodreamer.sftcore.common.machine.multiblock.part;

import org.leodreamer.sftcore.api.gui.gas.GasGuiHelper;
import org.leodreamer.sftcore.common.machine.trait.gas.ExportOnlyAEGasList;
import org.leodreamer.sftcore.common.machine.trait.gas.NotifiableGasTank;
import org.leodreamer.sftcore.integration.ae2.gui.AEGasConfigWidget;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.feature.IDataStickInteractable;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.stacks.GenericStack;
import brachy.modularui.api.drawable.Text;
import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.sync.BooleanSyncValue;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widget.ParentWidget;
import brachy.modularui.widgets.layout.Flow;
import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Copy version for gas from {@link com.gregtechceu.gtceu.integration.ae2.machine.MEInputHatchPartMachine}
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MEGasInputHatchPartMachine extends MEGasHatchPartMachine implements IDataStickInteractable {

    @Getter
    protected ExportOnlyAEGasList gasHandler;

    public MEGasInputHatchPartMachine(BlockEntityCreationInfo info) {
        super(info, IO.IN);
    }

    @Override
    protected NotifiableGasTank createTank(long initialCapacity, int slots) {
        this.gasHandler = createGasHandler(slots);
        return gasHandler;
    }

    protected ExportOnlyAEGasList createGasHandler(int slots) {
        return new ExportOnlyAEGasList(slots);
    }

    @Override
    public void onMachineDestroyed() {
        super.onMachineDestroyed();
        flushInventory();
    }

    @Override
    protected void autoIO() {
        if (!isWorkingEnabled()) {
            updateTankSubscription();
            return;
        }

        if (!shouldSyncME()) {
            return;
        }

        if (updateMEStatus()) {
            syncME();
            updateTankSubscription();
        }
    }

    protected void syncME() {
        var grid = getMainNode().getGrid();
        if (grid == null) {
            return;
        }

        var network = grid.getStorageService().getInventory();
        gasHandler.syncFromME(network, actionSource);
    }

    protected void flushInventory() {
        var grid = getMainNode().getGrid();
        gasHandler.flushToME(
            grid == null ? null : grid.getStorageService().getInventory(),
            actionSource
        );
    }

    @Override
    public void buildMainUI(
        ParentWidget<?> mainWidget, PosGuiData guiData, PanelSyncManager syncManager,
        UISettings settings
    ) {
        var isOnlineValue = new BooleanSyncValue(this::isOnline, this::setOnline);
        syncManager.syncValue("is_online", isOnlineValue);
        registerConfigActions(syncManager);

        var flow = Flow.col().coverChildren();
        flow.child(
            Text.dynamic(
                () -> isOnlineValue.getBoolValue() ?
                    Component.translatable("gtceu.gui.me_network.online") :
                    Component.translatable("gtceu.gui.me_network.offline")
            )
                .asWidget().marginTop(2).marginBottom(4)
        );
        flow.child(
            new AEGasConfigWidget(gasHandler, CONFIG_SIZE)
                .syncManager(syncManager)
                .size(8 * 18, 2 * (18 * 2 + 2))
        );

        mainWidget.child(flow.center());
    }

    protected void registerConfigActions(PanelSyncManager syncManager) {
        syncManager.registerServerSyncedAction("ae_config_set", packet -> {});
        syncManager.registerServerSyncedAction("ae_config_clear", packet -> {
            int index = packet.readVarInt();
            if (index < 0 || index >= CONFIG_SIZE) {
                return;
            }
            gasHandler.getInventory()[index].setConfig(null);
        });
        syncManager.registerServerSyncedAction("ae_config_amount", packet -> {
            int index = packet.readVarInt();
            long amount = packet.readVarLong();
            if (index < 0 || index >= CONFIG_SIZE) {
                return;
            }
            var slot = gasHandler.getInventory()[index];
            if (GasGuiHelper.isGas(slot.getConfig()) && amount > 0) {
                slot.setConfig(new GenericStack(slot.getConfig().what(), amount));
            }
        });
        syncManager.registerServerSyncedAction("ae_config_set_ghost", packet -> {});
    }

    @Override
    public final InteractionResult onDataStickShiftUse(Player player, ItemStack dataStick) {
        if (!isRemote()) {
            var tag = new CompoundTag();
            tag.put("MEGasInputHatch", writeConfigToTag());
            dataStick.setTag(tag);
            player.sendSystemMessage(Component.translatable("gtceu.machine.me.import_copy_settings"));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public final InteractionResult onDataStickUse(Player player, ItemStack dataStick) {
        var tag = dataStick.getTag();

        if (tag == null || !tag.contains("MEGasInputHatch")) {
            return InteractionResult.PASS;
        }

        if (!isRemote()) {
            readConfigFromTag(tag.getCompound("MEGasInputHatch"));
            updateTankSubscription();
            player.sendSystemMessage(Component.translatable("gtceu.machine.me.import_paste_settings"));
        }

        return InteractionResult.sidedSuccess(isRemote());
    }

    protected CompoundTag writeConfigToTag() {
        var tag = new CompoundTag();
        var configStacks = new CompoundTag();
        tag.put("ConfigStacks", configStacks);

        for (int i = 0; i < CONFIG_SIZE; i++) {
            var config = gasHandler.getInventory()[i].getConfig();
            if (GasGuiHelper.isGas(config)) {
                configStacks.put(Integer.toString(i), GenericStack.writeTag(config));
            }
        }

        tag.putByte("GhostCircuit", (byte) circuitSlot.getCurrentCircuit());

        return tag;
    }

    protected void readConfigFromTag(CompoundTag tag) {
        if (tag.contains("ConfigStacks")) {
            var configStacks = tag.getCompound("ConfigStacks");

            for (int i = 0; i < CONFIG_SIZE; i++) {
                String key = Integer.toString(i);

                if (configStacks.contains(key)) {
                    var config = GenericStack.readTag(configStacks.getCompound(key));
                    gasHandler.getInventory()[i].setConfig(GasGuiHelper.isGas(config) ? config : null);
                } else {
                    gasHandler.getInventory()[i].setConfig(null);
                }

                gasHandler.getInventory()[i].setStock(null);
            }
        }

        if (tag.contains("GhostCircuit")) {
            circuitSlot.setCurrentCircuit(tag.getByte("GhostCircuit"));
        }
    }
}
