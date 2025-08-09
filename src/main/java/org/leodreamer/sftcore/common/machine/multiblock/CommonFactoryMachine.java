package org.leodreamer.sftcore.common.machine.multiblock;

import com.gregtechceu.gtceu.api.gui.fancy.TabsWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.CombinedDirectionalFancyConfigurator;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.lowdragmc.lowdraglib.syncdata.ISubscription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.common.api.feature.IMachineAdjustment;

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

    public CommonFactoryMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public void attachSideTabs(TabsWidget sideTabs) {
        // Hide the recipe configurator tab
        sideTabs.setMainTab(this);

        var directionalConfigurator = CombinedDirectionalFancyConfigurator.of(self(), self());
        if (directionalConfigurator != null)
            sideTabs.attachSubTab(directionalConfigurator);
    }

    @NotNull
    private IMachineAdjustment getMachineHolder() {
        for (IMultiPart part : getParts()) {
            if (part instanceof IMachineAdjustment holderPart) {
                return holderPart;
            }
        }
        throw new IllegalStateException("No machine holder found in the common factory");
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        var machineHolder = getMachineHolder();
        this.machineSub = machineHolder.addListenerOnChanged((holder) -> {
            this.recipeType = holder.getRecipeType();
            this.tier = holder.getTier();
        });
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (this.machineSub != null) {
            this.machineSub.unsubscribe();
            this.machineSub = null;
        }
    }

    @Override
    @NotNull
    public GTRecipeType getRecipeType() {
        return recipeType;
    }
}
