package org.leodreamer.sftcore.common.item.cepattern;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.data.SFTItems;
import org.leodreamer.sftcore.integration.emi.gui.CEPatternEmiViewer;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.core.definitions.AEItems;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.gui.widget.custom.PlayerInventoryWidget;

import java.util.Objects;
import java.util.function.Consumer;

@DataGenScanned
public class CEPatternUIProvider {

    public static final String EDITOR_WIDGET_ID = "create_encapsulation_pattern_editor";

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

    private final Level level;
    private final ItemStack stack;
    private final Consumer<ItemStack> onSave;
    private final int lockedPlayerSlot;
    private final CEPatternData data;
    private final CustomItemStackHandler inputInventory = new CustomItemStackHandler(1);
    private final CustomItemStackHandler outputInventory = new CustomItemStackHandler(1);

    private CEPatternEditorWidget editorWidget;
    private WidgetGroup stepList;
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

    public Widget createWidget() {
        var root = new WidgetGroup(0, 0, 230, 238);
        root.setBackground(GuiTextures.BACKGROUND);

        var editor = new CEPatternEditorWidget(this, 5, 5, 220, 168);
        this.editorWidget = editor;

        // recipe id input
        editor.addWidget(new LabelWidget(4, 7, RECIPE_ID));
        var recipeIdField = new TextFieldWidget(56, 3, 128, 16, () -> recipeIdText, text -> recipeIdText = text)
            .setResourceLocationOnly()
            .setMaxStringLength(128);
        recipeIdField.setClientSideWidget();
        editor.addWidget(recipeIdField);

        // add recipe
        editor.addWidget(
            createButton(188, 2, 28, 18, ADD, this::addRecipeFromText)
                .setClientSideWidget()
        );

        // recipe steps
        stepList = new DraggableScrollableWidgetGroup(4, 24, 212, 94);
        stepList.setBackground(GuiTextures.DISPLAY);
        stepList.setClientSideWidget();
        editor.addWidget(stepList);
        rebuildStepList();

        // autogen for AE2 patterns
        editor.addWidget(new LabelWidget(4, 128, GENERATE));
        editor.addWidget(
            new SlotWidget(inputInventory, 0, 72, 124, true, true)
                .setBackground(GuiTextures.SLOT, GuiTextures.IN_SLOT_OVERLAY)
        );
        editor.addWidget(
            new Widget(96, 128, 18, 10).setBackground(GuiTextures.PROGRESS_BAR_ARROW.getSubTexture(0, 0, 1, 0.5))
        );
        editor.addWidget(
            new SlotWidget(outputInventory, 0, 120, 124, true, false)
                .setBackground(GuiTextures.SLOT, GuiTextures.OUT_SLOT_OVERLAY)
        );
        editor.addWidget(createButton(148, 123, 52, 20, GENERATE_BUTTON, cd -> generatePattern()));

        root.addWidget(editor);

        // player inventory
        var playerInventory = new LockedPlayerInventoryWidget(lockedPlayerSlot);
        playerInventory.setSelfPosition(29, 150);
        playerInventory.setSlotBackground(GuiTextures.SLOT);
        root.addWidget(playerInventory);

        return root;
    }

