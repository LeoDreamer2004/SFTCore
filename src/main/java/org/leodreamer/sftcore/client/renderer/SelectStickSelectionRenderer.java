package org.leodreamer.sftcore.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.command.DumpCommand;
import org.leodreamer.sftcore.common.data.SFTItems;

@Mod.EventBusSubscriber(
    modid = SFTCore.MOD_ID,
    value = Dist.CLIENT,
    bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class SelectStickSelectionRenderer {
    private static final float FILL_R = 0.15F;
    private static final float FILL_G = 0.75F;
    private static final float FILL_B = 1.00F;
    private static final float FILL_A = 0.14F;

    private static final float LINE_R = 0.20F;
    private static final float LINE_G = 0.95F;
    private static final float LINE_B = 1.00F;
    private static final float LINE_A = 0.75F;

    private SelectStickSelectionRenderer() {}

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null || mc.level == null) {
            return;
        }

        if (!isHoldingSelectStick(player)) {
            return;
        }

        var area = DumpCommand.SelectedData.getCompleteSelectedArea(player).orElse(null);
        if (area == null) {
            return;
        }

        var box = createSelectionBox(area.pos1, area.pos2);
        renderSelectionBox(event.getPoseStack(), event.getCamera(), box);
    }

    private static boolean isHoldingSelectStick(LocalPlayer player) {
        return isSelectStick(player.getMainHandItem()) || isSelectStick(player.getOffhandItem());
    }

    private static boolean isSelectStick(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == SFTItems.SELECT_STICK.get();
    }

    private static AABB createSelectionBox(BlockPos a, BlockPos b) {
        int minX = Math.min(a.getX(), b.getX());
        int minY = Math.min(a.getY(), b.getY());
        int minZ = Math.min(a.getZ(), b.getZ());

        int maxX = Math.max(a.getX(), b.getX()) + 1;
        int maxY = Math.max(a.getY(), b.getY()) + 1;
        int maxZ = Math.max(a.getZ(), b.getZ()) + 1;

        // avoid z-fighting
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(0.003D);
    }

    private static void renderSelectionBox(PoseStack poseStack, Camera camera, AABB box) {
        var cameraPos = camera.getPosition();

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        drawFilledBox(poseStack, box, FILL_R, FILL_G, FILL_B, FILL_A);
        drawLineBox(poseStack, box, LINE_R, LINE_G, LINE_B, LINE_A);

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    private static void drawFilledBox(PoseStack poseStack, AABB box, float r, float g, float b, float a) {
        var builder = Tesselator.getInstance().getBuilder();
        var matrix = poseStack.last().pose();

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        double minX = box.minX;
        double minY = box.minY;
        double minZ = box.minZ;
        double maxX = box.maxX;
        double maxY = box.maxY;
        double maxZ = box.maxZ;

        // bottom
        vertex(builder, matrix, minX, minY, minZ, r, g, b, a);
        vertex(builder, matrix, maxX, minY, minZ, r, g, b, a);
        vertex(builder, matrix, maxX, minY, maxZ, r, g, b, a);
        vertex(builder, matrix, minX, minY, maxZ, r, g, b, a);

        // top
        vertex(builder, matrix, minX, maxY, maxZ, r, g, b, a);
        vertex(builder, matrix, maxX, maxY, maxZ, r, g, b, a);
        vertex(builder, matrix, maxX, maxY, minZ, r, g, b, a);
        vertex(builder, matrix, minX, maxY, minZ, r, g, b, a);

        // north
        vertex(builder, matrix, minX, minY, minZ, r, g, b, a);
        vertex(builder, matrix, minX, maxY, minZ, r, g, b, a);
        vertex(builder, matrix, maxX, maxY, minZ, r, g, b, a);
        vertex(builder, matrix, maxX, minY, minZ, r, g, b, a);

        // south
        vertex(builder, matrix, maxX, minY, maxZ, r, g, b, a);
        vertex(builder, matrix, maxX, maxY, maxZ, r, g, b, a);
        vertex(builder, matrix, minX, maxY, maxZ, r, g, b, a);
        vertex(builder, matrix, minX, minY, maxZ, r, g, b, a);

        // west
        vertex(builder, matrix, minX, minY, maxZ, r, g, b, a);
        vertex(builder, matrix, minX, maxY, maxZ, r, g, b, a);
        vertex(builder, matrix, minX, maxY, minZ, r, g, b, a);
        vertex(builder, matrix, minX, minY, minZ, r, g, b, a);

        // east
        vertex(builder, matrix, maxX, minY, minZ, r, g, b, a);
        vertex(builder, matrix, maxX, maxY, minZ, r, g, b, a);
        vertex(builder, matrix, maxX, maxY, maxZ, r, g, b, a);
        vertex(builder, matrix, maxX, minY, maxZ, r, g, b, a);

        BufferUploader.drawWithShader(builder.end());
    }

    private static void drawLineBox(PoseStack poseStack, AABB box, float r, float g, float b, float a) {
        var builder = Tesselator.getInstance().getBuilder();
        var matrix = poseStack.last().pose();

        builder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        double minX = box.minX;
        double minY = box.minY;
        double minZ = box.minZ;
        double maxX = box.maxX;
        double maxY = box.maxY;
        double maxZ = box.maxZ;

        line(builder, matrix, minX, minY, minZ, maxX, minY, minZ, r, g, b, a);
        line(builder, matrix, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a);
        line(builder, matrix, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a);
        line(builder, matrix, minX, minY, maxZ, minX, minY, minZ, r, g, b, a);

        line(builder, matrix, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a);
        line(builder, matrix, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a);
        line(builder, matrix, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
        line(builder, matrix, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a);

        line(builder, matrix, minX, minY, minZ, minX, maxY, minZ, r, g, b, a);
        line(builder, matrix, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a);
        line(builder, matrix, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a);
        line(builder, matrix, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a);

        BufferUploader.drawWithShader(builder.end());
    }

    private static void line(
        BufferBuilder builder,
        Matrix4f matrix,
        double x1, double y1, double z1,
        double x2, double y2, double z2,
        float r, float g, float b, float a
    ) {
        vertex(builder, matrix, x1, y1, z1, r, g, b, a);
        vertex(builder, matrix, x2, y2, z2, r, g, b, a);
    }

    private static void vertex(
        BufferBuilder builder,
        Matrix4f matrix,
        double x, double y, double z,
        float r, float g, float b, float a
    ) {
        builder.vertex(matrix, (float) x, (float) y, (float) z)
            .color(r, g, b, a)
            .endVertex();
    }
}
