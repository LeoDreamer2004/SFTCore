package org.leodreamer.sftcore.api.gui;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.integration.mek.SFTMekanismCapabilities;

import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import brachy.modularui.api.ITheme;
import brachy.modularui.api.value.ISyncOrValue;
import brachy.modularui.api.widget.Interactable;
import brachy.modularui.drawable.GuiDraw;
import brachy.modularui.screen.RichTooltip;
import brachy.modularui.screen.viewport.ModularGuiContext;
import brachy.modularui.theme.SlotTheme;
import brachy.modularui.theme.WidgetThemeEntry;
import brachy.modularui.utils.MouseData;
import brachy.modularui.widget.Widget;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import lombok.Setter;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.client.gui.GuiUtils;
import mekanism.client.render.MekanismRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DataGenScanned
public class GasTankWidget extends Widget<GasTankWidget> implements Interactable {

    @RegisterLanguage("Gas")
    public static final String GAS = "sftcore.gui.gas";

    @RegisterLanguage("Empty")
    public static final String GAS_EMPTY = "sftcore.gui.gas.empty";

    @RegisterLanguage("Stored: %s / %s mB")
    public static final String GAS_STORED = "sftcore.gui.gas.stored";

    @RegisterLanguage("Gas Amount")
    public static final String GAS_AMOUNT = "sftcore.gui.gas.amount";

    private GasSlotSyncHandler syncHandler;
    @Getter
    @Setter
    private boolean showAmount = true;
    @Getter
    @Setter
    private boolean showAmountOverlay = true;

    public GasTankWidget() {
        size(18);
        tooltip().autoUpdate(true);
        tooltipBuilder(this::addTooltip);
    }

    public GasTankWidget(IGasHandler gasTank, int tank) {
        this();
        syncHandler(new GasSlotSyncHandler(gasTank, tank));
    }

    @Nullable
    public GasStack getGasStack() {
        return syncHandler == null ? null : syncHandler.getValue();
    }

    public long getCapacity() {
        return syncHandler == null ? 0 : syncHandler.getCapacity();
    }

    public void syncHandler(GasSlotSyncHandler syncHandler) {
        setSyncOrValue(ISyncOrValue.orEmpty(syncHandler));
    }

    public GasTankWidget setAllowClickDrained(boolean allowClickDrained) {
        if (syncHandler != null) {
            syncHandler.canFillSlot(allowClickDrained);
        }
        return this;
    }

    @Override
    public boolean isValidSyncOrValue(@NotNull ISyncOrValue syncOrValue) {
        return syncOrValue.isTypeOrEmpty(GasSlotSyncHandler.class);
    }

    @Override
    protected void setSyncOrValue(@NotNull ISyncOrValue syncOrValue) {
        super.setSyncOrValue(syncOrValue);
        this.syncHandler = syncOrValue.castNullable(GasSlotSyncHandler.class);
    }

    @Override
    public @NotNull GasSlotSyncHandler getSyncHandler() {
        if (syncHandler == null) {
            throw new IllegalStateException("Widget is not initialised or not synced!");
        }
        return syncHandler;
    }

    @Override
    protected WidgetThemeEntry<?> getWidgetThemeInternal(ITheme theme) {
        return theme.getFluidSlotTheme();
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        var gas = getGasStack();
        if (gas == null || gas.isEmpty()) {
            return;
        }

        int width = getArea().width - 2;
        int height = getArea().height - 2;
        if (width <= 0 || height <= 0) {
            return;
        }

        int filledHeight = height;
        long capacity = getCapacity();
        if (capacity > 0) {
            filledHeight = Math.max(1, (int) Math.ceil(height * (gas.getAmount() / (double) capacity)));
            filledHeight = Math.min(filledHeight, height);
        }

        drawSimpleGas(context.getGraphics(), gas, 1, 1 + height - filledHeight, width, filledHeight);
    }

    @Override
    public void drawOverlay(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        super.drawOverlay(context, widgetTheme);
        var gas = getGasStack();
        if (gas != null && !gas.isEmpty() && showAmount && showAmountOverlay) {
            GuiDraw.drawScaledAlignedTextInBox(
                context,
                FormattingUtil.formatNumbers(gas.getAmount()) + "B",
                1,
                0,
                getArea().width - 1,
                getArea().height,
                brachy.modularui.utils.Alignment.BottomRight
            );
        }
        if (isHovering()) {
            RenderSystem.colorMask(true, true, true, false);
            GuiDraw.drawRect(context.getGraphics(), 1, 1, getArea().w() - 2, getArea().h() - 2, getSlotHoverColor());
            RenderSystem.colorMask(true, true, true, true);
        }
    }

    @Override
    public @NotNull Result onMousePressed(int button) {
        if (syncHandler == null || (!syncHandler.canFillSlot() && !syncHandler.canDrainSlot())) {
            return Result.ACCEPT;
        }
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return Result.IGNORE;
        }
        var cursorStack = player.containerMenu.getCarried();
        if (!cursorStack.isEmpty() && cursorStack.getCapability(SFTMekanismCapabilities.GAS_HANDLER).isPresent()) {
            MouseData mouseData = MouseData.create(button);
            syncHandler.syncToServer(GasSlotSyncHandler.SYNC_CLICK, mouseData::writeToPacket);
            return Result.SUCCESS;
        }
        return Result.ACCEPT;
    }

    private int getSlotHoverColor() {
        var theme = getWidgetTheme(getPanel().getTheme(), SlotTheme.class);
        return theme.theme().getSlotHoverColor();
    }

    private void addTooltip(RichTooltip tooltip) {
        var gas = getGasStack();
        long capacity = getCapacity();
        if (gas != null && !gas.isEmpty()) {
            tooltip.addLine(gas.getTextComponent());
            if (showAmount) {
                tooltip.addLine(
                    Component.translatable(
                        GAS_STORED,
                        FormattingUtil.formatNumbers(gas.getAmount()),
                        FormattingUtil.formatNumbers(capacity)
                    )
                );
            }
        } else {
            tooltip.addLine(Component.translatable(GAS_EMPTY));
            if (showAmount) {
                tooltip.addLine(
                    Component.translatable(
                        GAS_STORED,
                        0,
                        FormattingUtil.formatNumbers(capacity)
                    )
                );
            }
        }
    }

    public static void drawSimpleGas(GuiGraphics graphics, GasStack gas, int x, int y) {
        drawSimpleGas(graphics, gas, x, y, 16, 16);
    }

    public static void drawSimpleGas(GuiGraphics graphics, GasStack gas, int x, int y, int width, int height) {
        if (gas.isEmpty()) {
            return;
        }
        var sprite = MekanismRenderer.getChemicalTexture(gas.getType());
        MekanismRenderer.color(graphics, gas);
        GuiUtils.drawTiledSprite(
            graphics,
            x,
            y + height,
            0,
            width,
            height,
            sprite,
            16,
            16,
            100,
            GuiUtils.TilingDirection.UP_RIGHT,
            true
        );
        MekanismRenderer.resetColor(graphics);
    }
}
