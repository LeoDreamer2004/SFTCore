package org.leodreamer.sftcore.common.item.cepattern;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.integration.emi.gui.CEPatternEmiViewer;

import com.gregtechceu.gtceu.api.mui.IItemUIHolder;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.core.definitions.AEItems;
import brachy.modularui.api.drawable.Text;
import brachy.modularui.api.widget.IWidget;
import brachy.modularui.factory.PlayerInventoryGuiData;
import brachy.modularui.screen.ModularPanel;
import brachy.modularui.screen.RichTooltip;
import brachy.modularui.screen.UISettings;
import brachy.modularui.utils.Alignment;
import brachy.modularui.utils.Color;
import brachy.modularui.value.StringValue;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widgets.ButtonWidget;
import brachy.modularui.widgets.ListWidget;
import brachy.modularui.widgets.TextWidget;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.slot.ItemSlot;
import brachy.modularui.widgets.slot.ModularSlot;
import brachy.modularui.widgets.textfield.TextFieldWidget;
import com.mojang.blaze3d.platform.InputConstants;

import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@DataGenScanned
public class CEPatternUIProvider implements IItemUIHolder {

    @RegisterLanguage("Recipe ID")
    public static final String RECIPE_ID = "item.sftcore.create_encapsulation_pattern.recipe_id";

    @RegisterLanguage("Add")
    public static final String ADD = "item.sftcore.create_encapsulation_pattern.add";

    @RegisterLanguage("No Create recipes added")
    public static final String EMPTY = "item.sftcore.create_encapsulation_pattern.empty";

    @RegisterLanguage("Generate Pattern")
    public static final String GENERATE = "item.sftcore.create_encapsulation_pattern.generate";

    @RegisterLanguage("Generate")
    public static final String GENERATE_BUTTON = "item.sftcore.create_encapsulation_pattern.generate.button";

    @RegisterLanguage("View")
    public static final String VIEW = "item.sftcore.create_encapsulation_pattern.view";

    public static final String PANEL_NAME = "create_encapsulation_pattern";
    public static final String ADD_RECIPE_ACTION = "ce_pattern_add_recipe";
    private static final String REMOVE_RECIPE_ACTION = "ce_pattern_remove_recipe";
    private static final String SET_MULTIPLIER_ACTION = "ce_pattern_set_multiplier";
    private static final String GENERATE_ACTION = "ce_pattern_generate";

    private final Level level;
    private final ItemStack stack;
    private final Consumer<ItemStack> onSave;
    private final int lockedPlayerSlot;
    private final CEPatternData data;
    private final CustomItemStackHandler inputInventory = new CustomItemStackHandler(1);
    private final CustomItemStackHandler outputInventory = new CustomItemStackHandler(1);
    private String recipeIdText = "";

    public CEPatternUIProvider(
        Level level,
        ItemStack stack,
        Consumer<ItemStack> onSave,
        int lockedPlayerSlot
    ) {
        this.level = level;
        this.stack = stack;
        this.onSave = onSave;
        this.lockedPlayerSlot = lockedPlayerSlot;
        this.data = CEPatternData.read(stack.getOrCreateTag());
        this.inputInventory.setFilter(AEItems.BLANK_PATTERN::isSameAs);
    }

    @Override
    public ModularPanel<?> buildUI(
        PlayerInventoryGuiData<?> guiData, PanelSyncManager syncManager, UISettings settings
    ) {
        registerActions(syncManager);
        syncManager.addCloseListener(this::onClose);

        return ModularPanel.defaultPanel(PANEL_NAME, 230, 258)
            .background(GTGuiTextures.BACKGROUND)
            .child(
                Flow.column()
                    .margin(5)
                    .childPadding(4)
                    .widthRel(1)
                    .heightRel(1)
                    .child(createRecipeIdRow(syncManager))
                    .child(createStepList(syncManager))
                    .child(createGenerateRow(syncManager))
                    .child(createPlayerSlotNotice())
            );
    }

