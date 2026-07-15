package org.leodreamer.sftcore.common.machine.multiblock;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.util.RLUtils;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.notifiable.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.api.widget.IWidget;
import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.sync.BooleanSyncValue;
import brachy.modularui.value.sync.IntSyncValue;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widget.ParentWidget;
import brachy.modularui.widgets.slot.ItemSlot;
import brachy.modularui.widgets.slot.ModularSlot;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;

@DataGenScanned
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
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

    @SaveField
    @Getter
    private final NotifiableItemStackHandler machineInventory;

    @Getter
    private GTRecipeType recipeType = DUMMY_RECIPES;

    @Getter
    private boolean voltageValid = false;

    public CommonFactoryMachine(BlockEntityCreationInfo info) {
        super(info);
        this.machineInventory = attachTrait(
            new NotifiableItemStackHandler(1, IO.NONE, IO.NONE)
                .setFilter(this::isValidInnerMachine)
                .shouldSearchContent(false)
        );
        this.machineInventory.addChangedListener(this::onMachineChanged);
    }

    @Override
    public void formStructure(String substructureName) {
        super.formStructure(substructureName);
        this.onMachineChanged();
    }

    @Override
    public void invalidateStructure(String name) {
        super.invalidateStructure(name);
        clearMachineCache();
    }

    private void checkVoltageValid() {
        if (energyContainer == null) {
            voltageValid = false;
            return;
        }
        long maxVoltage = energyContainer.getInputVoltage();
        int voltageTier = GTUtil.getFloorTierByVoltage(maxVoltage);
        voltageValid = voltageTier == tier;
    }

    private boolean isValidInnerMachine(ItemStack stack) {
        return getInnerMachineDefinition(stack) != null;
    }

    @Nullable
    private static MachineDefinition getInnerMachineDefinition(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        var item = stack.getItem();
        var rl = RLUtils.getItemRL(item);
        var def = GTRegistries.MACHINES.get(rl);
        if (def == null || def instanceof MultiblockMachineDefinition || def.getRecipeTypes().length == 0) {
            return null;
        }
        return def;
    }

    private void onMachineChanged() {
        var def = getInnerMachineDefinition(machineInventory.getStackInSlot(0));
        if (def == null) {
            clearMachineCache();
            return;
        }

        var recipeTypes = def.getRecipeTypes();
        if (recipeTypes.length == 0) {
            clearMachineCache();
            return;
        }

        // small machine should only have one recipe type
        this.recipeType = recipeTypes[0];
        this.tier = def.getTier();
        checkVoltageValid();
        getRecipeLogic().markLastRecipeDirty();
        getRecipeLogic().updateTickSubscription();
    }

    private void clearMachineCache() {
        this.recipeType = DUMMY_RECIPES;
        this.tier = 0;
        this.voltageValid = false;
        getRecipeLogic().markLastRecipeDirty();
        getRecipeLogic().updateTickSubscription();
    }

    public int getMaxParallels() {
        return 4 * (getCoilTier() + 1);
    }

    @Override
    public void buildMainUI(
        ParentWidget<?> mainWidget, PosGuiData guiData, PanelSyncManager syncManager,
        UISettings settings
    ) {
        super.buildMainUI(mainWidget, guiData, syncManager, settings);
        mainWidget.child(
            new ItemSlot()
                .slot(new ModularSlot(machineInventory.storage, 0))
                .background(GTGuiTextures.SLOT, GTGuiTextures.IN_SLOT_OVERLAY)
                .right(4)
                .bottom(4)
        );
    }

    @RegisterLanguage("The voltage of energy hatch and machine don't match!")
    static final String VOLTAGE_INVALID = "sftcore.machine.common_factory.voltage_invalid";

    @Override
    public List<IWidget> getWidgetsForDisplay(PanelSyncManager syncManager) {
        var widgets = new ArrayList<>(super.getWidgetsForDisplay(syncManager));
        widgets.add(addMaxParallelLine(syncManager));
        widgets.add(addVoltageWarningLine(syncManager));
        return widgets;
    }

    private IWidget addMaxParallelLine(PanelSyncManager syncManager) {
        var isFormed = syncManager.getOrCreateSyncHandler(
            "commonFactoryFormed", BooleanSyncValue.class,
            () -> new BooleanSyncValue(this::isFormed)
        );
        var isActive = syncManager.getOrCreateSyncHandler(
            "commonFactoryActive", BooleanSyncValue.class,
            () -> new BooleanSyncValue(recipeLogic::isActive)
        );
        var maxParallels = syncManager.getOrCreateSyncHandler(
            "commonFactoryMaxParallels", IntSyncValue.class,
            () -> new IntSyncValue(this::getMaxParallels)
        );
        return Text.dynamic(
            () -> Component.translatable(
                "gtceu.multiblock.parallel",
                Component.literal(FormattingUtil.formatNumbers(maxParallels.getIntValue()))
                    .withStyle(ChatFormatting.DARK_PURPLE)
            ).withStyle(ChatFormatting.GRAY)
        )
            .asWidget()
            .setEnabledIf(
                widget -> isFormed.getBoolValue() && !isActive.getBoolValue() &&
                    maxParallels.getIntValue() > 1
            );
    }

    private IWidget addVoltageWarningLine(PanelSyncManager syncManager) {
        var isFormed = syncManager.getOrCreateSyncHandler(
            "commonFactoryFormed", BooleanSyncValue.class,
            () -> new BooleanSyncValue(this::isFormed)
        );
        var voltageValid = syncManager.getOrCreateSyncHandler(
            "commonFactoryVoltageValid", BooleanSyncValue.class,
            () -> new BooleanSyncValue(this::isVoltageValid)
        );
        var hasMachine = syncManager.getOrCreateSyncHandler(
            "commonFactoryHasMachine", BooleanSyncValue.class,
            () -> new BooleanSyncValue(() -> recipeType != DUMMY_RECIPES)
        );
        return Text.dynamic(() -> Component.translatable(VOLTAGE_INVALID).withStyle(ChatFormatting.RED))
            .asWidget()
            .setEnabledIf(
                widget -> isFormed.getBoolValue() && !voltageValid.getBoolValue() &&
                    hasMachine.getBoolValue()
            );
    }
}
