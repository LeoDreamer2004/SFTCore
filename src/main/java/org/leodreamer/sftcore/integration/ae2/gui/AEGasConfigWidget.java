package org.leodreamer.sftcore.integration.ae2.gui;

import org.leodreamer.sftcore.api.gui.gas.GasGuiHelper;
import org.leodreamer.sftcore.common.machine.trait.gas.ExportOnlyAEGasList;

import com.gregtechceu.gtceu.common.mui.GTGuiTextures;
import com.gregtechceu.gtceu.integration.ae2.gui.AEConfigSyncHandler;
import com.gregtechceu.gtceu.integration.ae2.gui.AEGuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.api.widget.Interactable;
import brachy.modularui.screen.ModularPanel;
import brachy.modularui.screen.SecondaryPanel;
import brachy.modularui.screen.viewport.ModularGuiContext;
import brachy.modularui.theme.WidgetThemeEntry;
import brachy.modularui.value.StringValue;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widget.Widget;
import brachy.modularui.widgets.ButtonWidget;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.textfield.TextFieldWidget;
import com.mojang.blaze3d.platform.InputConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Copy version for gas from {@link com.gregtechceu.gtceu.integration.ae2.gui.AEConfigWidget}
 */
public class AEGasConfigWidget extends Widget<AEGasConfigWidget> implements Interactable {

    private static final int CELL_SIZE = 18;
    private static final int PAIR_HEIGHT = CELL_SIZE * 2 + 2;
    private static final int COLUMNS = 8;

    private final ExportOnlyAEGasList gasHandler;
    private final int slotCount;
    private @Nullable PanelSyncManager syncManager;
    private @Nullable AEConfigSyncHandler configSyncHandler;

    @OnlyIn(Dist.CLIENT)
    private int editingSlotIndex;
    @OnlyIn(Dist.CLIENT)
    private @Nullable String pendingAmount;
    @OnlyIn(Dist.CLIENT)
    private @Nullable SecondaryPanel amountEditorPanel;
    @OnlyIn(Dist.CLIENT)
    private @Nullable TextFieldWidget amountField;

    public AEGasConfigWidget(ExportOnlyAEGasList gasHandler, int slotCount) {
        this.gasHandler = gasHandler;
        this.slotCount = slotCount;
    }

    public AEGasConfigWidget syncManager(PanelSyncManager syncManager) {
        this.syncManager = syncManager;
        this.configSyncHandler = new AEConfigSyncHandler(gasHandler, slotCount);
        syncManager.syncValue("ae_config_display", configSyncHandler);
        return this;
    }

    @Override
    public void onInit() {
        super.onInit();
        editingSlotIndex = -1;
        pendingAmount = "0";
        amountEditorPanel = new SecondaryPanel(getPanel(), this::buildAmountEditor, true);
    }

    @OnlyIn(Dist.CLIENT)
    private ModularPanel<?> buildAmountEditor(ModularPanel<?> parent, Player player) {
        amountField = new TextFieldWidget() {

            @Override
            @NotNull
            public Interactable.Result onKeyPressed(int keyCode, int scanCode, int modifiers) {
                if (
                    isFocused() && (keyCode == InputConstants.KEY_RETURN ||
                        keyCode == InputConstants.KEY_NUMPADENTER)
                ) {
                    confirmAmountEdit();
                    return Interactable.Result.SUCCESS;
                }
                return super.onKeyPressed(keyCode, scanCode, modifiers);
            }
        };
        amountField.expanded().heightRel(1.0F)
            .value(new StringValue.Dynamic(() -> pendingAmount, val -> pendingAmount = val));

        return new ModularPanel<>("ae_gas_amount_editor")
            .size(120, 36)
            .posRel(0.5F, 0.35F)
            .background(GTGuiTextures.BACKGROUND)
            .child(ButtonWidget.panelCloseButton())
            .child(Text.str("Amount").asWidget().pos(4, 4))
            .child(
                Flow.row()
                    .left(4).right(4).bottom(4).height(18)
                    .child(amountField)
                    .child(
                        new ButtonWidget<>()
                            .size(18, 18)
                            .overlay(Text.str("+"))
                            .onMousePressed((context, button) -> {
                                if (button == 0) {
                                    confirmAmountEdit();
                                    return true;
                                }
                                return false;
                            })
                    )
            );
    }

