package org.leodreamer.sftcore.api.gui;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.systems.RenderSystem;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.client.gui.GuiUtils;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import java.util.ArrayList;
import java.util.List;

public class GasTankWidget extends Widget {
    @Nullable
    protected IGasHandler gasTank;

    protected int tank;

    protected GasStack lastGasInTank = GasStack.EMPTY;
    protected long lastTankCapacity = 0;

    protected boolean showAmount;
    protected boolean showAmountOverlay = true;
    protected boolean drawHoverTips = true;
    protected boolean drawHoverOverlay = true;

    protected boolean allowClickFilled;
    protected boolean allowClickDrained;

    @RegisterLanguage("Gas")
    public static final String GAS = "sftcore.gui.gas";

    @RegisterLanguage("Empty")
    public static final String GAS_EMPTY = "sftcore.gui.gas.empty";

    @RegisterLanguage("Stored: %s / %s mB")
    public static final String GAS_STORED = "sftcore.gui.gas.stored";

    @RegisterLanguage("Amount: %s mB")
    public static final String GAS_AMOUNT = "sftcore.gui.gas.amount";

    public GasTankWidget() {
        this(null, 0, 0, 0, 18, 18, true, true);
    }

    public GasTankWidget(
        @Nullable IGasHandler gasTank,
        int x,
        int y,
        boolean allowClickContainerFilling,
        boolean allowClickContainerEmptying
    ) {
        this(gasTank, 0, x, y, 18, 18, allowClickContainerFilling, allowClickContainerEmptying);
    }

    public GasTankWidget(
        @Nullable IGasHandler gasTank,
        int tank,
        int x,
        int y,
        boolean allowClickContainerFilling,
        boolean allowClickContainerEmptying
    ) {
        this(gasTank, tank, x, y, 18, 18, allowClickContainerFilling, allowClickContainerEmptying);
    }

    public GasTankWidget(
        @Nullable IGasHandler gasTank,
        int tank,
        int x,
        int y,
        int width,
        int height,
        boolean allowClickContainerFilling,
        boolean allowClickContainerEmptying
    ) {
        super(new Position(x, y), new Size(width, height));
        this.showAmount = true;
        this.drawHoverTips = true;
        this.allowClickFilled = allowClickContainerFilling;
        this.allowClickDrained = allowClickContainerEmptying;
        setGasTank(gasTank, tank);
    }

    public GasTankWidget setGasTank(@Nullable IGasHandler gasTank) {
        return setGasTank(gasTank, 0);
    }

    public GasTankWidget setGasTank(@Nullable IGasHandler gasTank, int tank) {
        this.gasTank = gasTank;
        this.tank = tank;

        if (isClientSideWidget) {
            setClientSideWidget();
        }

        return this;
    }

    public GasStack getGas() {
        if (isClientSideWidget || isRemote()) {
            return lastGasInTank == null ? GasStack.EMPTY : lastGasInTank;
        }

        return gasTank == null ? GasStack.EMPTY : gasTank.getChemicalInTank(tank);
    }

    public long getCapacity() {
        if (isClientSideWidget || isRemote()) {
            return lastTankCapacity;
        }

        return gasTank == null ? 0 : gasTank.getTankCapacity(tank);
    }

    public GasTankWidget setShowAmount(boolean showAmount) {
        this.showAmount = showAmount;
        return this;
    }

    public GasTankWidget setShowAmountOverlay(boolean showAmountOverlay) {
        this.showAmountOverlay = showAmountOverlay;
        return this;
    }

    public GasTankWidget setDrawHoverTips(boolean drawHoverTips) {
        this.drawHoverTips = drawHoverTips;
        return this;
    }

    public GasTankWidget setDrawHoverOverlay(boolean drawHoverOverlay) {
        this.drawHoverOverlay = drawHoverOverlay;
        return this;
    }

    public GasTankWidget setAllowClickFilled(boolean allowClickFilled) {
        this.allowClickFilled = allowClickFilled;
        return this;
    }