    private void registerActions(PanelSyncManager syncManager) {
        syncManager.registerServerSyncedAction(ADD_RECIPE_ACTION, packet -> addRecipe(packet.readResourceLocation()));
        syncManager.registerServerSyncedAction(REMOVE_RECIPE_ACTION, packet -> removeRecipe(packet.readVarInt()));
        syncManager.registerServerSyncedAction(
            SET_MULTIPLIER_ACTION, packet -> setMultiplier(packet.readVarInt(), packet.readUtf())
        );
        syncManager.registerServerSyncedAction(GENERATE_ACTION, packet -> generatePattern());
    }

    private IWidget createRecipeIdRow(PanelSyncManager syncManager) {
        return Flow.row()
            .widthRel(1)
            .height(20)
            .child(Text.lang(RECIPE_ID).asWidget().width(52))
            .child(
                new TextFieldWidget()
                    .width(126)
                    .height(18)
                    .value(new StringValue.Dynamic(() -> recipeIdText, text -> recipeIdText = text))
                    .setPattern(ResourceLocationPatternHolder.PATTERN)
                    .setMaxLength(128)
            )
            .child(createButton(32, ADD, () -> {
                var id = ResourceLocation.tryParse(recipeIdText);
                if (id != null) {
                    syncManager.callSyncedAction(ADD_RECIPE_ACTION, buf -> buf.writeResourceLocation(id));
                }
            }));
    }

    private IWidget createStepList(PanelSyncManager syncManager) {
        var list = new ListWidget<>()
            .widthRel(1)
            .height(108)
            .crossAxisAlignment(Alignment.CrossAxis.START)
            .background(GTGuiTextures.DISPLAY);
        if (data.recipeIds().isEmpty()) {
            list.child(
                new TextWidget<>(Text.lang(EMPTY)).height(18)
                    .color(Objects.requireNonNull(ChatFormatting.GRAY.getColor())).margin(5)
            );
            return list;
        }

        for (int i = 0; i < data.recipeIds().size(); i++) {
            list.child(createStepRow(syncManager, i));
        }
        return list;
    }

    private IWidget createStepRow(PanelSyncManager syncManager, int index) {
        var id = data.recipeIds().get(index);
        var recipe = CERecipeStep.fromId(id, level).orElse(null);
        var icon = recipe == null ? SFTItems.CREATE_ENCAPSULATION_PATTERN.asStack() : recipe.machineIcon().copy();
        var iconHandler = new CustomItemStackHandler(icon);
        var description = recipe == null ? id.toString() : CEPatternTooltips.describeStep(recipe);
        var multiplier = new StringValue.Dynamic(
            () -> Integer.toString(data.multipliers().get(index)),
            text -> syncManager.callSyncedAction(SET_MULTIPLIER_ACTION, buf -> {
                buf.writeVarInt(index);
                buf.writeUtf(text);
            })
        );

        return Flow.row()
            .widthRel(1)
            .height(24)
            .margin(2)
            .child(
                new ItemSlot()
                    .slot(new ModularSlot(iconHandler, 0).accessibility(false, false))
                    .background(GTGuiTextures.SLOT)
            )
            .child(
                Text.of(recipe == null ? Component.literal(id.toString()) : recipe.typeName())
                    .asWidget()
                    .width(88)
                    .marginLeft(3)
            )
            .child(
                new TextFieldWidget()
                    .size(28, 16)
                    .value(multiplier)
                    .setNumbers(1, 999)
                    .setMaxLength(3)
                    .setTextAlignment(Alignment.Center)
                    .setTextColor(Color.WHITE.main)
            )
            .child(
                createButton(40, VIEW, () -> CEPatternEmiViewer.openRecipe(id))
                    .tooltip(new RichTooltip().addLine(Text.str(description)))
            )
            .child(
                createButton(
                    18, "x", () -> syncManager.callSyncedAction(REMOVE_RECIPE_ACTION, buf -> buf.writeVarInt(index))
                )
            );
    }

