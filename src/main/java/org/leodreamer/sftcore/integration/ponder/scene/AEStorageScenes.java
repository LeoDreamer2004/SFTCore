package org.leodreamer.sftcore.integration.ponder.scene;

import appeng.core.definitions.AEItems;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.leodreamer.sftcore.integration.ponder.api.SFTSceneBuilder;

public class AEStorageScenes {

    public static void portBuses(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("import_export_bus", "The Import and Export of Storage");
        var sel = util.select();
        var vec = util.vector();
        var pos = util.grid();

        scene.showBasePlate();
        scene.idle(20);
        scene.world().showSection(sel.position(4, 1, 3), Direction.DOWN);
        scene.world().showSection(sel.fromTo(4, 1, 4, 2, 1, 4), Direction.DOWN);
        scene.idle(20);
        scene.text(60, "The Import and Export Buses allow direct insertion and extraction from the network");
        scene.idle(80);
        scene.world().showSection(sel.fromTo(2, 1, 2, 1, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(sel.fromTo(1, 1, 3, 1, 1, 4), Direction.DOWN);
        scene.rotateCameraY(-90);
        scene.idle(40);
        scene.overlay().showOutlineWithText(sel.position(1, 1, 3), 60)
                .colored(PonderPalette.BLUE)
                .text("The Export Bus extracts items from the network into a target inventory")
                .pointAt(vec.of(1.5, 1.5, 3.5))
                .attachKeyFrame();
        scene.idle(80);
        scene.text(80, "Unlike the Import Bus, the Export Bus requires a filter to specify which items to extract");
        scene.idle(80);

        var crystal = AEItems.CERTUS_QUARTZ_CRYSTAL.stack();
        scene.overlay().showControls(vec.of(1.5, 1.5, 3.25), Pointing.DOWN, 40)
                .withItem(crystal);
        scene.idle(40);
        scene.rotateCameraY(90);
        scene.idle(30);
        scene.world().showSection(sel.position(4, 2, 3), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(sel.position(1, 1, 1), Direction.NORTH);
        scene.idle(30);
        var item = scene.world().createItemEntity(vec.of(4.5, 5, 3.5), crystal);
        scene.idle(10);
        scene.world().killEntity(item);

        scene.idle(10);
        scene.world().flapFunnel(pos.at(1, 1, 1), true);
        item = scene.world().createItemEntity(vec.of(1.5, 1.25, 1.5), crystal);
        scene.idle(80);
        scene.world().killEntity(item);
        scene.world().hideSection(sel.position(1, 1, 1), Direction.UP);
        scene.world().hideSection(sel.position(4, 2, 3), Direction.UP);
        scene.world().hideSection(sel.fromTo(1, 1, 3, 1, 1, 4), Direction.UP);

        scene.idle(40);
        scene.world().showSection(sel.position(2, 1, 3), Direction.DOWN);
        scene.idle(40);
        scene.rotateCameraY(-90);
        scene.overlay().showOutlineWithText(sel.position(2, 1, 3), 60)
                .colored(PonderPalette.GREEN)
                .text("The Import Bus inserts items from a target inventory into the network")
                .pointAt(vec.of(2.5, 1.5, 3.5))
                .attachKeyFrame();
        scene.idle(80);
        scene.text(60, "The Import Bus can be configured with filters...");
        scene.idle(60);
        scene.overlay().showControls(vec.of(2.5, 1.5, 3.25), Pointing.DOWN, 40)
                .withItem(crystal);
        scene.idle(40);
        scene.world().showSection(sel.position(4, 1, 2), Direction.NORTH);
        scene.idle(10);
        scene.world().showSection(sel.position(2, 2, 2), Direction.DOWN);
        scene.rotateCameraY(90);
        scene.idle(40);
        item = scene.world().createItemEntity(vec.of(2.5, 5, 2.5), crystal);
        scene.idle(10);
        scene.world().killEntity(item);
        scene.idle(10);
        scene.world().flapFunnel(pos.at(4, 1, 2), true);
        scene.world().createItemEntity(vec.of(4.5, 1.25, 2.5), crystal);
        scene.idle(20);
    }

    public static void ioPort(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("io_port", "使用IO端口整理存储");
        var sel = util.select();
        var vec = util.vector();

        scene.world().showSection(sel.fromTo(0, 0, 0, 9, 0, 9), Direction.UP);
        scene.idle(20);
        scene.world().showSection(sel.fromTo(3, 1, 4, 5, 1, 4), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutlineWithText(sel.position(4, 1, 4), 80)
                .colored(PonderPalette.GREEN)
                .text("IO端口能够将其中的存储元件内的东西导入到网络，或者将网络内的存储内容导入元件")
                .pointAt(vec.of(4.5, 2, 4.5));
        scene.idle(100);
        scene.world().showSection(sel.fromTo(0, 1, 0, 4, 1, 3), Direction.DOWN);
        scene.text(60, "你可以使用它将网络内的物品导出到存储元件")
                .attachKeyFrame();
        scene.idle(60);

        scene.world().hideSection(sel.fromTo(0, 1, 0, 4, 1, 3), Direction.UP);
        scene.idle(20);
        scene.rotateCameraY(90);
        scene.idle(20);
        scene.world().showSection(sel.fromTo(4, 1, 5, 6, 1, 8), Direction.DOWN);
        scene.idle(20);
        scene.text(60, "你还可以用IO端口来整理驱动器中存储元件的内容")
                .attachKeyFrame();
        scene.idle(60);
    }

    public static void ioPortOutput(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("io_port_output", "使用IO端口输出大量物品");
        var sel = util.select();
        var vec = util.vector();

        scene.showBasePlate();
        scene.idle(20);
        scene.world().showSection(sel.fromTo(1, 1, 4, 3, 1, 4), Direction.DOWN);
        scene.idle(20);
        scene.text(60, "IO端口的输入与输出速度非常的快……");
        scene.rotateCameraY(180);
        scene.idle(80);
        scene.world().showSection(sel.fromTo(2, 1, 3, 2, 1, 2), Direction.DOWN);
        scene.text(100, "因此如果有无限元件，你可以使用它来快速生产物质球和奇点");
        scene.idle(20);

        scene.overlay().showControls(vec.of(2.5, 2, 4.5), Pointing.DOWN, 40)
                .withItem(new ItemStack(EPPItemAndBlock.INFINITY_CELL.asItem()));
        scene.idle(60);
        scene.overlay().showControls(vec.of(2.5, 2, 2.5), Pointing.DOWN, 40)
                .withItem(AEItems.SINGULARITY.stack());

        scene.idle(40);
    }

    public static void storageBus(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("storage_bus", "The Use of Storage Bus");
        var sel = util.select();
        var vec = util.vector();

        scene.showBasePlate();
        scene.idle(20);
        scene.world().showSection(sel.fromTo(1, 1, 1, 1, 1, 3), Direction.DOWN);
        scene.text(60, "The Storage Bus allows external storage to be accessed by the AE Network");
        scene.idle(80);
        scene.world().showSection(sel.fromTo(2, 1, 2, 4, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(sel.fromTo(4, 1, 3, 4, 2, 3), Direction.DOWN);
        scene.addKeyframe();
        scene.idle(10);
        scene.rotateCameraY(-180);
        scene.idle(40);
        var crystal = AEItems.CERTUS_QUARTZ_CRYSTAL.stack();
        var item = scene.world().createItemEntity(vec.of(4.5, 5, 3.5), crystal);
        scene.idle(15);
        scene.world().killEntity(item);
        scene.idle(40);
        scene.overlay().showControls(vec.of(2, 1.5, 2.75), Pointing.RIGHT, 40)
                .withItem(crystal);
    }

    public static void subnetwork(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("storage_subnetwork", "Build a Recursive Subnetwork");
        var sel = util.select();
        var vec = util.vector();

        scene.showBasePlate();
        scene.idle(20);
        scene.world().showSection(sel.fromTo(1, 1, 1, 1, 1, 3), Direction.DOWN);
        scene.idle(10);

        scene.world().showSection(sel.fromTo(5, 1, 1, 5, 1, 4), Direction.DOWN);
        scene.text(60, "When you want one network to access the contents of another...")
                .attachKeyFrame();
        scene.idle(60);
        scene.idle(30);
        scene.world().showSection(sel.fromTo(2, 1, 2, 3, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(sel.position(4, 1, 2), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutlineWithText(sel.fromTo(3, 1, 2, 4, 1, 2), 60)
                .colored(PonderPalette.BLUE)
                .text("Connect the Storage Bus to an ME Interface")
                .pointAt(vec.of(4, 1.5, 2.5))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(80);
        scene.overlay().showOutlineWithText(sel.position(3, 1, 2), 60)
                .colored(PonderPalette.GREEN)
                .text("The network on the end of the Storage Bus (the main network)...")
                .pointAt(vec.of(3.5, 1.5, 2.5))
                .placeNearTarget();
        scene.idle(80);
        scene.overlay().showOutlineWithText(sel.position(4, 1, 2), 60)
                .colored(PonderPalette.RED)
                .text("...can access the network on the other end of the ME Interface (the subnetwork)")
                .pointAt(vec.of(4.5, 1.5, 2.5))
                .placeNearTarget();
        scene.idle(80);
        scene.text(40, "While the opposite is not true")
                .pointAt(vec.of(4.5, 1.5, 2.5))
                .placeNearTarget();
        scene.idle(60);
        scene.text(100, "By configuring the Storage Bus, you can set the main network to have read-only, write-only, or read-write access to the subnetwork, as well as apply filters for reading and writing.")
                .pointAt(vec.of(3, 2.5, 2))
                .attachKeyFrame();
        scene.overlay().showControls(vec.of(3.5, 2.5, 2), Pointing.DOWN, 100)
                .rightClick();
        scene.idle(120);
        scene.addKeyframe();
        scene.rotateCameraY(90);
        scene.idle(30);
        scene.world().showSection(sel.position(5, 2, 4), Direction.DOWN);
        scene.idle(20);
        var crystal = AEItems.CERTUS_QUARTZ_CRYSTAL.stack();
        var item = scene.world().createItemEntity(vec.of(5.5, 5, 4.5), crystal);
        scene.idle(15);
        scene.world().killEntity(item);
        scene.idle(20);
        scene.overlay().showControls(vec.of(1.5, 2, 1.5), Pointing.DOWN, 40)
                .withItem(crystal);
        scene.idle(20);
    }
}