    public GasTankWidget setAllowClickDrained(boolean allowClickDrained) {
        this.allowClickDrained = allowClickDrained;
        return this;
    }

    @Override
    public GasTankWidget setBackground(IGuiTexture... backgroundTexture) {
        super.setBackground(backgroundTexture);
        return this;
    }

    @Override
    public GasTankWidget setClientSideWidget() {
        super.setClientSideWidget();

        if (gasTank != null) {
            this.lastGasInTank = gasTank.getChemicalInTank(tank).copy();
            this.lastTankCapacity = gasTank.getTankCapacity(tank);
        } else {
            this.lastGasInTank = GasStack.EMPTY;
            this.lastTankCapacity = 0;
        }

        return this;
    }

    public List<Component> getFullTooltipTexts() {
        List<Component> tooltips = new ArrayList<>();
        GasStack gasStack = lastGasInTank == null ? GasStack.EMPTY : lastGasInTank;

        if (!gasStack.isEmpty()) {
            tooltips.add(gasStack.getTextComponent());

            if (showAmount) {
                tooltips.add(Component.translatable(GAS_STORED,
                    formatAmount(gasStack.getAmount()), formatAmount(lastTankCapacity)
                ));
            }
        } else {
            tooltips.add(Component.translatable(GAS_EMPTY));

            if (showAmount) {
                tooltips.add(Component.translatable(GAS_STORED, 0, formatAmount(lastTankCapacity)));
            }
        }

        tooltips.addAll(getTooltipTexts());
        return tooltips;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);

        if (isClientSideWidget && gasTank != null) {
            GasStack gasStack = gasTank.getChemicalInTank(tank);
            long capacity = gasTank.getTankCapacity(tank);

            if (capacity != lastTankCapacity) {
                this.lastTankCapacity = capacity;
            }

            if (!sameGasType(gasStack, lastGasInTank) || gasStack.getAmount() != lastGasInTank.getAmount()) {
                this.lastGasInTank = gasStack.copy();
            }
        }

        GasStack renderedGas = lastGasInTank == null ? GasStack.EMPTY : lastGasInTank;

        if (!renderedGas.isEmpty()) {
            Position pos = getPosition();
            Size size = getSize();

            int innerX = pos.x + 1;
            int innerY = pos.y + 1;
            int innerWidth = Math.max(0, size.width - 2);
            int innerHeight = Math.max(0, size.height - 2);

            if (innerWidth > 0 && innerHeight > 0) {
                long capacity = Math.max(Math.max(renderedGas.getAmount(), lastTankCapacity), 1L);
                double progress = renderedGas.getAmount() / (double) capacity;

                int filledHeight = Math.max(1, (int) Math.ceil(innerHeight * progress));
                filledHeight = Math.min(filledHeight, innerHeight);

                int bottomY = innerY + innerHeight;

                var sprite = MekanismRenderer.getChemicalTexture(renderedGas.getType());

                MekanismRenderer.color(graphics, renderedGas);

                GuiUtils.drawTiledSprite(
                    graphics,
                    innerX,
                    bottomY,
                    0,
                    innerWidth,
                    filledHeight,
                    sprite,
                    16,
                    16,
                    100,
                    GuiUtils.TilingDirection.UP_RIGHT,
                    true
                );

                MekanismRenderer.resetColor(graphics);
            }

            if (showAmount && showAmountOverlay) {
                renderAmountOverlay(graphics, renderedGas);
            }
        }

        drawOverlay(graphics, mouseX, mouseY, partialTicks);

