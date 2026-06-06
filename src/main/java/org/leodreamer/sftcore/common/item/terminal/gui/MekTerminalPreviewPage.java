package org.leodreamer.sftcore.common.item.terminal.gui;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.item.terminal.builder.IMekMultiblockBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SceneWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;

/**
 * A terminal page with two mutually exclusive layers:
 * Opening the preview hides the normal page and Fancy UI chrome.
 */
@DataGenScanned
@Accessors(fluent = true)
public final class MekTerminalPreviewPage extends WidgetGroup {

    public static final int PAGE_WIDTH = 158;
    public static final int PAGE_HEIGHT = 158;

    private static final int BUTTON_SIZE = 18;
    private static final int BUTTON_MARGIN = 4;

    @RegisterLanguage("Show multiblock preview")
    public static final String SHOW_PREVIEW = "item.sftcore.mek_terminal.preview.show";

    @RegisterLanguage("Back")
    public static final String BACK = "item.sftcore.mek_terminal.preview.back";

    @RegisterLanguage("No blocks to preview")
    public static final String EMPTY = "item.sftcore.mek_terminal.preview.empty";

    @Getter
    private final WidgetGroup contentLayer;
    @Getter
    private final WidgetGroup previewLayer;
    @Getter
    private final WidgetGroup sceneHolder;

    private final IMekMultiblockBuilder builder;
    private final CompoundTag terminalTag;

    public MekTerminalPreviewPage(
        WidgetGroup contentLayer,
        IMekMultiblockBuilder builder,
        CompoundTag terminalTag
    ) {
        super(0, 0, PAGE_WIDTH, PAGE_HEIGHT);

        this.contentLayer = contentLayer;
        this.builder = builder;
        this.terminalTag = terminalTag;

        this.previewLayer = new WidgetGroup(0, 0, PAGE_WIDTH, PAGE_HEIGHT);
        this.previewLayer.setVisible(false);

        this.sceneHolder = new WidgetGroup(0, 0, PAGE_WIDTH, PAGE_HEIGHT);

        // SceneWidget is built lazily and exists only on the client.
        // Marking this holder as client-side ensures dynamically added scene
        // widgets do not participate in client/server UI synchronization.
        sceneHolder.setClientSideWidget();

        previewLayer.setVisible(false);
        previewLayer.setActive(false);

        addWidget(contentLayer);
        addWidget(previewLayer);

        previewLayer.addWidget(sceneHolder);

        contentLayer.addWidget(
            createLocalButton(
                buttonX(),
                buttonY(),
                "3D",
                Component.translatable(SHOW_PREVIEW),
                this::openPreview
            )
        );

        // Added after sceneHolder so that it is rendered above the scene and
        // receives mouse clicks before the SceneWidget.
        previewLayer.addWidget(
            createLocalButton(
                buttonX(),
                buttonY(),
                "<",
                Component.translatable(BACK),
                this::closePreview
            )
        );
    }

    private int buttonX() {
        return PAGE_WIDTH - BUTTON_SIZE - BUTTON_MARGIN;
    }

    private int buttonY() {
        return PAGE_HEIGHT - BUTTON_SIZE - BUTTON_MARGIN;
    }

    private ButtonWidget createLocalButton(
        int x,
        int y,
        String text,
        Component tooltip,
        Runnable action
    ) {
        var texture = new GuiTextureGroup(
            ResourceBorderTexture.BUTTON_COMMON,
            new TextTexture(text)
        );

        var button = new ButtonWidget(
            x,
            y,
            BUTTON_SIZE,
            BUTTON_SIZE,
            texture,
            clickData -> action.run()
        );

        button.setHoverTooltips(tooltip);
        button.setClientSideWidget();

        return button;
    }

    @OnlyIn(Dist.CLIENT)
    private void openPreview() {
        if (!LDLib.isRemote()) {
            return;
        }

        rebuildScene();

        contentLayer.setVisible(false);
        contentLayer.setActive(false);

        previewLayer.setVisible(true);
        previewLayer.setActive(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void closePreview() {
        previewLayer.setVisible(false);
        previewLayer.setActive(false);

        contentLayer.setVisible(true);
        contentLayer.setActive(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void rebuildScene() {
        // Rebuild on every open instead of caching forever.
        // This matters for configurable builders
        sceneHolder.clearAllWidgets();

        var states = builder.previewStates(terminalTag);

        if (states.isEmpty()) {
            sceneHolder.addWidget(
                new LabelWidget(
                    16,
                    PAGE_HEIGHT / 2 - 4,
                    Component.translatable(EMPTY)
                )
            );
            return;
        }

        var blocks = new LinkedHashMap<BlockPos, BlockInfo>();

        // avoid mekanism TESR/BER bug
        states.forEach(
            (pos, state) -> blocks.put(pos, new BlockInfo(state, false))
        );

        var previewWorld = new TrackedDummyWorld();
        previewWorld.addBlocks(blocks);

        var scene = new SceneWidget(
            0,
            0,
            PAGE_WIDTH,
            PAGE_HEIGHT,
            previewWorld
        )
            .setRenderFacing(false)
            .setRenderSelect(false)
            .setHoverTips(true)
            .setDraggable(true)
            .setScalable(true)
            .useCacheBuffer();

        scene.setClientSideWidget();
        scene.setRenderedCore(blocks.keySet());

        sceneHolder.addWidget(scene);
    }
}
