package org.leodreamer.sftcore.common.machine;

import org.leodreamer.sftcore.api.feature.IWirelessAEMachine;
import org.leodreamer.sftcore.common.save.WirelessSavedData;
import org.leodreamer.sftcore.integration.ae2.logic.WirelessGrid;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.integration.ae2.machine.feature.IGridConnectedMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.trait.GridNodeHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IManagedGridNode;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

@Slf4j
public class GTWirelessControllerMachine extends MetaMachine implements IGridConnectedMachine {

    @Persisted
    protected final GridNodeHolder nodeHolder;

    @DescSynced
    @Getter
    @Setter
    protected boolean isOnline;

    @Nullable
    private WirelessGrid grid = null;

    private final Map<BlockPos, TickableSubscription> waitTasks = new Object2ObjectOpenHashMap<>();

    public GTWirelessControllerMachine(IMachineBlockEntity holder) {
        super(holder);
        nodeHolder = new GridNodeHolder(this);
    }

    @Override
    public IManagedGridNode getMainNode() {
        return nodeHolder.getMainNode();
    }

    public void join(IWirelessAEMachine other) {
        if (grid == null) return;
        if (grid.members().add(other.self().holder.pos())) {
            WirelessSavedData.INSTANCE.setDirty();
            connect(other);
        }
    }

    void connect(IWirelessAEMachine otherMachine) {
        var node = getGridNode();
        var otherNode = otherMachine.getGridNode();
        if (node == null || otherNode == null) return;
        try {
            GridHelper.createConnection(node, otherNode);
            otherMachine.sftcore$getWirelessHolder().setGrid(grid);
        } catch (IllegalStateException ignored) {}
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (grid == null) {
            var level = getLevel();
            if (level == null) return;
            grid = WirelessSavedData.INSTANCE.createOrGetGrid(level.dimension(), getPos());
        }
        for (var member : grid.members()) {
            if (!waitTasks.containsKey(member)) {
                waitTasks.put(member, subscribeServerTick(() -> waitForReady(member)));
            }
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (getLevel() == null || getLevel().isClientSide) return;
        waitTasks.values().forEach(TickableSubscription::unsubscribe);

        // GTM YOU ARE JOKING!
        // OK, it's my fault. Do not remove the grid data when the server is saving.
        if (
            getLevel() instanceof ServerLevel serverLevel &&
                serverLevel.getServer().isCurrentlySaving()
        ) return;

        waitTasks.clear();
        if (grid != null) {
            for (var member : grid.members()) {
                var machine = getWirelessMachineAt(member);
                if (machine != null) {
                    machine.sftcore$getWirelessHolder().setGrid(null);
                }
            }
            WirelessSavedData.INSTANCE.removeGrid(grid);
        }
    }

    void waitForReady(BlockPos member) {
        if (getOffsetTimer() % 20 == 0) {
            Objects.requireNonNull(grid);
            var machine = getWirelessMachineAt(member);
            if (machine == null) return;

            var otherMain = machine.getMainNode();
            if (otherMain.isReady()) {
                connect(machine);
                var subscription = waitTasks.get(member);
                if (subscription != null) {
                    subscription.unsubscribe();
                    waitTasks.remove(member);
                }
            }
        }
    }

    @Nullable
    private IWirelessAEMachine getWirelessMachineAt(BlockPos pos) {
        if (
            Objects.requireNonNull(getLevel()).getBlockEntity(pos) instanceof MetaMachineBlockEntity metaMachine &&
                metaMachine.metaMachine instanceof IWirelessAEMachine machine
        ) {
            return machine;
        }
        return null;
    }
}