    @OnlyIn(Dist.CLIENT)
    private void confirmAmountEdit() {
        if (editingSlotIndex < 0 || syncManager == null || amountEditorPanel == null) {
            return;
        }
        String text = amountField != null ? amountField.getText() : pendingAmount;
        if (text == null) {
            return;
        }
        long amount = AEGuiHelper.parseAmount(text);
        if (amount > 0) {
            String resolved = String.valueOf(amount);
            pendingAmount = resolved;
            if (amountField != null) {
                amountField.setText(resolved);
            }
            int slot = editingSlotIndex;
            syncManager.callSyncedAction("ae_config_amount", buf -> {
                buf.writeVarInt(slot);
                buf.writeVarLong(amount);
            });
        }
        editingSlotIndex = -1;
        amountEditorPanel.closePanel();
    }

    @OnlyIn(Dist.CLIENT)
    private void openAmountEditor(int slotIndex) {
        var config = configSyncHandler != null ? configSyncHandler.getClientConfig(slotIndex) : null;
        var gas = GasGuiHelper.getGasStack(config);
        if (gas.isEmpty() || amountEditorPanel == null) {
            return;
        }
        editingSlotIndex = slotIndex;
        pendingAmount = String.valueOf(gas.getAmount());
        if (amountEditorPanel.isPanelOpen()) {
            if (amountField != null) {
                amountField.setText(pendingAmount);
            }
            return;
        }
        amountEditorPanel.deleteCachedPanel();
        amountEditorPanel.openPanel();
    }

    private boolean isAutoPull() {
        return gasHandler.isAutoPull();
    }

    private boolean isStocking() {
        return gasHandler.isStocking();
    }

    private int slotX(int index) {
        return (index % COLUMNS) * CELL_SIZE;
    }

