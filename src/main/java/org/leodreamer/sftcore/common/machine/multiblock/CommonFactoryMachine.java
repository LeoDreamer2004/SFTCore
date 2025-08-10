package org.leodreamer.sftcore.common.machine.multiblock;

import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.CombinedDirectionalFancyConfigurator;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.lowdragmc.lowdraglib.syncdata.ISubscription;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.common.api.feature.IMachineAdjustment;

import java.util.List;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;

public class CommonFactoryMachine extends CoilWorkableElectricMultiblockMachine {

    public static final GTRecipeType[] AVAILABLE_RECIPES = {
            DUMMY_RECIPES,
            ARC_FURNACE_RECIPES,
            ASSEMBLER_RECIPES,
            AUTOCLAVE_RECIPES,
            BREWING_RECIPES,
            FERMENTING_RECIPES,
            FLUID_HEATER_RECIPES,
            CENTRIFUGE_RECIPES,
            THERMAL_CENTRIFUGE_RECIPES,
            CHEMICAL_BATH_RECIPES,
            CIRCUIT_ASSEMBLER_RECIPES,
            CUTTER_RECIPES,
            LATHE_RECIPES,
            DISTILLERY_RECIPES,
            ELECTROLYZER_RECIPES,
            ELECTROMAGNETIC_SEPARATOR_RECIPES,
            POLARIZER_RECIPES,
            LASER_ENGRAVER_RECIPES,
            EXTRACTOR_RECIPES,
            EXTRUDER_RECIPES,
            CANNER_RECIPES,
            MACERATOR_RECIPES,
            BENDER_RECIPES,
            COMPRESSOR_RECIPES,
            FORGE_HAMMER_RECIPES,
            FORMING_PRESS_RECIPES,
            MIXER_RECIPES,
            PACKER_RECIPES,
            SIFTER_RECIPES,
            FLUID_SOLIDFICATION_RECIPES,
            WIREMILL_RECIPES,
    };

    @Nullable
    private ISubscription machineSub = null;

    private GTRecipeType recipeType = DUMMY_RECIPES;

    @Getter
    private boolean voltageValid = false;

    public CommonFactoryMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @NotNull
    private IMachineAdjustment getMachineHolder() {
        for (IMultiPart part : getParts()) {
            if (part instanceof IMachineAdjustment holderPart) {
                return holderPart;
            }
        }
        throw new IllegalStateException("No machine adjustment hatch found in the common factory.");
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        var machineAdjustment = getMachineHolder();
        this.onMachineAdjustmentChanged(machineAdjustment);
        this.machineSub = machineAdjustment.addListenerOnChanged(this::onMachineAdjustmentChanged);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (this.machineSub != null) {
            this.machineSub.unsubscribe();
            this.machineSub = null;
        }
    }

    private void checkVoltageValid() {
        long maxVoltage = energyContainer.getInputVoltage();
        int voltageTier = GTUtil.getFloorTierByVoltage(maxVoltage);
        voltageValid = voltageTier == tier;
    }

    private void onMachineAdjustmentChanged(IMachineAdjustment holder) {
        this.recipeType = holder.getRecipeType();
        this.tier = holder.getTier();
        checkVoltageValid();
    }

    @Override
    @NotNull
    public GTRecipeType getRecipeType() {
        return recipeType;
    }

    public int getMaxParallels() {
        return 4 * (getCoilTier() + 1);
    }

    ///  GUI  ///

    @Override
    public void attachSideTabs(TabsWidget sideTabs) {
        // Hide the recipe configurator tab
        sideTabs.setMainTab(this);

        var directionalConfigurator = CombinedDirectionalFancyConfigurator.of(self(), self());
        if (directionalConfigurator != null)
            sideTabs.attachSubTab(directionalConfigurator);
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) {
            return;
        }

        if (!recipeLogic.isActive()) {
            var component = textList.remove(textList.size() - 1); // idle
            MultiblockDisplayText.builder(textList, isFormed())
                    .addParallelsLine(getMaxParallels());
            textList.add(component);
        }
        if (!isVoltageValid() && recipeType != DUMMY_RECIPES) {
            textList.add(Component.translatable("sftcore.machine.common_factory.voltage_invalid")
                    .withStyle(ChatFormatting.RED));
        }
        getDefinition().getAdditionalDisplay().accept(this, textList);
    }
}
