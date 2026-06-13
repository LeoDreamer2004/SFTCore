package org.leodreamer.sftcore.common.item.mechanical;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.data.SFTItems;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.custom.PlayerInventoryWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@DataGenScanned
public class MechanicalEncapsulationPatternUIProvider {

    public static final String EDITOR_WIDGET_ID = "sftcore_mechanical_encapsulation_pattern_editor";

    @RegisterLanguage("Recipe ID")
    public static final String RECIPE_ID = "item.sftcore.mechanical_encapsulation_pattern.recipe_id";

    @RegisterLanguage("Add")
    public static final String ADD = "item.sftcore.mechanical_encapsulation_pattern.add";

    @RegisterLanguage("No Create recipes added")
    public static final String EMPTY = "item.sftcore.mechanical_encapsulation_pattern.empty";

    @RegisterLanguage("Generate Pattern")
    public static final String GENERATE = "item.sftcore.mechanical_encapsulation_pattern.generate";

    @RegisterLanguage("Generate")
    public static final String GENERATE_BUTTON = "item.sftcore.mechanical_encapsulation_pattern.generate.button";

    @RegisterLanguage("View")
    public static final String VIEW = "item.sftcore.mechanical_encapsulation_pattern.view";

    private final Level level;
    private final Supplier<ItemStack> heldStackSupplier;
    private final Consumer<ItemStack> heldStackSaver;
    private final List<ResourceLocation> recipeIds = new ArrayList<>();
    private final List<Integer> multipliers = new ArrayList<>();
    private final CustomItemStackHandler inputInventory = new CustomItemStackHandler(1);
    private final CustomItemStackHandler outputInventory = new CustomItemStackHandler(1);

    private MechanicalPatternEditorWidget editorWidget;
    private WidgetGroup stepList;
    private String recipeIdText = "";

    public MechanicalEncapsulationPatternUIProvider(
        Level level,
        Supplier<ItemStack> heldStackSupplier,
        Consumer<ItemStack> heldStackSaver
    ) {
        this.level = level;
        this.heldStackSupplier = heldStackSupplier;
        this.heldStackSaver = heldStackSaver;
        this.recipeIds.addAll(MechanicalEncapsulationPatternLogic.readRecipeIds(heldStackSupplier.get()));
        this.multipliers.addAll(MechanicalEncapsulationPatternLogic.readRecipeMultipliers(heldStackSupplier.get()));
        normalizeMultiplierList();
        this.inputInventory.setFilter(MechanicalEncapsulationPatternLogic::isBlankAePattern);
    }

    public Widget createWidget() {
        var root = new WidgetGroup(0, 0, 230, 238);
        root.setBackground(GuiTextures.BACKGROUND);

        var editor = new MechanicalPatternEditorWidget(this, 5, 5, 220, 168);
        this.editorWidget = editor;

        editor.addWidget(new LabelWidget(4, 7, RECIPE_ID));
        var recipeIdField = new TextFieldWidget(56, 3, 128, 16, () -> recipeIdText, text -> recipeIdText = text)
            .setResourceLocationOnly()
            .setMaxStringLength(128);
        recipeIdField.setClientSideWidget();
        editor.addWidget(recipeIdField);
        editor.addWidget(createButton(188, 2, 28, 18, ADD, this::addRecipeFromText));

        stepList = new DraggableScrollableWidgetGroup(4, 24, 212, 94);
        stepList.setBackground(GuiTextures.DISPLAY);
        stepList.setClientSideWidget();
        editor.addWidget(stepList);
        rebuildStepList();

        editor.addWidget(new LabelWidget(4, 128, GENERATE));
        editor.addWidget(
            new SlotWidget(inputInventory, 0, 72, 124, true, true)
                .setBackground(GuiTextures.SLOT, GuiTextures.IN_SLOT_OVERLAY)
        );
        editor.addWidget(new Widget(96, 128, 18, 10).setBackground(GuiTextures.PROGRESS_BAR_ARROW.getSubTexture(0, 0, 1, 0.5)));
        editor.addWidget(
            new SlotWidget(outputInventory, 0, 120, 124, true, false)
                .setBackground(GuiTextures.SLOT, GuiTextures.OUT_SLOT_OVERLAY)
        );
        editor.addWidget(createButton(148, 123, 52, 20, GENERATE_BUTTON, cd -> requestGeneratePattern()));

        root.addWidget(editor);

        var playerInventory = new PlayerInventoryWidget();
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
        if (id != null) {
            requestAddRecipe(id);
        }
    }

    public void addRecipe(ResourceLocation id) {
        if (recipeIds.size() >= MechanicalEncapsulationPatternLogic.MAX_STEPS) {
            return;
        }
        if (!MechanicalEncapsulationPatternLogic.canEncode(level, id)) {
            return;
        }
        recipeIds.add(id);
        multipliers.add(1);
        rebuildStepList();
        saveHeldStackIfServer();
    }

    public void removeRecipe(int index) {
        if (index < 0 || index >= recipeIds.size()) {
            return;
        }
        recipeIds.remove(index);
        if (index < multipliers.size()) {
            multipliers.remove(index);
        }
        normalizeMultiplierList();
        rebuildStepList();
        saveHeldStackIfServer();
    }

