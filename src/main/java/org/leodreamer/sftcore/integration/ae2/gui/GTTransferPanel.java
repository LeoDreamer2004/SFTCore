package org.leodreamer.sftcore.integration.ae2.gui;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.integration.ae2.feature.ISendToGTMachine;
import org.leodreamer.sftcore.integration.ae2.logic.AvailableGTRow;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.Tooltip;
import appeng.client.gui.widgets.Scrollbar;
import appeng.core.AppEng;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@DataGenScanned
public class GTTransferPanel implements ICompositeWidget {

    private int x;
    private int y;
    private Rect2i bounds = new Rect2i(0, 0, 0, 0);

    private final ISendToGTMachine sender;
    private final Scrollbar scrollbar;

    @Setter
    @Getter
    private List<AvailableGTRow> rows = new ArrayList<>();

    private static final ResourceLocation BACKGROUND = AppEng.makeId("textures/guis/cpu_selector.png");
    private static final int HEADER = 19;
    private static final int LINE_HEIGHT = 23;
    private static final int PADDING_LEFT = 13;
    private static final int PADDING_TOP = 3;
    private static final int ROWS = 6;

    public GTTransferPanel(ISendToGTMachine sender, Scrollbar scrollbar) {
        this.sender = sender;
        scrollbar.setCaptureMouseWheel(false);
        this.scrollbar = scrollbar;
    }

    @Override
    public void setPosition(Point position) {
        x = position.getX();
        y = position.getY();
    }

    @Override
    public void setSize(int width, int height) {
        // skip
    }

    @Override
    public boolean isVisible() {
        return !rows.isEmpty();
    }

    @Override
    public void populateScreen(Consumer<AbstractWidget> addWidget, Rect2i bounds, AEBaseScreen<?> screen) {
        this.bounds = bounds;
    }

    @Override
    public Rect2i getBounds() {
        return new Rect2i(x, y, 94, 164);
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        this.bounds = bounds;

        guiGraphics.blit(BACKGROUND, absX(), absY(), 0, 0, 94, 164);

        var hovered = hoveredRow(mouse);
        var top = absY() + HEADER + hovered * LINE_HEIGHT;
        if (hovered != -1) {
            guiGraphics.fill(absX() + 9, top, absX() + 76, top + LINE_HEIGHT, 0x90FFFFFF);
        }
    }

    @RegisterLanguage("Auto Transfer")
    static final String TITLE = "sftcore.mixin.ae2.gt_auto_transfer";

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        this.bounds = bounds;

        var font = Minecraft.getInstance().font;
        // draw the title
        guiGraphics.drawString(
            font,
            Component.translatable(TITLE),
            absX() + 9,
            absY() + 6,
            Objects.requireNonNull(ChatFormatting.DARK_GRAY.getColor()),
            false
        );

        int scroll = scrollbar.getCurrentScroll();
        int drawLines = Math.min(ROWS, rows.size() - scroll);
        for (int i = 0; i < drawLines; i++) {
            var row = rows.get(i + scroll);
            var top = HEADER + i * LINE_HEIGHT + PADDING_TOP;

            var pose = guiGraphics.pose();

            // draw the item
            guiGraphics.renderItem(row.item().toStack(), absX() + PADDING_LEFT, absY() + top);

            // draw the name of machine
            pose.pushPose();
            float fontScale = 0.75f;
            pose.scale(fontScale, fontScale, 1.0f);
            guiGraphics.drawString(
                font,
                row.title(),
                (int) ((absX() + PADDING_LEFT + 20) / fontScale),
                (int) ((absY() + top) / fontScale),
                Objects.requireNonNull(ChatFormatting.DARK_GRAY.getColor()),
                false
            );
            pose.popPose();

            // draw the prompt
            pose.pushPose();
            float promptScale = 0.6f;
            pose.scale(promptScale, promptScale, 1.0f);
            if (!row.prompt().isEmpty()) {
                guiGraphics.drawString(
                    font,
                    row.prompt(),
                    (int) ((absX() + PADDING_LEFT + 20) / promptScale),
                    (int) ((absY() + top + 10) / promptScale),
                    Objects.requireNonNull(ChatFormatting.DARK_GRAY.getColor()),
                    false
                );
            }
            pose.popPose();
        }
    }

    @Override
    public boolean onMouseUp(Point mousePos, int button) {
        int row = hoveredRow(mousePos);
        if (row == -1) return false;
        sender.sftcore$sendToGTMachine(row + scrollbar.getCurrentScroll());
        return true;
    }

    @Override
    public boolean onMouseWheel(Point mousePos, double delta) {
        scrollbar.onMouseWheel(mousePos, delta);
        return true;
    }

    @Override
    public void updateBeforeRender() {
        var hiddenRows = Math.max(0, rows.size() - ROWS);
        System.out.println("Ok, " + rows.size() + " so hidden" + hiddenRows);
        scrollbar.setRange(0, hiddenRows, ROWS / 3);
    }

    @Override
    public @Nullable Tooltip getTooltip(int mouseX, int mouseY) {
        int row = hoveredRow(new Point(mouseX, mouseY));
        if (row == -1) return new Tooltip();
        return new Tooltip(rows.get(row).tooltips());
    }

    private int absX() {
        return bounds.getX() + x;
    }

    private int absY() {
        return bounds.getY() + y;
    }

    private int hoveredRow(Point mouse) {
        var relX = mouse.getX() - x;
        if (relX < 0 || relX >= 93) {
            return -1;
        }
        var relY = mouse.getY() - y - HEADER - PADDING_TOP;
        int rowIdx = relY / LINE_HEIGHT;
        if (relY < 0 || rowIdx < 0 || rowIdx >= rows.size()) {
            return -1;
        }
        return rowIdx;
    }
}
