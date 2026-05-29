package org.leodreamer.sftcore.api.gui;

import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.util.TextFormattingUtil;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.client.gui.GuiUtils;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.machine.trait.NotifiableGasTank;
import org.leodreamer.sftcore.integration.mek.SFTMekanismCapabilities;

import java.util.ArrayList;
import java.util.List;

@Accessors(chain = true)
@DataGenScanned
public class GasTankWidget extends Widget {

    @Nullable
    protected IGasHandler gasTank;

    @Getter
    protected int tank;

    protected GasStack lastGasInTank = GasStack.EMPTY;
    protected long lastTankCapacity = 0;

    @Setter
    protected boolean showAmount = true;
    @Setter
    protected boolean showAmountOverlay = true;
    @Setter
    protected boolean drawHoverTips = true;
    @Setter
    protected boolean drawHoverOverlay = true;
    @Setter
    protected boolean allowClickFilled = true;
    @Setter
    protected boolean allowClickDrained = true;

    @RegisterLanguage("Gas")
    public static final String GAS = "sftcore.gui.gas";

    @RegisterLanguage("Empty")
    public static final String GAS_EMPTY = "sftcore.gui.gas.empty";

    @RegisterLanguage("Stored: %s / %s mB")
    public static final String GAS_STORED = "sftcore.gui.gas.stored";

    @RegisterLanguage("Gas Amount")
    public static final String GAS_AMOUNT = "sftcore.gui.gas.amount";

    private static final int CLIENT_ACTION_CLICK_CONTAINER = 1;
    private static final int UPDATE_CARRIED_STACK = 4;

