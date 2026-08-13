package org.leodreamer.sftcore.integration.ponder.scene;

import com.gregtechceu.gtceu.GTCEu;
import net.minecraft.core.Direction;
import org.leodreamer.sftcore.integration.ponder.api.SFTSceneBuilder;
import org.leodreamer.sftcore.integration.ponder.api.annotation.PonderScene;
import org.leodreamer.sftcore.integration.ponder.api.annotation.PonderSceneScanned;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.mui.MachineUIPanelBuilder;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.gui.GTRecipeTypeMachineWidget;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import brachy.modularui.api.RecipeViewerSettings;
import brachy.modularui.screen.ModularPanel;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.sync.ModularSyncManager;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.value.sync.DoubleSyncValue;
import brachy.modularui.value.sync.ItemSlotSyncHandler;
import brachy.modularui.value.ObjectValue;
import brachy.modularui.widgets.ItemDisplayWidget;
import brachy.modularui.widgets.slot.ItemSlot;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Objects;
import java.util.function.Supplier;

import static org.leodreamer.sftcore.integration.ponder.misc.SFTPonderGroup.LATHE;

@PonderSceneScanned
public class GTMachineScenes {

    private static final int RECIPE_DEMO_TICKS = 60;
    private static final int UI_WIDTH = 177;
    private static final int UI_HEIGHT = 85;

    @PonderScene(groups = LATHE, file = "ponder_alloy_electric_furnace")
    public static void lvLatheUI(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("lv_lathe_ui", "A Running Lathe UI");

        var pos = util.grid().at(2, 1, 2);
        var selection = util.select().position(pos);
        var machine = new SimpleTieredMachine[1];
        var recipeDuration = new int[1];
        var progressValue = new DoubleSyncValue[1];

        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.world().setBlock(pos, GTMachines.LATHE[GTValues.LV].getBlock().defaultBlockState());

        // The lathe enters the scene three seconds after opening Ponder.
        scene.idle(60);
        scene.world().showSection(selection, Direction.DOWN);
        scene.idle(20);

        scene.overlay()
            .showControls(util.vector().centerOf(pos), Pointing.DOWN, 20)
            .withItem(new ItemStack(Items.IRON_INGOT));

        scene.addInstruction(ponderScene -> {
            var lathe = (SimpleTieredMachine) Objects.requireNonNull(
                MetaMachine.getMachine(ponderScene.getWorld(), pos),
                "LV lathe was not created in the Ponder world"
            );
            machine[0] = lathe;
            lathe.importItems.setStackInSlot(0, new ItemStack(Items.IRON_INGOT));

            var recipe = ponderScene.getWorld()
                .getRecipeManager()
                .byKey(GTCEu.id("lathe/lathe_iron_to_rod"))
                .filter(GTRecipe.class::isInstance)
                .map(GTRecipe.class::cast)
                .orElseThrow(() -> new IllegalStateException("Iron rod lathe recipe was not found"));
            lathe.getRecipeLogic().setupRecipe(recipe);
            recipeDuration[0] = lathe.getRecipeLogic().getDuration();
        });

        scene.mui(
            util.vector().of(2.5, 2.5, 2.5),
            Pointing.LEFT,
            RECIPE_DEMO_TICKS + 20,
            UI_WIDTH,
            UI_HEIGHT,
            () -> createMachinePanel(Objects.requireNonNull(machine[0]), progressValue)
        );

        for (int tick = 1; tick <= RECIPE_DEMO_TICKS; tick++) {
            int currentTick = tick;
            scene.addInstruction(ponderScene -> machine[0].getRecipeLogic().setProgress(
                recipeDuration[0] * currentTick / RECIPE_DEMO_TICKS
            ));
            scene.addInstruction(ponderScene -> {
                if (progressValue[0] != null) {
                    progressValue[0].updateCacheFromSource(false);
                }
            });
            scene.idle(1);
        }

        scene.addInstruction(ponderScene -> machine[0].exportItems.setStackInSlot(
            0,
            ChemicalHelper.get(TagPrefix.rod, GTMaterials.Iron, 2)
        ));
        scene.idle(20);
    }

    private static ModularPanel<?> createMachinePanel(
        SimpleTieredMachine machine,
        DoubleSyncValue[] progressValue
    ) {
        var syncManager = new ModularSyncManager(true);
        var panelSyncManager = new PanelSyncManager(syncManager, true);
        var settings = new UISettings(RecipeViewerSettings.DUMMY);
        var panel = MachineUIPanelBuilder.panelBuilder(machine)
            .drawGTLogo(true)
            .attachInventory(false)
            .mainContents(contents -> contents.child(new GTRecipeTypeMachineWidget(
                machine.getRecipeType(),
                panelSyncManager,
                machine,
                machine.getRecipeLogic()::getProgressPercent
            )))
            .build(panelSyncManager, settings);
        panel.getLeftConfiguratorPanel().bottom(0);
        panel.getRightConfiguratorPanel().bottom(0);
        progressValue[0] = panelSyncManager.getOrCreateSyncHandler(
            "progressPercent",
            DoubleSyncValue.class,
            () -> new DoubleSyncValue(machine.getRecipeLogic()::getProgressPercent)
        );
        makeItemSlotsRenderOnly(panel, panelSyncManager);
        return panel;
    }

    private static void makeItemSlotsRenderOnly(
        ModularPanel<?> panel,
        PanelSyncManager syncManager
    ) {
        panel.visitTransformAllChildren(widget -> {
            if (!(widget instanceof ItemSlot slot)) {
                return widget;
            }

            // Embed screens intentionally do not initialize synced widgets and are not container screens.
            // Resolve keyed slots directly from the already-built panel sync manager; calling the normal
            // initializer here would require the widget to have been attached to a live screen first.
            Supplier<ItemStack> stackSupplier;
            if (slot.getSyncKey() == null) {
                var modularSlot = slot.getSlot();
                stackSupplier = modularSlot::getItem;
            } else {
                var slotHandler = (ItemSlotSyncHandler) syncManager.getSyncHandlerFromMapKey(slot.getSyncKey());
                stackSupplier = slotHandler == null ? () -> ItemStack.EMPTY : slotHandler.getSlot()::getItem;
            }
            var display = new ItemDisplayWidget()
                .item(new ObjectValue.Dynamic<>(ItemStack.class, stackSupplier, ignored -> {}))
                .displayAmount(true);

            display.resizer().copyPropertiesOf(slot.resizer());
            if (slot.getName() != null) {
                display.name(slot.getName());
            }
            if (slot.getBackground() != null) {
                display.backgroundOverlay(slot.getBackground());
            }
            if (slot.getOverlay() != null) {
                display.overlay(slot.getOverlay());
            }
            return display;
        });
    }
}
