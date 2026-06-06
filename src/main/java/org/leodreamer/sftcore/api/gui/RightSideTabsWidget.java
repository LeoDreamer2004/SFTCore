package org.leodreamer.sftcore.api.gui;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RightSideTabsWidget extends Widget {

    private static final int TAB_SIZE = 24;
    private static final int TOP_PADDING = 8;

    private final Consumer<IFancyUIProvider> onTabClick;
    private final List<IFancyUIProvider> tabs = new ArrayList<>();

    @Nullable
    private IFancyUIProvider selectedTab;

    private final IGuiTexture tabTexture = new ResourceTexture(GTCEu.id("gtceu:textures/gui/tab/tabs_right.png"))
        .getSubTexture(0.5f, 1 / 3f, 0.5f, 1 / 3f);

    private final IGuiTexture tabHoverTexture = new ResourceTexture(GTCEu.id("textures/gui/tab/tabs_right.png"))
        .getSubTexture(0, 1 / 3f, 0.5f, 1 / 3f);

    public RightSideTabsWidget(
        Consumer<IFancyUIProvider> onTabClick,
        int x,
        int y,
        int width,
        int height
    ) {
        super(x, y, width, height);
        this.onTabClick = onTabClick;
    }

    public void setTabs(List<IFancyUIProvider> tabs) {
        this.tabs.clear();
        this.tabs.addAll(tabs);
    }

    public boolean isEmpty() {
        return tabs.isEmpty();
    }

    public void selectTab(IFancyUIProvider selectedTab) {
        this.selectedTab = selectedTab;
        detectAndSendChanges();
    }

    @Override
    public void handleClientAction(
        int id,
        FriendlyByteBuf buffer
    ) {
        super.handleClientAction(id, buffer);

        if (id != 0) {
            return;
        }

        int index = buffer.readVarInt();
        if (index < 0 || index >= tabs.size()) {
            return;
        }

        var tab = tabs.get(index);
        this.selectedTab = tab;
        this.onTabClick.accept(tab);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOverElement(mouseX, mouseY)) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        int index = ((int) mouseY - getPosition().y - TOP_PADDING) / TAB_SIZE;

        if (index < 0 || index >= tabs.size()) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        var tab = tabs.get(index);
        if (tab != selectedTab) {
            this.selectedTab = tab;

            writeClientAction(0, buf -> buf.writeVarInt(index));
            onTabClick.accept(tab);
            playButtonClickSound();
        }

        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(
        @NotNull GuiGraphics graphics,
        int mouseX,
        int mouseY,
        float partialTicks
    ) {
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);

        var hoveredTab = getHoveredTab(mouseX, mouseY);
        int x = getPosition().x;
        int y = getPosition().y + TOP_PADDING;

        for (int i = 0; i < tabs.size(); i++) {
            drawTab(
                tabs.get(i),
                graphics,
                mouseX,
                mouseY,
                x,
                y + i * TAB_SIZE,
                hoveredTab
            );
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInForeground(
        @NotNull GuiGraphics graphics,
        int mouseX,
        int mouseY,
        float partialTicks
    ) {
        var hoveredTab = getHoveredTab(mouseX, mouseY);

        if (
            hoveredTab != null &&
                gui != null &&
                gui.getModularUIGui() != null
        ) {
            gui.getModularUIGui().setHoverTooltip(
                hoveredTab.getTabTooltips(),
                ItemStack.EMPTY,
                null,
                hoveredTab.getTabTooltipComponent()
            );
        }

        super.drawInForeground(
            graphics,
            mouseX,
            mouseY,
            partialTicks
        );
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    private IFancyUIProvider getHoveredTab(double mouseX, double mouseY) {
        if (!isMouseOverElement(mouseX, mouseY)) {
            return null;
        }

        int index = ((int) mouseY - getPosition().y - TOP_PADDING) / TAB_SIZE;

        if (index < 0 || index >= tabs.size()) {
            return null;
        }

        return tabs.get(index);
    }

    @OnlyIn(Dist.CLIENT)
    private void drawTab(
        IFancyUIProvider tab,
        @NotNull GuiGraphics graphics,
        int mouseX,
        int mouseY,
        int x,
        int y,
        @Nullable IFancyUIProvider hoveredTab
    ) {
        var texture = tab == selectedTab || tab == hoveredTab ? tabHoverTexture : tabTexture;
        texture.draw(
            graphics,
            mouseX,
            mouseY,
            x,
            y,
            RightSideTabsWidget.TAB_SIZE,
            RightSideTabsWidget.TAB_SIZE
        );

        tab.getTabIcon().draw(
            graphics,
            mouseX,
            mouseY,
            x + 4,
            y + 4,
            16,
            16
        );
    }
}