    private int slotY(int index) {
        return (index / COLUMNS) * PAIR_HEIGHT;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        var graphics = context.getGraphics();
        boolean autoPull = isAutoPull();
        boolean stocking = isStocking();

        for (int i = 0; i < slotCount; i++) {
            int x = slotX(i);
            int y = slotY(i);
            drawSlotBackground(context, x, y, autoPull);

            var config = configSyncHandler != null ? configSyncHandler.getClientConfig(i) : null;
            var stock = configSyncHandler != null ? configSyncHandler.getClientStock(i) : null;

            var configGas = GasGuiHelper.getGasStack(config);
            if (!configGas.isEmpty()) {
                GasGuiHelper.drawGas(graphics, configGas, x + 1, y + 1);
                if (!stocking) {
                    GasGuiHelper.drawAmountOverlay(graphics, configGas.getAmount(), x + 1, y + 1);
                }
            }

            var stockGas = GasGuiHelper.getGasStack(stock);
            if (!stockGas.isEmpty()) {
                GasGuiHelper.drawGas(graphics, stockGas, x + 1, y + 19);
                GasGuiHelper.drawAmountOverlay(graphics, stockGas.getAmount(), x + 1, y + 19);
            }

            float mouseX = context.getMouseX();
            float mouseY = context.getMouseY();
            if (mouseX >= x && mouseX < x + CELL_SIZE && mouseY >= y && mouseY < y + CELL_SIZE) {
                AEGuiHelper.drawSelectionOverlay(graphics, x + 1, y + 1, 16, 16);
            } else if (
                mouseX >= x && mouseX < x + CELL_SIZE &&
                    mouseY >= y + CELL_SIZE && mouseY < y + CELL_SIZE * 2
            ) {
                AEGuiHelper.drawSelectionOverlay(graphics, x + 1, y + 19, 16, 16);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawForeground(ModularGuiContext context) {
        float mouseX = context.getMouseX();
        float mouseY = context.getMouseY();

        for (int i = 0; i < slotCount; i++) {
            int x = slotX(i);
            int y = slotY(i);

            if (mouseX >= x && mouseX < x + CELL_SIZE && mouseY >= y && mouseY < y + CELL_SIZE * 2) {
                var tooltipStack = mouseY < y + CELL_SIZE ?
                    (configSyncHandler != null ? configSyncHandler.getClientConfig(i) : null) :
                    (configSyncHandler != null ? configSyncHandler.getClientStock(i) : null);
                if (GasGuiHelper.getGasStack(tooltipStack).isEmpty()) {
                    return;
                }
                GasGuiHelper.renderTooltip(
                    context.getGraphics(),
                    tooltipStack,
                    context.getAbsMouseX(),
                    context.getAbsMouseY()
                );
            }
        }
    }

    @Override
    @NotNull
    public Result onMousePressed(int button) {
        double localX = getContext().getMouseX() - getArea().x;
        double localY = getContext().getMouseY() - getArea().y;

        int slotIndex = getSlotAtLocal(localX, localY);
        if (slotIndex < 0) {
            return Result.IGNORE;
        }

        int y = slotY(slotIndex);
        boolean overConfig = localY < y + CELL_SIZE;

        if (overConfig && !isAutoPull() && syncManager != null) {
            if (button == 1) {
                syncManager.callSyncedAction("ae_config_clear", buf -> buf.writeVarInt(slotIndex));
                return Result.SUCCESS;
            }
            if (button == 0) {
                var config = configSyncHandler != null ? configSyncHandler.getClientConfig(slotIndex) : null;
                var gas = GasGuiHelper.getGasStack(config);
                boolean holdingItem = Minecraft.getInstance().player != null &&
                    !Minecraft.getInstance().player.containerMenu.getCarried().isEmpty();
                if (!gas.isEmpty() && !holdingItem) {
                    openAmountEditor(slotIndex);
                } else {
                    syncManager.callSyncedAction("ae_config_set", buf -> buf.writeVarInt(slotIndex));
                }
                return Result.SUCCESS;
            }
        }

        return Result.IGNORE;
    }

    @Override
    public boolean onMouseScrolled(double delta) {
        if (isStocking()) {
            return false;
        }

        double localX = getContext().getMouseX() - getArea().x;
        double localY = getContext().getMouseY() - getArea().y;
        int slotIndex = getSlotAtLocal(localX, localY);
        if (slotIndex < 0 || localY >= slotY(slotIndex) + CELL_SIZE) {
            return false;
        }

        var config = configSyncHandler != null ? configSyncHandler.getClientConfig(slotIndex) : null;
        var gas = GasGuiHelper.getGasStack(config);
        if (gas.isEmpty() || delta == 0 || syncManager == null) {
            return false;
        }

        long current = gas.getAmount();
        long next = Interactable.hasControlDown() ?
            (delta > 0 ? current * 2L : current / 2L) :
            (delta > 0 ? current + 1L : current - 1L);

        if (next > 0 && next < Integer.MAX_VALUE + 1L) {
            syncManager.callSyncedAction("ae_config_amount", buf -> {
                buf.writeVarInt(slotIndex);
                buf.writeVarLong(next);
            });
            return true;
        }
        return false;
    }

    private int getSlotAtLocal(double localX, double localY) {
        for (int i = 0; i < slotCount; i++) {
            int x = slotX(i);
            int y = slotY(i);
            if (localX >= x && localX < x + CELL_SIZE && localY >= y && localY < y + CELL_SIZE * 2) {
                return i;
            }
        }
        return -1;
    }

    @OnlyIn(Dist.CLIENT)
    private void drawSlotBackground(ModularGuiContext context, int x, int y, boolean autoPull) {
        if (autoPull) {
            GTGuiTextures.SLOT_DARK.draw(context, x, y, 18, 18);
            GTGuiTextures.CONFIG_ARROW.draw(context, x, y, 18, 18);
        } else {
            GTGuiTextures.FLUID_SLOT.draw(context, x, y, 18, 18);
            GTGuiTextures.CONFIG_ARROW_DARK.draw(context, x, y, 18, 18);
        }
        GTGuiTextures.SLOT_DARK.draw(context, x, y + 18, 18, 18);
    }
}
