package org.leodreamer.sftcore.integration.ponder.scene;

import appeng.block.networking.ControllerBlock;
import appeng.block.qnb.QuantumBaseBlock;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import org.leodreamer.sftcore.integration.ponder.api.SFTSceneBuilder;
import org.leodreamer.sftcore.integration.ponder.api.annotation.PonderScene;
import org.leodreamer.sftcore.integration.ponder.api.annotation.PonderSceneScanned;
import org.leodreamer.sftcore.integration.ponder.api.annotation.WithTags;

import static org.leodreamer.sftcore.integration.ponder.misc.SFTPonderGroup.CONTROLLER;
import static org.leodreamer.sftcore.integration.ponder.misc.SFTPonderGroup.QUANTUM_BRIDGE;
import static org.leodreamer.sftcore.integration.ponder.misc.SFTPonderTag.AE_NETWORK;

@PonderSceneScanned
@WithTags(AE_NETWORK)
public class AENetworkScenes {

    @PonderScene(groups = CONTROLLER, file = "ponder_me_controller")
    public static void controller(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("me_controller_scene", "The Placement of ME Controller");
        var sel = util.select();
        var vec = util.vector();
        var pos = util.grid();

        scene.configureBasePlate(0, 0, 9);
        scene.world().showSection(sel.fromTo(0, 0, 0, 9, 0, 9), Direction.UP);
        scene.world().setBlocks(sel.position(8, 1, 1),
                AEBlocks.CONTROLLER.block().defaultBlockState());
        scene.idle(20);
        scene.world().showSection(sel.fromTo(0, 1, 1, 1, 1, 1), Direction.DOWN);
        scene.idle(20);
        scene.text(40, "This is a Controller...");
        scene.idle(60);

        scene.overlay().showOutlineWithText(sel.position(0, 1, 1), 60)
                .colored(PonderPalette.GREEN)
                .text("Controller needs to be powered to function")
                .pointAt(vec.of(0.5, 1.5, 1.5))
                .attachKeyFrame();

        scene.idle(80);
        for (int index = 0; index < 6; index++) {
            scene.world().showSection(sel.position(index + 2, 1, 1), Direction.WEST);
            scene.world().showSection(sel.position(1, 2 + index, 1), Direction.DOWN);
            scene.world().showSection(sel.position(1, 1, 2 + index), Direction.NORTH);
            scene.idle(5);
        }
        scene.idle(10);
        scene.text(60, "A Controller can extend up to 7 blocks in each direction").attachKeyFrame();
        scene.idle(60);
        scene.idle(30);
        scene.world().showSection(sel.position(8, 1, 1), Direction.DOWN);
        scene.idle(20);
        scene.world().modifyBlocks(
                sel.fromTo(8, 1, 1, 1, 1, 1),
                (state) -> state.setValue(ControllerBlock.CONTROLLER_STATE,
                        ControllerBlock.ControllerBlockState.conflicted)
        );
        scene.world().modifyBlocks(
                sel.fromTo(1, 8, 1, 1, 1, 1),
                (state) -> state.setValue(ControllerBlock.CONTROLLER_STATE,
                        ControllerBlock.ControllerBlockState.conflicted)
        );
        scene.world().modifyBlocks(
                sel.fromTo(1, 1, 8, 1, 1, 1),
                (state) -> state.setValue(ControllerBlock.CONTROLLER_STATE,
                        ControllerBlock.ControllerBlockState.conflicted)
        );
        scene.world().modifyBlock(
                pos.at(7, 1, 1),
                (state) -> state.setValue(ControllerBlock.CONTROLLER_TYPE,
                        ControllerBlock.ControllerRenderType.column_x)
        );
        scene.text(60, "Controllers will turn red and become unusable if they exceed the 7-block limit");
        scene.rotateCameraY(180);
        scene.idle(80);
        scene.world().showSection(sel.fromTo(3, 1, 3, 5, 1, 4), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutlineWithText(sel.fromTo(3, 1, 3, 5, 1, 4), 60)
                .colored(PonderPalette.RED)
                .text("A network may only have one connected ME Controller, which means you cannot link two different controllers with Cables")
                .attachKeyFrame();
        scene.idle(80);
        scene.world().showSection(sel.fromTo(3, 1, 5, 8, 3, 7), Direction.DOWN);
        scene.idle(20);
        scene.text(60, "Controllers cannot be placed in a cross shape").attachKeyFrame();
        scene.idle(80);
    }

    @PonderScene(groups = QUANTUM_BRIDGE, file = "ponder_quantum_network_bridge")
    public static void quantumBridge(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("quantum_network_bridge", "Use Quantum Bridge for Remote Network Transmission");
        var sel = util.select();
        var vec = util.vector();
        var pos = util.grid();

        scene.world().showSection(sel.fromTo(0, 0, 0, 9, 0, 9), Direction.UP);
        scene.idle(20);
        scene.text(100, "Quantum Bridge can be used for remote network transmission, even across dimensions. A pair of Quantum Rings can transmit up to 32 channels.");
        scene.idle(120);
        scene.text(60, "To build a Quantum Ring structure, you need eight Quantum Rings and one Quantum Link")
                .attachKeyFrame();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                scene.world().modifyBlock(
                        pos.at(i, j + 1, 0),
                        (state) -> state.setValue(QuantumBaseBlock.FORMED, false)
                );
                if (i != 1 || j != 1) {
                    scene.world().showSection(sel.position(i, j + 1, 0), Direction.DOWN);
                    scene.idle(5);
                }
            }
        }
        scene.idle(10);
        scene.world().showSection(sel.position(1, 2, 0), Direction.SOUTH);
        scene.idle(20);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == 1 || j == 1)
                    scene.world().modifyBlock(
                            pos.at(i, j + 1, 0),
                            (state) -> state.setValue(QuantumBaseBlock.FORMED, true)
                    );
            }
        }
        scene.idle(30);
        scene.rotateCameraY(180);
        scene.idle(30);

        scene.world().showSection(sel.fromTo(0, 1, 1, 3, 1, 3), Direction.DOWN);
        scene.overlay().showOutline(PonderPalette.GREEN, new Object(),
                sel.position(1, 1, 0), 60);
        scene.overlay().showOutline(PonderPalette.GREEN, new Object(),
                sel.position(0, 2, 0), 60);
        scene.overlay().showOutline(PonderPalette.GREEN, new Object(),
                sel.position(1, 3, 0), 60);
        scene.overlay().showOutline(PonderPalette.GREEN, new Object(),
                sel.position(2, 2, 0), 60);
        scene.text(60, "Network can only be connected to the Quantum Ring through the four Quantum Ring blocks surrounding the Quantum Link")
                .pointAt(vec.of(1.5, 2.5, 1))
                .attachKeyFrame();
        scene.idle(80);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i != 1 && j != 1)
                    scene.world().modifyBlock(
                            pos.at(i + 6, j + 1, 8),
                            (state) -> state.setValue(QuantumBaseBlock.FORMED, true)
                    );
                scene.world().showSection(sel.position(i + 6, j + 1, 8), Direction.DOWN);
                scene.idle(5);
            }
        }
        scene.text(60, "The Quantum Rings are used in pairs, you need to place another Quantum Ring structure at the remote end where you need it")
                .attachKeyFrame();
        scene.idle(80);
        scene.text(60, "To activate the Quantum Rings, you need to insert a Quantum Entangled Singularity into each of the two Quantum Rings");

        var singularity = AEItems.QUANTUM_ENTANGLED_SINGULARITY.stack();
        scene.overlay().showControls(vec.of(7.5, 2.5, 8.5), Pointing.LEFT, 40)
                .withItem(singularity);
        scene.idle(40);
        scene.rotateCameraY(180);
        scene.idle(30);
        scene.overlay().showControls(vec.of(1.5, 2.5, 0), Pointing.LEFT, 40)
                .withItem(singularity);
        scene.idle(60);
        scene.world().showSection(sel.fromTo(7, 1, 7, 4, 1, 5), Direction.DOWN);
        scene.idle(20);
        scene.world().showSection(sel.position(3, 2, 2), Direction.DOWN);
        scene.idle(20);
        scene.rotateCameraY(90);
        scene.idle(30);

        var crystal = AEItems.CERTUS_QUARTZ_CRYSTAL.stack();
        var item = scene.world().createItemEntity(vec.of(3.5, 5, 2.5), crystal);
        scene.idle(10);
        scene.world().killEntity(item);
        scene.idle(20);
        scene.overlay().showControls(vec.of(5.5, 1.5, 6), Pointing.DOWN, 40)
                .withItem(crystal);
        scene.idle(60);
        scene.text(60, "Quantum Bridge transmits data at a fixed cost of 400 AE/t, regardless of distance");
        scene.idle(60);
        scene.text(60, "In the same dimension, a more economical solution is the ME Wireless Connector");
        scene.idle(60);
    }
}
