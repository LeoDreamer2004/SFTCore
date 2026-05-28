package org.leodreamer.sftcore.api.gui;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.mojang.blaze3d.systems.RenderSystem;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import java.util.ArrayList;
import java.util.List;

@DataGenScanned
public class GasSlotWidget extends Widget {
    protected final IGasHandler gasHandler;
    protected final int tank;

    protected GasStack lastGasInTank = GasStack.EMPTY;
    protected long lastTankCapacity = 0;

    protected boolean showAmount = true;
    protected boolean drawHoverTips = true;

    @RegisterLanguage("Gas")
    public static final String GAS = "sftcore.gui.gas";

    @RegisterLanguage("Empty")
    public static final String GAS_EMPTY = "sftcore.gui.gas.empty";

    @RegisterLanguage("Stored: %s / %s mB")
    public static final String GAS_STORED = "sftcore.gui.gas.stored";

    @RegisterLanguage("Amount: %s mB")
    public static final String GAS_AMOUNT = "sftcore.gui.gas.amount";

    public GasSlotWidget(IGasHandler gasHandler, int tank, int x, int y) {
        this(gasHandler, tank, x, y, 18, 18);
    }

    public GasSlotWidget(IGasHandler gasHandler, int tank, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.gasHandler = gasHandler;
        this.tank = tank;
    }

    public GasSlotWidget setShowAmount(boolean showAmount) {
        this.showAmount = showAmount;
        return this;
    }

    public GasSlotWidget setDrawHoverTips(boolean drawHoverTips) {
        this.drawHoverTips = drawHoverTips;
        return this;
    }

    @Override
    public GasSlotWidget setBackground(IGuiTexture... backgroundTexture) {
        super.setBackground(backgroundTexture);
        return this;
    }

    public GasStack getGas() {
        if (gasHandler == null || tank < 0 || tank >= gasHandler.getTanks()) {
            return GasStack.EMPTY;
        }
        return gasHandler.getChemicalInTank(tank);
    }

    public long getCapacity() {
        if (gasHandler == null || tank < 0 || tank >= gasHandler.getTanks()) {
            return 0;
        }
        return gasHandler.getTankCapacity(tank);
    }

    @Override
    public void writeInitialData(FriendlyByteBuf buffer) {
        var gas = getGas();
        long capacity = getCapacity();

        lastGasInTank = gas.copy();
        lastTankCapacity = capacity;

        buffer.writeNbt(gas.write(new CompoundTag()));
        buffer.writeVarLong(capacity);
    }

    @Override
    public void readInitialData(FriendlyByteBuf buffer) {
        var tag = buffer.readNbt();
        lastGasInTank = tag == null ? GasStack.EMPTY : GasStack.readFromNBT(tag);
        lastTankCapacity = buffer.readVarLong();
    }

    @Override
    public void detectAndSendChanges() {
        var gas = getGas();
        long capacity = getCapacity();

        if (capacity != lastTankCapacity) {
            lastTankCapacity = capacity;
            writeUpdateInfo(0, buf -> buf.writeVarLong(lastTankCapacity));
        }

        if (!sameGasType(gas, lastGasInTank)) {
            lastGasInTank = gas.copy();
            writeUpdateInfo(1, buf -> buf.writeNbt(lastGasInTank.write(new CompoundTag())));
        } else if (gas.getAmount() != lastGasInTank.getAmount()) {
            lastGasInTank = gas.copy();
            writeUpdateInfo(2, buf -> buf.writeVarLong(lastGasInTank.getAmount()));
        }
    }

    @Override
    public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
        if (id == 0) {
            lastTankCapacity = buffer.readVarLong();
        } else if (id == 1) {
            var tag = buffer.readNbt();
            lastGasInTank = tag == null ? GasStack.EMPTY : GasStack.readFromNBT(tag);
        } else if (id == 2) {
            if (!lastGasInTank.isEmpty()) {
                lastGasInTank.setAmount(buffer.readVarLong());
            }
        } else {
            super.readUpdateInfo(id, buffer);
        }
    }

    @Override
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);

        var pos = getPosition();
        var size = getSize();

        var gas = isClientSideWidget ? lastGasInTank : getGas();
        long capacity = isClientSideWidget ? lastTankCapacity : getCapacity();

        if (!gas.isEmpty() && capacity > 0) {
            float progress = Math.min(1.0F, gas.getAmount() / (float) capacity);
            int innerWidth = Math.max(0, size.width - 2);
            int innerHeight = Math.max(0, size.height - 2);
            int fillHeight = Math.max(1, (int) (innerHeight * progress));
            int fillY = pos.y + size.height - 1 - fillHeight;

            DrawerHelper.drawSolidRect(
                graphics,
                pos.x + 1,
                fillY,
                innerWidth,
                fillHeight,
                0xAA00D7C8
            );

            if (showAmount) {
                String amount = compact(gas.getAmount());
                var font = Minecraft.getInstance().font;
                graphics.pose().pushPose();
                graphics.pose().translate(0, 0, 400);
                graphics.drawString(
                    font,
                    amount,
                    pos.x + size.width - font.width(amount) - 1,
                    pos.y + size.height - 8,
                    0xFFFFFF,
                    true
                );
                graphics.pose().popPose();
            }
        }
    }

    @Override
    public void drawInForeground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (drawHoverTips && isMouseOverElement(mouseX, mouseY) && getHoverElement(mouseX, mouseY) == this && gui != null) {
            gui.getModularUIGui().setHoverTooltip(getFullTooltipTexts(), ItemStack.EMPTY, null, null);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            return;
        }
        super.drawInForeground(graphics, mouseX, mouseY, partialTicks);
    }

    public List<Component> getFullTooltipTexts() {
        var gas = isClientSideWidget ? lastGasInTank : getGas();
        long capacity = isClientSideWidget ? lastTankCapacity : getCapacity();

        var tooltip = new ArrayList<Component>();

        if (gas.isEmpty()) {
            tooltip.add(Component.translatable(GAS_EMPTY));
        } else {
            tooltip.add(gas.getTextComponent());
            tooltip.add(Component.translatable(
                GAS_STORED,
                format(gas.getAmount()),
                format(capacity)
            ));
        }

        return tooltip;
    }

    private static boolean sameGasType(GasStack a, GasStack b) {
        if (a == null || a.isEmpty()) {
            return b == null || b.isEmpty();
        }
        if (b == null || b.isEmpty()) {
            return false;
        }
        return a.isTypeEqual(b);
    }

    private static String compact(long amount) {
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

    private static String format(long amount) {
        return String.format("%,d", amount);
    }
}