    private IWidget createGenerateRow(PanelSyncManager syncManager) {
        return Flow.row()
            .widthRel(1)
            .height(26)
            .child(Text.lang(GENERATE).asWidget().width(68))
            .child(
                new ItemSlot()
                    .slot(new ModularSlot(inputInventory, 0).accessibility(true, true))
                    .background(GTGuiTextures.SLOT, GTGuiTextures.IN_SLOT_OVERLAY)
            )
            .child(Text.str("->").asWidget().width(22).textAlign(Alignment.Center))
            .child(
                new ItemSlot()
                    .slot(new ModularSlot(outputInventory, 0) {

                        @Override
                        public boolean mayPlace(ItemStack stack) {
                            return false;
                        }
                    }.accessibility(false, true))
                    .background(GTGuiTextures.SLOT, GTGuiTextures.OUT_SLOT_OVERLAY)
            )
            .child(createButton(54, GENERATE_BUTTON, () -> syncManager.callSyncedAction(GENERATE_ACTION)));
    }

    private IWidget createPlayerSlotNotice() {
        return Text.str(lockedPlayerSlot >= 0 ? "" : " ").asWidget().height(1);
    }

    private ButtonWidget<?> createButton(int width, String labelKey, Runnable action) {
        return new ButtonWidget<>()
            .size(width, 18)
            .overlay(Text.lang(labelKey).style(ChatFormatting.WHITE).scale(0.55f))
            .onMousePressed((context, button) -> {
                if (button == InputConstants.MOUSE_BUTTON_LEFT) {
                    action.run();
                    return true;
                }
                return false;
            });
    }

    private void onClose(Player player) {
        if (player.level().isClientSide) {
            return;
        }
        saveHeldStack();
        giveToPlayer(player, inputInventory.getStackInSlot(0));
        giveToPlayer(player, outputInventory.getStackInSlot(0));
        inputInventory.setStackInSlot(0, ItemStack.EMPTY);
        outputInventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public void addRecipe(ResourceLocation id) {
        if (CERecipeStep.fromId(id, level).isEmpty()) {
            return;
        }
        if (data.addRecipe(id)) {
            saveHeldStack();
        }
    }

    public void removeRecipe(int index) {
        if (data.removeRecipe(index)) {
            saveHeldStack();
        }
    }

    public void setMultiplier(int index, String text) {
        if (index < 0 || index >= data.multipliers().size()) {
            return;
        }
        int multiplier;
        try {
            multiplier = Integer.parseInt(text);
        } catch (NumberFormatException ignored) {
            multiplier = 1;
        }
        data.multipliers().set(index, Math.max(1, Math.min(999, multiplier)));
        saveHeldStack();
    }

    public void generatePattern() {
        if (data.isEmpty() || !outputInventory.getStackInSlot(0).isEmpty()) {
            return;
        }

        var input = inputInventory.getStackInSlot(0);
        if (!AEItems.BLANK_PATTERN.isSameAs(input)) {
            return;
        }

        var encoded = data.compile(level).makeAEProcessingPattern();
        if (encoded.isEmpty()) {
            return;
        }
        if (!outputInventory.insertItem(0, encoded, true).isEmpty()) {
            return;
        }

        outputInventory.insertItem(0, encoded, false);
        input.shrink(1);
        inputInventory.setStackInSlot(0, input);
    }

    private void saveHeldStack() {
        if (!level.isClientSide) {
            stack.setTag(data.write());
            onSave.accept(stack);
        }
    }

    private static void giveToPlayer(Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (!player.addItem(stack.copy())) {
            player.drop(stack.copy(), false);
        }
    }

    private static final class ResourceLocationPatternHolder {

        private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("[a-z0-9_.:-]*");
    }
}