    public GasTankWidget(
        @Nullable IGasHandler gasTank,
        int tank,
        int x,
        int y,
        int width,
        int height
    ) {
        super(new Position(x, y), new Size(width, height));
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

    @Override
    public GasTankWidget setBackground(IGuiTexture... backgroundTexture) {
        super.setBackground(backgroundTexture);
        return this;
    }

    // ----------------------------
    // ********** GUI *************
    // ----------------------------

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
        var tooltips = new ArrayList<Component>();
        var gasStack = lastGasInTank == null ? GasStack.EMPTY : lastGasInTank;

        if (!gasStack.isEmpty()) {
            tooltips.add(gasStack.getTextComponent());

            if (showAmount) {
                tooltips.add(
                    Component.translatable(
                        GAS_STORED,
                        FormattingUtil.formatNumbers(gasStack.getAmount()),
                        FormattingUtil.formatNumbers(lastTankCapacity)
                    )
                );
            }
        } else {
            tooltips.add(Component.translatable(GAS_EMPTY));

            if (showAmount) {
                tooltips.add(
                    Component.translatable(
                        GAS_STORED,
                        0,
                        FormattingUtil.formatNumbers(lastTankCapacity)
                    )
                );
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
            var gasStack = gasTank.getChemicalInTank(tank);
            long capacity = gasTank.getTankCapacity(tank);

            if (capacity != lastTankCapacity) {
                this.lastTankCapacity = capacity;
            }

            if (!sameGasType(gasStack, lastGasInTank) || gasStack.getAmount() != lastGasInTank.getAmount()) {
                this.lastGasInTank = gasStack.copy();
            }
        }

        var renderedGas = lastGasInTank == null ? GasStack.EMPTY : lastGasInTank;

        if (!renderedGas.isEmpty()) {
            var pos = getPosition();
            var size = getSize();

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

    // ----------------------------
    // ********** Sync ************
    // ----------------------------

    @OnlyIn(Dist.CLIENT)
    private void renderAmountOverlay(GuiGraphics graphics, GasStack gasStack) {
        if (gasStack.isEmpty()) {
            return;
        }

        var pos = getPosition();
        var size = getSize();

        String text = TextFormattingUtil.formatLongToCompactStringBuckets(gasStack.getAmount(), 3) + "B";
        var font = Minecraft.getInstance().font;

        graphics.pose().pushPose();
        graphics.pose().scale(0.5F, 0.5F, 1.0F);

        graphics.drawString(
            font,
            text,
            (int) ((pos.x + (size.width / 3f)) * 2 - font.width(text) + 21),
            (int) ((pos.y + (size.height / 3f) + 6) * 2),
            0xFFFFFF,
            true
        );

        graphics.pose().popPose();
    }

    @Override
    public void detectAndSendChanges() {
        if (gasTank == null) {
            super.detectAndSendChanges();
            return;
        }

        var gasStack = gasTank.getChemicalInTank(tank);
        long capacity = gasTank.getTankCapacity(tank);

        if (capacity != lastTankCapacity) {
            this.lastTankCapacity = capacity;
            writeUpdateInfo(0, buffer -> buffer.writeVarLong(lastTankCapacity));
        }

        if (!sameGasType(gasStack, lastGasInTank)) {
            this.lastGasInTank = gasStack.copy();
            var tag = lastGasInTank.write(new CompoundTag());
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

    // ----------------------------
    // ******** Interact **********
    // ----------------------------

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOverElement(mouseX, mouseY)) {
            if ((allowClickDrained || allowClickFilled) && button == 0 && hasHeldGasContainer()) {
                writeClientAction(CLIENT_ACTION_CLICK_CONTAINER, buffer -> buffer.writeBoolean(isShiftDown()));
                playButtonClickSound();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @OnlyIn(Dist.CLIENT)
    private boolean hasHeldGasContainer() {
        if (gui == null || gui.getModularUIContainer() == null) {
            return false;
        }

        var carried = gui.getModularUIContainer().getCarried();
        return !carried.isEmpty() && carried.getCapability(SFTMekanismCapabilities.GAS_HANDLER).isPresent();
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        super.handleClientAction(id, buffer);

        if (id == CLIENT_ACTION_CLICK_CONTAINER) {
            boolean shift = buffer.readBoolean();

            if (tryClickGasContainer(shift)) {
                writeUpdateInfo(UPDATE_CARRIED_STACK, buf -> buf.writeItem(gui.getModularUIContainer().getCarried()));
            }
        }
    }

    private boolean tryClickGasContainer(boolean shift) {
        if (gasTank == null || gui == null || gui.getModularUIContainer() == null) {
            return false;
        }

        var carried = gui.getModularUIContainer().getCarried();
        if (carried.isEmpty()) {
            return false;
        }

        var itemHandler = carried.getCapability(SFTMekanismCapabilities.GAS_HANDLER)
            .resolve()
            .orElse(null);

        if (itemHandler == null) {
            return false;
        }

        boolean changed = false;
        int maxAttempts = shift ? Math.max(1, carried.getCount()) : 1;

        for (int i = 0; i < maxAttempts; i++) {
            boolean transferred = false;

            // If there is gas in the slot, give priority to filling
            // the held chemical container with the gas in the slot.
            if (allowClickFilled && !getServerGasInTank().isEmpty()) {
                transferred = fillHeldGasContainer(itemHandler);
            }

            // If not, or the container on the hand cannot receive any gas,
            // try to drain the held chemical container into the slot.
            if (!transferred && allowClickDrained) {
                transferred = drainHeldGasContainer(itemHandler);
            }

            if (!transferred) {
                break;
            }

            changed = true;
        }

        if (changed) {
            gui.getModularUIContainer().setCarried(carried);
            detectAndSendChanges();
        }

        return changed;
    }

    private GasStack getServerGasInTank() {
        if (gasTank == null) {
            return GasStack.EMPTY;
        }

        var stack = gasTank.getChemicalInTank(tank);
        return stack.isEmpty() ? GasStack.EMPTY : stack.copy();
    }

    private boolean fillHeldGasContainer(IGasHandler itemHandler) {
        var stored = getServerGasInTank();
        if (stored.isEmpty()) {
            return false;
        }

        var simulatedRemainder = itemHandler.insertChemical(stored.copy(), Action.SIMULATE);
        long accepted = stored.getAmount() - simulatedRemainder.getAmount();

        if (accepted <= 0) {
            return false;
        }

        var extracted = extractFromWidgetTank(accepted);
        if (extracted.isEmpty()) {
            return false;
        }

        var remainder = itemHandler.insertChemical(extracted.copy(), Action.EXECUTE);
        long inserted = extracted.getAmount() - remainder.getAmount();

        if (!remainder.isEmpty()) {
            insertIntoWidgetTank(remainder, Action.EXECUTE);
        }

        return inserted > 0;
    }

    private boolean drainHeldGasContainer(IGasHandler itemHandler) {
        for (int itemTank = 0; itemTank < itemHandler.getTanks(); itemTank++) {
            var inItem = itemHandler.getChemicalInTank(itemTank);
            if (inItem.isEmpty()) {
                continue;
            }

            var simulatedRemainder = insertIntoWidgetTank(inItem.copy(), Action.SIMULATE);
            long accepted = inItem.getAmount() - simulatedRemainder.getAmount();

            if (accepted <= 0) {
                continue;
            }

            var extracted = itemHandler.extractChemical(itemTank, accepted, Action.EXECUTE);
            if (extracted.isEmpty()) {
                continue;
            }

            var remainder = insertIntoWidgetTank(extracted.copy(), Action.EXECUTE);
            long inserted = extracted.getAmount() - remainder.getAmount();

            if (!remainder.isEmpty()) {
                itemHandler.insertChemical(remainder, Action.EXECUTE);
            }

            return inserted > 0;
        }

        return false;
    }

    private GasStack insertIntoWidgetTank(GasStack stack, Action action) {
        if (gasTank == null || stack.isEmpty()) {
            return stack;
        }

        if (gasTank instanceof NotifiableGasTank notifiableGasTank) {
            return notifiableGasTank.insertChemicalManual(tank, stack, action);
        }

        return gasTank.insertChemical(tank, stack, action);
    }

    private GasStack extractFromWidgetTank(long amount) {
        if (gasTank == null || amount <= 0) {
            return GasStack.EMPTY;
        }

        if (gasTank instanceof NotifiableGasTank notifiableGasTank) {
            return notifiableGasTank.extractChemicalManual(tank, amount, Action.EXECUTE);
        }

        return gasTank.extractChemical(tank, amount, Action.EXECUTE);
    }
}
