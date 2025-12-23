package org.leodreamer.sftcore.integration.ponder.scene;

import org.leodreamer.sftcore.integration.ponder.api.SFTSceneBuilder;
import org.leodreamer.sftcore.integration.ponder.api.annotation.PonderScene;
import org.leodreamer.sftcore.integration.ponder.api.annotation.PonderSceneScanned;
import org.leodreamer.sftcore.integration.ponder.api.annotation.WithTags;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import appeng.block.crafting.PatternProviderBlock;
import appeng.block.crafting.PushDirection;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.BlockDefinition;
import com.simibubi.create.AllItems;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuildingUtil;

import static org.leodreamer.sftcore.integration.ponder.misc.SFTPonderGroup.*;
import static org.leodreamer.sftcore.integration.ponder.misc.SFTPonderTag.AE_AUTOMATION;

@PonderSceneScanned
@WithTags(AE_AUTOMATION)
public class AEAutomationScenes {

    @PonderScene(groups = CPU, file = "ponder_me_controller")
    public static void craftingCPU(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("crafting_cpu", "Constructing a Crafting CPU");
        var sel = util.select();
        var vec = util.vector();

        scene.world().showSection(sel.layer(0), Direction.DOWN);
        scene.idle(20);
        scene.text(40, "There are some rules to build a CPU...");
        scene.idle(60);
        scene.world().showSection(sel.position(1, 1, 1), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(sel.fromTo(3, 1, 1, 4, 1, 1), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(sel.fromTo(6, 1, 1, 7, 2, 1), Direction.DOWN);
        scene.idle(10);
        scene.text(40, "The shape of CPU must be a cuboid");
        scene.idle(60);
        scene.rotateCameraY(90);
        scene.idle(20);
        scene.world().showSection(sel.fromTo(1, 1, 3, 2, 2, 4), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(sel.fromTo(4, 1, 3, 5, 1, 4), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(sel.fromTo(7, 1, 3, 8, 2, 4), Direction.DOWN);
        scene.idle(20);
        scene.text(40, "A CPU that is not a cuboid will not appear in a formed state");
        scene.idle(60);
        scene.world().showSection(sel.fromTo(1, 1, 6, 2, 2, 7), Direction.DOWN);
        scene.idle(20);
        scene
            .overlay()
            .showOutlineWithText(sel.fromTo(1, 1, 6, 2, 2, 7), 40)
            .colored(PonderPalette.RED)
            .text(
                "CPU structure must contain at least one Crafting Storage, with total storage equal to the sum of all Crafting Storages"
            )
            .pointAt(vec.of(2, 3, 7));
        scene.idle(100);
        scene.world().showSection(sel.fromTo(4, 1, 6, 5, 2, 7), Direction.DOWN);
        scene.idle(20);
        scene
            .overlay()
            .showOutlineWithText(sel.fromTo(4, 1, 6, 5, 2, 7), 40)
            .colored(PonderPalette.GREEN)
            .text(
                "The rest can be filled with Crafting Units, Crafting Monitors, Crafting Storages, and Crafting Accelerators"
            )
            .pointAt(vec.of(5, 3, 7));
        scene.idle(80);
    }

    @PonderScene(groups = INTERFACE, file = "ponder_ae_interface")
    public static void interfaceBasic(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("interface", "The Interface to Communicate with Outside World");
        var sel = util.select();
        var vec = util.vector();
        var pos = util.grid();

        scene.showBasePlate();
        scene.idle(20);
        scene.world().showSection(sel.fromTo(1, 1, 1, 1, 1, 3), Direction.DOWN);
        scene.world().showSection(sel.fromTo(1, 1, 2, 3, 1, 2), Direction.DOWN);
        scene.idle(20);
        scene
            .overlay()
            .showOutlineWithText(sel.position(3, 1, 2), 60)
            .colored(PonderPalette.GREEN)
            .text("The ME Interface is the interface between the network and the outside world")
            .pointAt(vec.of(3.5, 2, 2.5))
            .placeNearTarget();

        scene.idle(60);
        scene.world().showSection(sel.position(3, 2, 2), Direction.DOWN);
        scene.addKeyframe();
        scene.idle(20);

        var item = AEItems.CERTUS_QUARTZ_CRYSTAL.stack();
        var entity = scene.world().createItemEntity(vec.of(3.5, 5, 2.5), item);

        scene.idle(15);
        scene.world().killEntity(entity);
        scene.idle(10);
        scene
            .text(60, "You can directly input items into the network from the ME Interface")
            .pointAt(vec.of(3.5, 2, 2.5))
            .placeNearTarget();
        scene.idle(70);
        scene.world().hideSection(sel.position(3, 2, 2), Direction.UP);
        scene.idle(20);
        scene
            .overlay()
            .showOutlineWithText(sel.position(3, 1, 2), 40)
            .colored(PonderPalette.GREEN)
            .text("Configuring the slots of the ME Interface")
            .pointAt(vec.of(3.5, 2, 2.5))
            .placeNearTarget();
        scene.idle(40);

        scene.overlay().showControls(vec.of(3.5, 2, 2.5), Pointing.DOWN, 40).withItem(item);
        scene.idle(40);
        scene.world().showSection(sel.position(3, 1, 1), Direction.SOUTH);
        scene.idle(20);
        scene
            .text(60, "It will fill itself with items to the amount you set")
            .pointAt(vec.of(3.5, 2, 2.5))
            .placeNearTarget();
        scene.world().flapFunnel(pos.at(3, 1, 1), true);
        scene.world().createItemEntity(vec.of(3.5, 1.25, 1.5), item);
        scene.idle(60);
    }

    @PonderScene(groups = ANNIHILATION_PLANE, file = "ponder_annihilation_plane")
    public static void annihilationPlane(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("annihilation_plane", "The Usage for Annihilation Plane");
        var sel = util.select();
        var vec = util.vector();
        var pos = util.grid();

        scene.showBasePlate();
        scene.idle(20);
        scene.world().showSection(sel.fromTo(2, 1, 1, 3, 1, 2), Direction.DOWN);
        scene.world().showSection(sel.position(2, 2, 2), Direction.DOWN);
        scene.idle(20);
        scene.text(
            60,
            "If the network can still store items, the Annihilation Plane will collect blocks or item entities in front of it into the network"
        );
        scene.idle(60);
        scene
            .world()
            .setBlocks(
                sel.position(2, 2, 1), AEBlocks.QUARTZ_CLUSTER.block().defaultBlockState(), false
            );
        scene.world().showSection(sel.position(2, 2, 1), Direction.UP);
        scene.idle(20);
        scene.world().showSection(sel.fromTo(1, 1, 2, 0, 1, 2), Direction.DOWN);
        scene.idle(20);
        scene.world().destroyBlock(pos.at(2, 2, 1));
        scene.idle(20);

        var item = AEItems.CERTUS_QUARTZ_CRYSTAL.stack();
        scene.overlay().showControls(vec.of(0.5, 2, 2.5), Pointing.DOWN, 40).withItem(item);
        scene.idle(60);
        var link = scene.world().createItemEntity(vec.of(2.5, 5, 2.5), item);
        scene.idle(10);
        scene.world().killEntity(link);
        scene.idle(20);
        scene.overlay().showControls(vec.of(0.5, 2, 2.5), Pointing.DOWN, 40).withItem(item);
        scene.idle(60);
    }

    @PonderScene(groups = ANNIHILATION_PLANE, file = "ponder_annihilation_plane_filter")
    public static void annihilationPlaneFilter(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title(
            "annihilation_plane_filter", "Break or Collect Special Items with Annihilation Plane"
        );
        var sel = util.select();
        var vec = util.vector();
        var pos = util.grid();

        scene.showBasePlate();
        scene.world().showSection(sel.fromTo(0, 1, 0, 3, 1, 2), Direction.DOWN);
        scene.world().showSection(sel.position(2, 2, 2), Direction.DOWN);
        scene.idle(40);
        scene
            .world()
            .setBlocks(
                sel.position(2, 2, 1), AEBlocks.SMALL_QUARTZ_BUD.block().defaultBlockState(), false
            );
        scene.world().showSection(sel.position(2, 2, 1), Direction.UP);
        scene.idle(20);
        scene.world().destroyBlock(pos.at(2, 2, 1));
        scene.idle(40);
        scene
            .overlay()
            .showOutlineWithText(sel.position(2, 2, 1), 60)
            .colored(PonderPalette.RED)
            .text("You will find that the Annihilation Plane collects all items in front of it...")
            .pointAt(vec.of(2.5, 2.5, 1.5))
            .attachKeyFrame();
        scene.idle(80);
        scene.text(60, "To address this, you can configure your network to only accept certain items");
        scene.idle(80);
        scene.world().hideSection(sel.fromTo(1, 1, 2, 0, 1, 2), Direction.UP);
        scene.idle(20);
        scene.world().showSection(sel.fromTo(4, 1, 2, 5, 1, 2), Direction.DOWN);
        scene.idle(20);
        var point = vec.of(4.75, 1.5, 2.5);
        scene
            .text(60, "Configuring the storage bus filter...")
            .pointAt(point)
            .placeNearTarget()
            .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.GREEN, new Object(), sel.position(4, 1, 2), 60);
        scene.idle(60);
        scene
            .text(50, "Set up the entire production as a sub-network without placing any drives")
            .pointAt(point);
        scene.idle(60);
        var crystal = AEItems.CERTUS_QUARTZ_CRYSTAL.stack();
        scene.overlay().showControls(point, Pointing.DOWN, 40).withItem(crystal);
        scene.idle(40);
        var quartz_list = new BlockDefinition[] {
            AEBlocks.SMALL_QUARTZ_BUD,
            AEBlocks.MEDIUM_QUARTZ_BUD,
            AEBlocks.LARGE_QUARTZ_BUD,
            AEBlocks.QUARTZ_CLUSTER
        };
        for (var block : quartz_list) {
            scene.world().setBlocks(sel.position(2, 2, 1), block.block().defaultBlockState(), false);
            scene.idle(20);
        }
        scene.world().destroyBlock(pos.at(2, 2, 1));
        scene.overlay().showControls(vec.of(5.5, 2, 2.5), Pointing.DOWN, 40).withItem(crystal);
        scene.idle(60);
        scene.text(
            60,
            "It is important to note that blocks without drops will be destroyed by the Annihilation Plane regardless"
        );
        scene.idle(80);
        scene.text(60, "Perhaps you can consider enchanting the Annihilation Plane with Silk Touch");
        scene.idle(60);
    }

    @PonderScene(groups = FORMATION_PLANE, file = "ponder_formation_plane")
    public static void formationPlane(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("formation_plane", "The Usage for Formation Plane");
        var sel = util.select();
        var vec = util.vector();

        var drop = vec.of(3.5, 5, 1.5);
        var loc = vec.of(2.5, 1.5, 1.75);

        scene.showBasePlate();
        scene.idle(20);
        scene.world().showSection(sel.fromTo(0, 1, 0, 6, 1, 6), Direction.DOWN);
        scene.idle(20);
        scene.text(
            60, "Formation Planes can output items from the network as either blocks or item entities"
        );
        scene.idle(80);
        scene.world().showSection(sel.position(3, 2, 1), Direction.DOWN);
        scene.text(60, "When in block mode...").attachKeyFrame();
        scene.idle(80);

        var qb = AEBlocks.QUARTZ_BLOCK;

        var item = scene.world().createItemEntity(drop, qb.stack());
        scene.idle(15);
        scene.world().killEntity(item);
        scene.world().setBlocks(sel.position(2, 1, 1), qb.block().defaultBlockState(), false);
        scene.idle(20);
        scene
            .overlay()
            .showOutlineWithText(sel.position(2, 1, 1), 60)
            .colored(PonderPalette.GREEN)
            .text("Formation Plane will place the block in front of it")
            .pointAt(vec.of(2.5, 1.5, 1.5))
            .placeNearTarget();

        scene.idle(80);
        scene.world().hideSection(sel.position(2, 1, 1), Direction.UP);
        scene.text(60, "When in item entity mode...").attachKeyFrame();
        scene.idle(80);

        item = scene.world().createItemEntity(drop, qb.stack());
        scene.idle(15);
        scene.world().killEntity(item);
        scene.world().createItemEntity(loc, qb.stack());

        scene
            .overlay()
            .showOutlineWithText(sel.position(2, 1, 1), 60)
            .colored(PonderPalette.GREEN)
            .text("Formation Plane will drop the item entity in front of it")
            .pointAt(vec.of(2.5, 1.5, 1.5));
        scene.idle(80);
        scene
            .text(
                60,
                "Configuring the filter options of the Formation Plane allows it to only output items within the filter"
            )
            .attachKeyFrame();
        scene.idle(80);

        var qc = AEItems.CERTUS_QUARTZ_CRYSTAL;

        scene.overlay().showControls(loc, Pointing.RIGHT, 40).withItem(qc.stack());
        scene.idle(20);
        item = scene.world().createItemEntity(drop, qb.stack());
        scene.idle(15);
        scene.world().killEntity(item);

        scene.idle(40);

        item = scene.world().createItemEntity(drop, qc.stack());
        scene.idle(15);
        scene.world().killEntity(item);
        scene.world().createItemEntity(loc, qc.stack());
    }

    @PonderScene(groups = MOLECULAR_ASSEMBLER, file = "ponder_molecular_assembler")
    public static void molecularAssembler(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("molecular_assembler", "Molecular Assembling");
        var sel = util.select();
        var vec = util.vector();

        scene.showBasePlate();
        scene.idle(20);

        scene.world().showSection(sel.fromTo(0, 1, 0, 3, 1, 0), Direction.DOWN);
        scene.world().showSection(sel.fromTo(2, 1, 1, 2, 2, 2), Direction.DOWN);
        scene.world().showSection(sel.fromTo(1, 1, 1, 1, 1, 1), Direction.DOWN);
        scene.text(60, "To use the Molecular Assembler...");
        scene.idle(80);
        scene.world().showSection(sel.fromTo(1, 1, 2, 1, 1, 2), Direction.DOWN);
        scene
            .text(60, "You need to place it together with the Pattern Provider")
            .pointAt(vec.of(1.5, 2, 2.5))
            .attachKeyFrame();
        scene.idle(80);
        scene.text(
            60,
            "Molecular Assembler can automate crafting, stonecutting, and smithing table recipes, sending the products back to the Pattern Provider"
        );
        scene.idle(80);
        scene.text(60, "Both the Pattern Provider and Molecular Assembler can transfer the network...");
        scene.idle(80);
        scene.world().showSection(sel.fromTo(0, 1, 1, 1, 2, 2), Direction.DOWN);
        scene.text(60, "So you can build a structure like this");
        scene.idle(80);
        scene.text(
            60,
            "Later on, you can replace the Molecular Assembler with the more efficient Assembler Matrix"
        );
        scene.idle(80);
    }

    @PonderScene(
        groups = { PATTERN, PATTERN_PROVIDER },
        file = "ponder_pattern_provider"
    )
    public static void craftingPrinciple(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("crafting_principle", "Crafting Principles");
        var sel = util.select();
        var vec = util.vector();
        var pos = util.grid();

        scene.world().showSection(sel.fromTo(0, 0, 0, 7, 0, 7), Direction.UP);
        scene.showBasePlate();
        scene.idle(10);
        scene.world().showSection(sel.fromTo(4, 1, 0, 4, 1, 2), Direction.UP);
        scene.idle(10);
        scene.world().showSection(sel.fromTo(1, 1, 3, 4, 1, 3), Direction.UP);
        scene.idle(20);
        scene.world().showSection(sel.position(2, 2, 3), Direction.UP);
        scene.idle(10);
        scene.world().showSection(sel.fromTo(4, 2, 1, 4, 3, 3), Direction.UP);
        scene.idle(20);
        scene.text(40, "This is a Pattern Provider").pointAt(vec.of(2.5, 2.5, 3.5)).attachKeyFrame();
        scene.idle(60);
        scene
            .overlay()
            .showControls(vec.of(2.5, 3, 3.5), Pointing.DOWN, 20)
            .rightClick()
            .withItem(AEItems.CERTUS_QUARTZ_WRENCH.stack());
        scene.idle(20);
        scene
            .world()
            .modifyBlock(
                pos.at(2, 2, 3),
                (state) -> state.setValue(PatternProviderBlock.PUSH_DIRECTION, PushDirection.DOWN),
                false
            );
        scene.idle(20);
        scene
            .text(40, "Click the Pattern Provider with a Wrench to set its direction")
            .pointAt(vec.of(2.5, 3, 3.5))
            .attachKeyFrame();
        scene.idle(60);
        scene.text(40, "When the network sends a crafting request...").attachKeyFrame();
        scene.idle(20);
        scene
            .world()
            .createItemOnBelt(pos.at(2, 1, 3), Direction.DOWN, new ItemStack(Items.IRON_INGOT));
        scene.idle(30);

        var process = pos.at(4, 1, 3);
        scene.world().removeItemsFromBelt(process);
        scene.world().createItemOnBelt(process, Direction.DOWN, AllItems.IRON_SHEET.asStack());
        scene.idle(20);
        scene.world().removeItemsFromBelt(process);
        scene
            .world()
            .createItemEntity(vec.of(5, 2, 3.5), vec.of(0.2, 0.25, 0), AllItems.IRON_SHEET.asStack());
        scene.idle(20);
        scene
            .text(40, "The Pattern Provider outputs the ingredients to adjacent inventories")
            .pointAt(vec.of(2.5, 1.5, 3.5));
        scene.overlay().showOutline(PonderPalette.GREEN, new Object(), sel.position(2, 1, 3), 40);
        scene.idle(80);
        scene.addKeyframe();
        scene
            .text(40, "To complete a craft, the items need to be returned to the network")
            .pointAt(vec.of(2.5, 2.5, 3.5));
        scene.idle(60);
        scene.rotateCameraY(-180);
        scene.world().showSection(sel.fromTo(5, 1, 3, 5, 1, 6), Direction.DOWN);
        scene.world().showSection(sel.fromTo(6, 1, 4, 0, 1, 7), Direction.DOWN);
        scene.world().showSection(sel.position(2, 2, 4), Direction.DOWN);
        scene.idle(20);
        scene.text(
            120,
            "Thus you need to build a return system, or you can also use other Interfaces or Providers to return items to the network"
        );
        scene.idle(120);
    }

    @PonderScene(
        groups = { PATTERN_PROVIDER, CPU, MOLECULAR_ASSEMBLER },
        file = "ponder_ae_crafting_system"
    )
    public static void craftingSystem(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("ae_crafting_system", "The Crafting System in AE2");
        var sel = util.select();
        var vec = util.vector();

        scene.showBasePlate();
        scene.idle(20);
        scene.world().showSection(sel.fromTo(2, 1, 1, 2, 1, 3), Direction.DOWN);
        scene.idle(20);
        scene.text(40, "To set up an automated crafting system...");
        scene.idle(60);
        scene.world().showSection(sel.fromTo(0, 1, 1, 1, 1, 3), Direction.DOWN);
        scene.idle(20);
        scene.text(40, "You need a Crafting CPU multiblock").pointAt(vec.of(0.5, 2, 2.5));
        scene
            .overlay()
            .showOutline(PonderPalette.GREEN, new Object(), sel.fromTo(0, 1, 1, 0, 1, 3), 40);
        scene.idle(60);
        scene.world().showSection(sel.fromTo(4, 1, 2, 3, 1, 0), Direction.DOWN);
        scene.idle(20);
        scene
            .text(40, "as well as a Pattern Provider and an Output Destination")
            .pointAt(vec.of(4.5, 2, 1));
        scene
            .overlay()
            .showOutline(PonderPalette.GREEN, new Object(), sel.fromTo(4, 1, 0, 4, 1, 1), 40);
        scene.idle(80);
        scene
            .text(
                80,
                "By writing patterns with the Pattern Encoding Terminal and placing them into the Pattern Provider, you can start ordering crafts"
            )
            .pointAt(vec.of(4, 2.5, 1))
            .attachKeyFrame();
        scene
            .overlay()
            .showControls(vec.of(4, 2.5, 1), Pointing.DOWN, 40)
            .rightClick()
            .withItem(AEItems.BLANK_PATTERN.stack());
        scene.idle(100);
    }

    @PonderScene(
        groups = { PATTERN, PATTERN_PROVIDER },
        file = "ponder_ae_crafting_parallel"
    )
    public static void craftingParallel(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("crafting_parallel", "Crafting Parallel");
        var sel = util.select();
        var vec = util.vector();
        var pos = util.grid();
        var memoryCard = AEItems.MEMORY_CARD.stack();

        scene.world().showSection(sel.fromTo(1, 0, 0, 8, 0, 8), Direction.UP);
        scene.idle(20);
        scene.world().showSection(sel.fromTo(1, 1, 0, 1, 1, 3), Direction.DOWN);
        scene.idle(10);
        for (int index = 0; index < 4; index++) {
            scene.world().showSection(sel.position(0, index, 4), Direction.DOWN);
            scene.idle(5);
        }

        scene.world().showSection(sel.fromTo(1, 1, 4, 1, 1, 5), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(sel.position(1, 3, 4), Direction.DOWN);
        scene.idle(40);
        scene
            .text(
                60,
                "Multiple Pattern Providers executing the same pattern can achieve parallelism across multiple machines"
            )
            .attachKeyFrame();
        scene.idle(80);
        scene.text(
            60,
            "Memory Cards can copy machine configurations and apply them to other machines, and Pattern Providers are no exception."
        );
        scene.idle(80);
        scene.text(60, "Use the Memory Card to right-click the Pattern Provider while sneaking...");
        scene.idle(20);
        scene
            .overlay()
            .showControls(vec.of(1.5, 2, 3.5), Pointing.DOWN, 40)
            .whileSneaking()
            .withItem(memoryCard);
        scene.idle(60);
        scene.text(60, "The Pattern Provider will store the pattern information into the Memory Card");
        scene.idle(60);
        scene.world().showSection(sel.fromTo(2, 1, 3, 2, 1, 5), Direction.DOWN);
        scene.idle(20);
        scene.world().showSection(sel.position(2, 3, 4), Direction.DOWN);
        scene
            .text(60, "Then, use the Memory Card to right-click the target Pattern Provider...")
            .attachKeyFrame();
        scene.idle(40);
        scene.overlay().showControls(vec.of(2.5, 2, 3.5), Pointing.DOWN, 40).withItem(memoryCard);
        scene.idle(20);
        scene
            .world()
            .modifyBlock(
                pos.at(2, 1, 3),
                (state) -> state.setValue(PatternProviderBlock.PUSH_DIRECTION, PushDirection.SOUTH),
                false
            );
        scene.idle(40);
        scene
            .text(
                40,
                "The Memory Card will consume a Blank Pattern from your inventory and copy the pattern into the target Pattern Provider"
            )
            .attachKeyFrame();
        scene.idle(60);
        scene.text(80, "Repeat the process to build parallelism");
        scene.idle(20);
        for (int index = 1; index <= 5; index++) {
            scene.world().showSection(sel.fromTo(2 + index, 1, 3, 2 + index, 1, 5), Direction.DOWN);
            scene.world().showSection(sel.position(2 + index, 3, 4), Direction.DOWN);
            scene.idle(5);
        }
        scene.idle(10);

        scene.world().showSection(sel.fromTo(2, 1, 0, 7, 1, 1), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(sel.position(0, 1, 5), Direction.DOWN);
        scene.world().showSection(sel.fromTo(0, 1, 6, 8, 1, 6), Direction.DOWN);
        scene.world().showSection(sel.fromTo(8, 1, 0, 8, 1, 6), Direction.DOWN);
        scene.world().showSection(sel.fromTo(6, 2, 6, 8, 2, 8), Direction.DOWN);

        scene.idle(10);
        scene.rotateCameraY(90);
        scene.idle(40);
        scene.rotateCameraY(90);
        scene.idle(40);
    }

    @PonderScene(
        groups = { PATTERN_PROVIDER, INTERFACE },
        file = "ponder_pattern_provider_interaction"
    )
    public static void providerSubnetwork(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("pattern_provider_interaction", "Subnetwork Assigning");
        var sel = util.select();
        var vec = util.vector();

        scene.showBasePlate();
        scene.idle(20);
        scene.world().showSection(sel.fromTo(1, 1, 0, 4, 1, 1), Direction.DOWN);
        scene.idle(20);
        scene.rotateCameraY(90);
        scene.idle(20);
        scene
            .overlay()
            .showOutlineWithText(sel.fromTo(1, 1, 1, 2, 1, 1), 40)
            .colored(PonderPalette.GREEN)
            .text(
                "ME Interface is also an inventory, when the Pattern Provider is adjacent to an ME Interface..."
            )
            .pointAt(vec.of(2, 1.5, 1.5));
        scene.idle(60);

        var crystal = AEItems.CERTUS_QUARTZ_CRYSTAL.stack();
        scene.overlay().showControls(vec.of(1.5, 1.5, 1.5), Pointing.DOWN, 40).withItem(crystal);
        scene.idle(40);
        scene.world().createItemEntity(vec.of(4.5, 1.5, 1.5), crystal);
        scene.idle(20);
        scene.text(
            60,
            "The Pattern Provider will directly input the ingredients into the network where the ME Interface is located"
        );
        scene.idle(80);
        scene.text(
            80,
            "This is important for some complex machine mechanisms, you can refer to the Metallurgic Infuser's ponder information."
        );
        scene.idle(80);
    }
}