    public void onClose(Player player) {
        if (player.level().isClientSide) {
            return;
        }
        saveHeldStack();
        giveToPlayer(player, inputInventory.getStackInSlot(0));
        giveToPlayer(player, outputInventory.getStackInSlot(0));
        inputInventory.setStackInSlot(0, ItemStack.EMPTY);
        outputInventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    private void addRecipeFromText(ClickData clickData) {
        var id = ResourceLocation.tryParse(recipeIdText);
        if (id == null) {
            return;
        }
        editorWidget.requestAddRecipe(id);
    }

    public void addRecipe(ResourceLocation id) {
        if (!CEPatternLogic.canEncode(level, id)) {
            return;
        }
        if (!data.addRecipe(id)) {
            return;
        }
        rebuildStepList();
        saveHeldStack();
    }

    public void removeRecipe(int index) {
        if (!data.removeRecipe(index)) {
            return;
        }
        rebuildStepList();
        saveHeldStack();
    }

    public void setMultiplier(int index, String text) {
        int multiplier;
        try {
            multiplier = Integer.parseInt(text);
        } catch (NumberFormatException ignored) {
            multiplier = 1;
        }
        if (!data.setMultiplier(index, multiplier)) {
            return;
        }
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

    private void rebuildStepList() {
        if (stepList == null) {
            return;
        }
        stepList.clearAllWidgets();
        var recipeIds = data.recipeIds();
        var multipliers = data.multipliers();
        if (recipeIds.isEmpty()) {
            var emptyLabel = new LabelWidget(6, 8, EMPTY);
            emptyLabel.setColor(Objects.requireNonNull(ChatFormatting.GRAY.getColor()));
            stepList.addWidget(emptyLabel);
            return;
        }

        for (int i = 0; i < recipeIds.size(); i++) {
            final int index = i;
            var id = recipeIds.get(i);
            var recipe = CEPatternLogic.getCERecipe(level, id).orElse(null);
            int y = i * 24 + 2;

            // mechanical machine
            var icon = recipe == null ? SFTItems.CREATE_ENCAPSULATION_PATTERN.asStack() :
                recipe.machineIcon().copy();
            var iconSlot = new SlotWidget(new CustomItemStackHandler(icon), 0, 2, y, false, false);
            iconSlot.setBackground(GuiTextures.SLOT);
            stepList.addWidget(iconSlot);

            // recipe type
            var typeName = recipe == null ? Component.literal(id.toString()) :
                recipe.typeName();
            stepList.addWidget(new LabelWidget(24, y + 5, typeName.getString()));

            // multiplier
            var multiplierField = new TextFieldWidget(
                116, y + 3, 28, 16,
                () -> String.valueOf(multipliers.get(index)),
                text -> editorWidget.requestSetMultiplier(index, text)
            )
                .setNumbersOnly(1, 999)
                .setMaxStringLength(3);
            multiplierField.setClientSideWidget();
            stepList.addWidget(multiplierField);

            // recipe visualization
            String description = recipe == null ? id.toString() :
                CEPatternTooltips.describeStep(recipe);
            var viewButton = createButton(
                150, y + 1, 38, 18, VIEW,
                cd -> CEPatternEmiViewer.openRecipe(id)
            ).setClientSideWidget();
            viewButton.setHoverTooltips(Component.literal(description).withStyle(ChatFormatting.GRAY));
            stepList.addWidget(viewButton);

            // remove recipe
            stepList.addWidget(
                createButton(
                    190, y + 1, 18, 18, "x",
                    cd -> editorWidget.requestRemoveRecipe(index)
                ).setClientSideWidget()
            );
        }
    }

    private void saveHeldStack() {
        if (!level.isClientSide) {
            var dataTag = data.write();
            stack.setTag(dataTag);
            onSave.accept(stack);
        }
    }

    private ButtonWidget createButton(
        int x,
        int y,
        int width,
        int height,
        String labelKey,
        Consumer<ClickData> action
    ) {
        return new ButtonWidget(
            x,
            y,
            width,
            height,
            new GuiTextureGroup(
                ResourceBorderTexture.BUTTON_COMMON.copy(),
                new TextTexture(Component.translatable(labelKey).getString())
            ),
            action
        );
    }

    private static void giveToPlayer(Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (!player.addItem(stack.copy())) {
            player.drop(stack.copy(), false);
        }
    }

    private static final class LockedPlayerInventoryWidget extends PlayerInventoryWidget {

        private final int lockedSlot;

        private LockedPlayerInventoryWidget(int lockedSlot) {
            this.lockedSlot = lockedSlot;
        }

        @Override
        public void initWidget() {
            super.initWidget();
            if (lockedSlot < 0) {
                return;
            }
            var widget = getFirstWidgetById("player_inv_" + lockedSlot);
            if (widget instanceof SlotWidget slotWidget) {
                slotWidget.setCanTakeItems(false);
                slotWidget.setCanPutItems(false);
                slotWidget.setDrawHoverOverlay(false);
            }
        }
    }
}
