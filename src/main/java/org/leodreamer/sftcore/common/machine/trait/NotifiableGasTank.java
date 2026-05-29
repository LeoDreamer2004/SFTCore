package org.leodreamer.sftcore.common.machine.trait;

import org.leodreamer.sftcore.api.recipe.capability.GasRecipeCapability;
import org.leodreamer.sftcore.integration.mek.NaiveGasTank;
import org.leodreamer.sftcore.integration.mek.SFTMekanismCapabilities;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.trait.ICapabilityTrait;
import com.gregtechceu.gtceu.api.machine.trait.MachineTraitType;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.api.sync_system.annotations.SyncToClient;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import lombok.Getter;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NotifiableGasTank extends NotifiableRecipeHandlerTrait<GasStack> implements ICapabilityTrait, IGasHandler {

    public static final MachineTraitType<NotifiableGasTank> TYPE = new MachineTraitType<>(NotifiableGasTank.class);

    @Getter
    public final IO handlerIO;

    @Getter
    public final IO capabilityIO;

    @SaveField
    @SyncToClient
    @Getter
    protected final NaiveGasTank[] storages;

    @Nullable
    private Boolean isEmpty;

    public NotifiableGasTank(int slots, long capacity, IO io) {
        this(slots, capacity, io, io);
    }

    public NotifiableGasTank(int slots, long capacity, IO handlerIO, IO capabilityIO) {
        this.handlerIO = handlerIO;
        this.capabilityIO = capabilityIO;
        this.storages = new NaiveGasTank[slots];

        for (int i = 0; i < storages.length; i++) {
            storages[i] = new NaiveGasTank(capacity);
            storages[i].setOnContentsChanged(this::onContentsChanged);
        }
    }

    @Override
    public MachineTraitType<NotifiableGasTank> getTraitType() {
        return TYPE;
    }

    public void onContentsChanged() {
        isEmpty = null;
        syncDataHolder.markClientSyncFieldDirty("storages");
        notifyListeners();
    }

    @Override
    public @Nullable List<GasStack> handleRecipeInner(IO io, GTRecipe recipe, List<GasStack> left, boolean simulate) {
        if (io != handlerIO) {
            return left;
        }
        if (io != IO.IN && io != IO.OUT) {
            return left.isEmpty() ? null : left;
        }

        var action = simulate ? Action.SIMULATE : Action.EXECUTE;

        for (var it = left.iterator(); it.hasNext();) {
            var gas = it.next();

            if (gas == null || gas.isEmpty()) {
                it.remove();
                continue;
            }

            if (io == IO.IN) {
                long need = gas.getAmount();

                for (int tank = 0; tank < getTanks() && need > 0; tank++) {
                    var stored = getChemicalInTank(tank);
                    if (stored.isEmpty() || !stored.isTypeEqual(gas)) {
                        continue;
                    }

                    var extracted = extractChemicalExact(tank, new GasStack(gas, need), action);
                    need -= extracted.getAmount();
                }

                if (need <= 0) {
                    it.remove();
                } else {
                    gas.setAmount(need);
                }
            } else {
                var remaining = gas.copy();

                for (int tank = 0; tank < getTanks() && !remaining.isEmpty(); tank++) {
                    remaining = insertChemical(tank, remaining, action);
                }

                if (remaining.isEmpty()) {
                    it.remove();
                } else {
                    gas.setAmount(remaining.getAmount());
                }
            }
        }

        return left.isEmpty() ? null : left;
    }

    @Override
    public RecipeCapability<GasStack> getCapability() {
        return GasRecipeCapability.CAP;
    }

    @Override
    public int getSize() {
        return getTanks();
    }

    @Override
    public List<Object> getContents() {
        var contents = new ArrayList<>();

        for (int i = 0; i < getTanks(); i++) {
            var stack = getChemicalInTank(i);
            if (!stack.isEmpty()) {
                contents.add(stack.copy());
            }
        }

        return contents;
    }

    @Override
    public double getTotalContentAmount() {
        long amount = 0;

        for (int i = 0; i < getTanks(); i++) {
            amount += getChemicalInTank(i).getAmount();
        }

        return amount;
    }

    public boolean isEmpty() {
        if (isEmpty == null) {
            isEmpty = true;
            for (NaiveGasTank tank : storages) {
                if (!tank.getStack().isEmpty()) {
                    isEmpty = false;
                    break;
                }
            }
        }
        return isEmpty;
    }

    public void exportToNearby(Direction... facings) {
        if (isEmpty()) {
            return;
        }

        var level = getMachine().getLevel();
        var pos = getMachine().getBlockPos();

        for (var facing : facings) {
            getAdjacentGasHandler(level, pos, facing)
                .ifPresent(adjacent -> transferGas(this, adjacent));
        }
    }

    public void importFromNearby(Direction... facings) {
        var level = getMachine().getLevel();
        var pos = getMachine().getBlockPos();

        for (var facing : facings) {
            getAdjacentGasHandler(level, pos, facing)
                .ifPresent(adjacent -> transferGas(adjacent, this));
        }
    }

    @Override
    public int getTanks() {
        return storages.length;
    }

    @Override
    public GasStack getChemicalInTank(int tank) {
        return storages[tank].getStack();
    }

    @Override
    public void setChemicalInTank(int tank, GasStack stack) {
        storages[tank].setStack(stack);
    }

    @Override
    public long getTankCapacity(int tank) {
        return storages[tank].getCapacity();
    }

    @Override
    public boolean isValid(int tank, GasStack stack) {
        return storages[tank].isValid(stack);
    }

    @Override
    public GasStack insertChemical(int tank, GasStack stack, Action action) {
        if (!canCapInput()) {
            return stack;
        }
        return storages[tank].insert(stack, action, AutomationType.EXTERNAL);
    }

    @Override
    public GasStack extractChemical(int tank, long amount, Action action) {
        if (!canCapOutput()) {
            return GasStack.EMPTY;
        }
        return storages[tank].extract(amount, action, AutomationType.EXTERNAL);
    }

    public GasStack insertChemicalManual(int tank, GasStack stack, Action action) {
        if (tank < 0 || tank >= storages.length || stack.isEmpty()) {
            return stack;
        }
        return storages[tank].insert(stack, action, AutomationType.MANUAL);
    }

    public GasStack extractChemicalManual(int tank, long amount, Action action) {
        if (tank < 0 || tank >= storages.length || amount <= 0) {
            return GasStack.EMPTY;
        }
        return storages[tank].extract(amount, action, AutomationType.MANUAL);
    }

    private GasStack extractChemicalExact(int tank, GasStack requested, Action action) {
        if (!canCapOutput() || requested.isEmpty()) {
            return GasStack.EMPTY;
        }

        var stored = getChemicalInTank(tank);
        if (stored.isEmpty() || !stored.isTypeEqual(requested)) {
            return GasStack.EMPTY;
        }

        return extractChemical(tank, requested.getAmount(), action);
    }

    private static Optional<IGasHandler> getAdjacentGasHandler(Level level, BlockPos pos, Direction facing) {
        var be = level.getBlockEntity(pos.relative(facing));
        if (be == null) {
            return Optional.empty();
        }

        return be.getCapability(SFTMekanismCapabilities.GAS_HANDLER, facing.getOpposite()).resolve();
    }

    public static boolean hasAdjacentGasHandler(Level level, BlockPos pos, Direction facing) {
        return getAdjacentGasHandler(level, pos, facing).isPresent();
    }

    private static void transferGas(IGasHandler from, IGasHandler to) {
        for (int tank = 0; tank < from.getTanks(); tank++) {
            var stored = from.getChemicalInTank(tank);
            if (stored.isEmpty()) {
                continue;
            }

            var simulatedExtract = from.extractChemical(tank, stored.getAmount(), Action.SIMULATE);

            if (simulatedExtract.isEmpty()) {
                continue;
            }

            var remainder = to.insertChemical(simulatedExtract.copy(), Action.SIMULATE);
            long accepted = simulatedExtract.getAmount() - remainder.getAmount();

            if (accepted <= 0) {
                continue;
            }
            var extracted = from.extractChemical(tank, accepted, Action.EXECUTE);

            if (!extracted.isEmpty()) {
                to.insertChemical(extracted, Action.EXECUTE);
            }
        }
    }
}