        if (drawHoverOverlay && isMouseOverElement(mouseX, mouseY) && getHoverElement(mouseX, mouseY) == this) {
            RenderSystem.colorMask(true, true, true, false);
            DrawerHelper.drawSolidRect(
                graphics,
                getPosition().x + 1,
                getPosition().y + 1,
                getSize().width - 2,
                getSize().height - 2,
                0x80FFFFFF
            );
            RenderSystem.colorMask(true, true, true, true);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInForeground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (drawHoverTips && isMouseOverElement(mouseX, mouseY) && getHoverElement(mouseX, mouseY) == this) {
            if (gui != null) {
                gui.getModularUIGui().setHoverTooltip(getFullTooltipTexts(), ItemStack.EMPTY, null, null);
            }
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            super.drawInForeground(graphics, mouseX, mouseY, partialTicks);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void renderAmountOverlay(GuiGraphics graphics, GasStack gasStack) {
        if (gasStack.isEmpty()) {
            return;
        }

        Position pos = getPosition();
        Size size = getSize();

        String text = compactAmount(gasStack.getAmount());

        Font font = Minecraft.getInstance().font;

        graphics.pose().pushPose();
        graphics.pose().scale(0.5F, 0.5F, 1.0F);

        int x = (int) ((pos.x + size.width - 1) * 2 - font.width(text));
        int y = (pos.y + size.height - 7) * 2;

        graphics.drawString(font, text, x, y, 0xFFFFFF, true);

        graphics.pose().popPose();
    }

    @Override
    public void detectAndSendChanges() {
        if (gasTank == null) {
            super.detectAndSendChanges();
            return;
        }

        GasStack gasStack = gasTank.getChemicalInTank(tank);
        long capacity = gasTank.getTankCapacity(tank);

        if (capacity != lastTankCapacity) {
            this.lastTankCapacity = capacity;
            writeUpdateInfo(0, buffer -> buffer.writeVarLong(lastTankCapacity));
        }

        if (!sameGasType(gasStack, lastGasInTank)) {
            this.lastGasInTank = gasStack.copy();
            CompoundTag tag = lastGasInTank.write(new CompoundTag());
            writeUpdateInfo(2, buffer -> buffer.writeNbt(tag));
        } else if (gasStack.getAmount() != lastGasInTank.getAmount()) {
            this.lastGasInTank = gasStack.copy();
            writeUpdateInfo(3, buffer -> buffer.writeVarLong(lastGasInTank.getAmount()));
        } else {
            super.detectAndSendChanges();
        }
    }

    @Override
    public void writeInitialData(FriendlyByteBuf buffer) {
        buffer.writeBoolean(gasTank != null);

        if (gasTank != null) {
            this.lastTankCapacity = gasTank.getTankCapacity(tank);
            this.lastGasInTank = gasTank.getChemicalInTank(tank).copy();

            buffer.writeVarLong(lastTankCapacity);
            buffer.writeNbt(lastGasInTank.write(new CompoundTag()));
        }
    }

    @Override
    public void readInitialData(FriendlyByteBuf buffer) {
        if (buffer.readBoolean()) {
            this.lastTankCapacity = buffer.readVarLong();
            this.lastGasInTank = GasStack.readFromNBT(buffer.readNbt());
        } else {
            this.lastTankCapacity = 0;
            this.lastGasInTank = GasStack.EMPTY;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
        if (id == 0) {
            this.lastTankCapacity = buffer.readVarLong();
        } else if (id == 2) {
            this.lastGasInTank = GasStack.readFromNBT(buffer.readNbt());
        } else if (id == 3) {
            long amount = buffer.readVarLong();

            if (lastGasInTank != null && !lastGasInTank.isEmpty()) {
                this.lastGasInTank = new GasStack(lastGasInTank, amount);
            } else {
                this.lastGasInTank = GasStack.EMPTY;
            }
        } else {
            super.readUpdateInfo(id, buffer);
        }
    }

    private static boolean sameGasType(@Nullable GasStack a, @Nullable GasStack b) {
        if (a == null || a.isEmpty()) {
            return b == null || b.isEmpty();
        }

        if (b == null || b.isEmpty()) {
            return false;
        }

        return a.isTypeEqual(b);
    }

    private static String formatAmount(long amount) {
        return String.format("%,d", amount);
    }

    private static String compactAmount(long amount) {
        if (amount >= 1_000_000_000L) {
            return amount / 1_000_000_000L + "G";
        }
        if (amount >= 1_000_000L) {
            return amount / 1_000_000L + "M";
        }
        if (amount >= 1_000L) {
            return amount / 1_000L + "K";
        }
        return Long.toString(amount);
    }
}
