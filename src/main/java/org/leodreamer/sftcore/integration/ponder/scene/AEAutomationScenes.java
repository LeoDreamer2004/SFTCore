package org.leodreamer.sftcore.integration.ponder.scene;

import appeng.core.definitions.AEItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;

public class AEAutomationScenes {
    public static void craftingCPU(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("crafting_cpu", "Constructing a Crafting CPU");
        scene.world().showSection(util.select().layer(0), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(40).text("There are some rules to build a CPU...");
        scene.idle(60);
        scene.world().showSection(util.select().position(1, 1, 1), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(3, 1, 1, 4, 1, 1), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(6, 1, 1, 7, 2, 1), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(40).text("The shape of CPU must be a cuboid");
        scene.idle(60);
        scene.rotateCameraY(90);
        scene.idle(20);
        scene.world().showSection(util.select().fromTo(1, 1, 3, 2, 2, 4), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(4, 1, 3, 5, 1, 4), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().fromTo(7, 1, 3, 8, 2, 4), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showText(40).text("A CPU that is not a cuboid will not appear in a formed state");
        scene.idle(60);
        scene.world().showSection(util.select().fromTo(1, 1, 6, 2, 2, 7), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutlineWithText(util.select().fromTo(1, 1, 6, 2, 2, 7), 40)
                .colored(PonderPalette.RED)
                .text("CPU structure must contain at least one Crafting Storage, with total storage equal to the sum of all Crafting Storages")
                .pointAt(util.vector().of(2, 3, 7));
        scene.idle(100);
        scene.world().showSection(util.select().fromTo(4, 1, 6, 5, 2, 7), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutlineWithText(util.select().fromTo(4, 1, 6, 5, 2, 7), 40)
                .colored(PonderPalette.GREEN)
                .text("The rest can be filled with Crafting Units, Crafting Monitors, Crafting Storages, and Crafting Accelerators")
                .pointAt(util.vector().of(5, 3, 7));
        scene.idle(80);
    }

    public static void interfaceBasic(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = new CreateSceneBuilder(builder);

        scene.title("interface", "The Interface to Communicate with Outside World");
        scene.showBasePlate();
        scene.idle(20);
        // scene.world.modifyBlockEntityNBT([2, 1, 2], (nbt) => {
        //         nbt.cable = {
        //                 id: "ae2:fluix_covered_cable",
        //         visual: {
        //     connections: ["west", "east"],
        // },
        //   };
        // });
        scene.world().showSection(util.select().fromTo(1, 1, 1, 1, 1, 3), Direction.DOWN);
        scene.world().showSection(util.select().fromTo(1, 1, 2, 3, 1, 2), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutlineWithText(util.select().position(3, 1, 2), 60)
                .colored(PonderPalette.GREEN)
                .text("The ME Interface is the interface between the network and the outside world")
                .pointAt(util.vector().of(3.5, 2, 2.5))
                .placeNearTarget();

        scene.idle(60);
        scene.world().showSection(util.select().position(3, 2, 2), Direction.DOWN);
        scene.addKeyframe();
        scene.idle(20);

        var item = AEItems.CERTUS_QUARTZ_CRYSTAL.stack();
        var entity = scene.world().createItemEntity(
                util.vector().of(3.5, 5, 2.5),
                util.vector().of(0, 0, 0),
                item
        );

        scene.idle(15);
        scene.world().modifyEntity(entity, Entity::kill);
        scene.idle(10);
        scene.overlay().showText(60)
                .text("You can directly input items into the network from the ME Interface")
                .pointAt(util.vector().of(3.5, 2, 2.5))
                .placeNearTarget();
        scene.idle(70);
        scene.world().hideSection(util.select().position(3, 2, 2), Direction.UP);
        scene.idle(20);
        scene.overlay().showOutlineWithText(util.select().position(3, 1, 2), 40)
                .colored(PonderPalette.GREEN)
                .text("Configuring the slots of the ME Interface")
                .pointAt(util.vector().of(3.5, 2, 2.5))
                .placeNearTarget();
        scene.idle(40);

        scene.overlay()
                .showControls(util.vector().of(3.5, 2, 2.5), Pointing.DOWN, 40)
                .withItem(item);
        scene.idle(40);
        scene.world().showSection(util.select().position(3, 1, 1), Direction.SOUTH);
        scene.idle(20);
        scene.overlay().showText(60)
                .text("It will fill itself with items to the amount you set")
                .pointAt(util.vector().of(3.5, 2, 2.5))
                .placeNearTarget();
        scene.world().flapFunnel(new BlockPos(3, 1, 1), true);
        scene.world().createItemEntity(
                util.vector().of(3.5, 1.25, 1.5),
                util.vector().of(0, 0, 0),
                item
        );
        scene.idle(60);
    }
}
