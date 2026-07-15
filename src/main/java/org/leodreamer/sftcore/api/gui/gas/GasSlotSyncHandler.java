package org.leodreamer.sftcore.api.gui.gas;

import org.leodreamer.sftcore.integration.mek.SFTMekanismCapabilities;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import brachy.modularui.utils.MouseData;
import brachy.modularui.value.sync.ValueSyncHandler;
import com.mojang.blaze3d.platform.InputConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Accessors(fluent = true, chain = true)
public class GasSlotSyncHandler extends ValueSyncHandler<GasStack, GasSlotSyncHandler> {

    public static final int SYNC_CLICK = 1;

    private @NotNull GasStack cache = GasStack.EMPTY;
    @Getter
    private final IGasHandler gasTank;
    @Getter
    private long lastCapacity;
    private final int tank;
    @Getter
    @Setter
    private boolean canFillSlot = true;
    @Getter
    @Setter
    private boolean canDrainSlot = true;

    public GasSlotSyncHandler(IGasHandler gasTank, int tank) {
        this.gasTank = gasTank;
        this.tank = tank;
        allowC2S();
    }

    @Override
    public @Nullable GasStack getValue() {
        return cache;
    }

    @Override
    public void setValue(@NotNull GasStack value, boolean setSource, boolean sync) {
        this.cache = value.isEmpty() ? GasStack.EMPTY : value.copy();
        if (setSource) {
            gasTank.setChemicalInTank(tank, this.cache.copy());
        }
        onValueChanged();
        if (sync) {
            sync();
        }
    }

    @Override
    public boolean updateCacheFromSource(boolean isFirstSync) {
        var current = gasTank.getChemicalInTank(tank);
        if (isFirstSync || !sameGasStack(current, cache)) {
            setValue(current, false, false);
            return true;
        }
        return false;
    }

    @Override
    public Class<GasStack> getValueType() {
        return GasStack.class;
    }

    @Override
    public void notifyUpdate() {
        setValue(gasTank.getChemicalInTank(tank), false, true);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeNbt(cache.write(new net.minecraft.nbt.CompoundTag()));
        buffer.writeVarLong(getCapacity());
    }

    public long getCapacity() {
        return lastCapacity > 0 ? lastCapacity : gasTank.getTankCapacity(tank);
    }

    @Override
    public void read(FriendlyByteBuf buffer) {
        var tag = buffer.readNbt();
        this.cache = tag == null ? GasStack.EMPTY : GasStack.readFromNBT(tag);
        this.lastCapacity = buffer.readVarLong();
        onValueChanged();
    }

    @Override
    public void readOnServer(int id, FriendlyByteBuf buf) {
        if (id == SYNC_VALUE) {
            read(buf);
        } else if (id == SYNC_CLICK) {
            tryClickContainer(MouseData.readPacket(buf));
        }
    }

    private void tryClickContainer(MouseData mouseData) {
        Player player = getSyncManager().getPlayer();
        ItemStack carried = player.containerMenu.getCarried();
        if (carried.isEmpty()) {
            return;
        }

        var itemHandler = carried.getCapability(SFTMekanismCapabilities.GAS_HANDLER)
            .resolve()
            .orElse(null);
        if (itemHandler == null) {
            return;
        }

        int maxAttempts = mouseData.shift() ? Math.max(1, carried.getCount()) : 1;
        boolean changed = false;

        for (int i = 0; i < maxAttempts; i++) {
            boolean transferred = false;
            if (mouseData.mouseButton() == InputConstants.MOUSE_BUTTON_LEFT && canDrainSlot) {
                transferred = fillHeldGasContainer(itemHandler);
            }
            if (!transferred && canFillSlot) {
                transferred = drainHeldGasContainer(itemHandler);
            }
            if (!transferred) {
                break;
            }
            changed = true;
        }

        if (changed) {
            player.containerMenu.setCarried(carried);
            notifyUpdate();
        }
    }

    private boolean fillHeldGasContainer(IGasHandler itemHandler) {
        var stored = gasTank.getChemicalInTank(tank);
        if (stored.isEmpty()) {
            return false;
        }

        var simulatedRemainder = itemHandler.insertChemical(stored.copy(), Action.SIMULATE);
        long accepted = stored.getAmount() - simulatedRemainder.getAmount();
        if (accepted <= 0) {
            return false;
        }

        var extracted = extractFromTank(accepted);
        if (extracted.isEmpty()) {
            return false;
        }

        var remainder = itemHandler.insertChemical(extracted.copy(), Action.EXECUTE);
        long inserted = extracted.getAmount() - remainder.getAmount();
        if (!remainder.isEmpty()) {
            insertIntoTank(remainder, Action.EXECUTE);
        }
        return inserted > 0;
    }

    private boolean drainHeldGasContainer(IGasHandler itemHandler) {
        for (int itemTank = 0; itemTank < itemHandler.getTanks(); itemTank++) {
            var available = itemHandler.getChemicalInTank(itemTank);
            if (available.isEmpty()) {
                continue;
            }

            var remainder = insertIntoTank(available.copy(), Action.SIMULATE);
            long accepted = available.getAmount() - remainder.getAmount();
            if (accepted <= 0) {
                continue;
            }

            var drained = itemHandler.extractChemical(itemTank, accepted, Action.EXECUTE);
            if (drained.isEmpty()) {
                continue;
            }
            insertIntoTank(drained, Action.EXECUTE);
            return true;
        }
        return false;
    }

    private GasStack extractFromTank(long amount) {
        var stored = gasTank.getChemicalInTank(tank);
        if (stored.isEmpty()) {
            return GasStack.EMPTY;
        }

        long extractedAmount = Math.min(amount, stored.getAmount());
        var extracted = new GasStack(stored, extractedAmount);
        var remain = stored.copy();
        remain.shrink(extractedAmount);
        gasTank.setChemicalInTank(tank, remain.isEmpty() ? GasStack.EMPTY : remain);
        return extracted;
    }

    private GasStack insertIntoTank(GasStack stack, Action action) {
        if (stack.isEmpty()) {
            return GasStack.EMPTY;
        }

        var stored = gasTank.getChemicalInTank(tank);
        if (!stored.isEmpty() && !stored.isTypeEqual(stack)) {
            return stack;
        }

        long capacity = gasTank.getTankCapacity(tank);
        long current = stored.isEmpty() ? 0 : stored.getAmount();
        long accepted = Math.min(stack.getAmount(), Math.max(0, capacity - current));
        if (accepted <= 0) {
            return stack;
        }

        if (action.execute()) {
            var inserted = stored.isEmpty() ? new GasStack(stack, accepted) : stored.copy();
            if (!stored.isEmpty()) {
                inserted.grow(accepted);
            }
            gasTank.setChemicalInTank(tank, inserted);
        }

        var remainder = stack.copy();
        remainder.shrink(accepted);
        return remainder.isEmpty() ? GasStack.EMPTY : remainder;
    }

    private static boolean sameGasStack(@Nullable GasStack a, @Nullable GasStack b) {
        if (a == null || a.isEmpty()) {
            return b == null || b.isEmpty();
        }
        if (b == null || b.isEmpty()) {
            return false;
        }
        return a.isTypeEqual(b) && a.getAmount() == b.getAmount();
    }
}