    public void setMultiplier(int index, String text) {
        if (index < 0 || index >= multipliers.size()) {
            return;
        }
        try {
            multipliers.set(index, MechanicalEncapsulationPatternLogic.sanitizeMultiplier(Integer.parseInt(text)));
        } catch (NumberFormatException ignored) {
            multipliers.set(index, 1);
        }
        saveHeldStackIfServer();
    }

    public void generatePattern() {
        if (recipeIds.isEmpty() || !outputInventory.getStackInSlot(0).isEmpty()) {
            return;
        }

        var input = inputInventory.getStackInSlot(0);
        if (!MechanicalEncapsulationPatternLogic.isBlankAePattern(input)) {
            return;
        }

        var encoded = MechanicalEncapsulationPatternLogic.makeEncodedProcessingPattern(level, recipeIds, multipliers);
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
        if (recipeIds.isEmpty()) {
            var emptyLabel = new LabelWidget(6, 8, EMPTY);
            emptyLabel.setColor(Objects.requireNonNull(ChatFormatting.GRAY.getColor()));
            stepList.addWidget(emptyLabel);
            return;
        }

        for (int i = 0; i < recipeIds.size(); i++) {
            final int index = i;
            var id = recipeIds.get(i);
            var recipe = MechanicalEncapsulationPatternLogic.getMechanicalRecipe(level, id).orElse(null);
            int y = i * 24 + 2;

            var icon = recipe == null ? SFTItems.MECHANICAL_ENCAPSULATION_PATTERN.asStack() :
                MechanicalEncapsulationPatternLogic.getMachineIcon(recipe);
            var iconSlot = new SlotWidget(new CustomItemStackHandler(icon), 0, 2, y, false, false);
            iconSlot.setBackground(GuiTextures.SLOT);
            stepList.addWidget(iconSlot);

            var typeName = recipe == null ? Component.literal(id.toString()) :
                MechanicalEncapsulationPatternLogic.getRecipeTypeName(recipe);
            stepList.addWidget(new LabelWidget(24, y + 5, typeName.getString()));

            String description = recipe == null ? id.toString() :
                MechanicalEncapsulationPatternLogic.describeStep(recipe);
            var multiplierField = new TextFieldWidget(116, y + 3, 28, 16, () -> getMultiplierText(index),
                text -> requestSetMultiplier(index, text))
                .setNumbersOnly(MechanicalEncapsulationPatternLogic.MIN_MULTIPLIER,
                    MechanicalEncapsulationPatternLogic.MAX_MULTIPLIER)
                .setMaxStringLength(3);
            multiplierField.setClientSideWidget();
            stepList.addWidget(multiplierField);

            var viewButton = createButton(150, y + 1, 38, 18, VIEW, cd -> viewRecipe(id));
            viewButton.setHoverTooltips(Component.literal(description).withStyle(ChatFormatting.GRAY));
            stepList.addWidget(viewButton);
            stepList.addWidget(createButton(190, y + 1, 18, 18, "x", cd -> requestRemoveRecipe(index)));
        }
    }

    private void requestAddRecipe(ResourceLocation id) {
        if (editorWidget != null) {
            editorWidget.requestAddRecipe(id);
        } else {
            addRecipe(id);
        }
    }

    private void requestRemoveRecipe(int index) {
        if (editorWidget != null) {
            editorWidget.requestRemoveRecipe(index);
        } else {
            removeRecipe(index);
        }
    }

    private void requestSetMultiplier(int index, String text) {
        if (editorWidget != null) {
            editorWidget.requestSetMultiplier(index, text);
        } else {
            setMultiplier(index, text);
        }
    }

    private void requestGeneratePattern() {
        if (editorWidget != null) {
            editorWidget.requestGeneratePattern();
        } else {
            generatePattern();
        }
    }

    private String getMultiplierText(int index) {
        return Integer.toString(index >= 0 && index < multipliers.size() ? multipliers.get(index) : 1);
    }

    private void normalizeMultiplierList() {
        while (multipliers.size() < recipeIds.size()) {
            multipliers.add(1);
        }
        while (multipliers.size() > recipeIds.size()) {
            multipliers.remove(multipliers.size() - 1);
        }
        multipliers.replaceAll(MechanicalEncapsulationPatternLogic::sanitizeMultiplier);
    }

    private void saveHeldStack() {
        var stack = heldStackSupplier.get();
        if (!stack.is(SFTItems.MECHANICAL_ENCAPSULATION_PATTERN.asItem())) {
            return;
        }
        var saved = MechanicalEncapsulationPatternLogic.writeRecipeIds(stack, recipeIds, multipliers);
        saved.setCount(stack.getCount());
        heldStackSaver.accept(saved);
    }

    private void saveHeldStackIfServer() {
        if (!level.isClientSide) {
            saveHeldStack();
        }
    }

    private void viewRecipe(ResourceLocation id) {
        if (editorWidget != null && editorWidget.isRemote()) {
            editorWidget.openRecipe(id);
        }
    }

    private ButtonWidget createButton(int x, int y, int width, int height, String labelKey, Consumer<ClickData> action) {
        var button = new ButtonWidget(
            x,
            y,
            width,
            height,
            new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON.copy(), new TextTexture(Component.translatable(labelKey).getString())),
            action
        );
        button.setClientSideWidget();
        return button;
    }

    private static void giveToPlayer(Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (!player.addItem(stack.copy())) {
            player.drop(stack.copy(), false);
        }
    }
}
