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
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import mekanism.common.registries.MekanismItems;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuildingUtil;

import static org.leodreamer.sftcore.integration.ponder.misc.SFTPonderGroup.INFUSING_FACTORY;
import static org.leodreamer.sftcore.integration.ponder.misc.SFTPonderTag.AE_AUTOMATION_EXAMPLES;

@PonderSceneScanned
@WithTags(AE_AUTOMATION_EXAMPLES)
public class AEAutomationExamplesScenes {

    @PonderScene(groups = INFUSING_FACTORY, file = "ponder_infusion_automation")
    public static void metallurgicInfuser(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("automation_metallurgic_infuser", "The Automation of Metallurgic Infuser");
        var sel = util.select();
        var vec = util.vector();
        var pos = util.grid();

        scene.showBasePlate();

        scene.world().showSection(sel.fromTo(1, 1, 1, 1, 1, 3), Direction.DOWN);
        scene.world().showSection(sel.position(2, 1, 2), Direction.DOWN);

        scene.idle(20);

        scene
                .overlay()
                .showOutlineWithText(sel.position(2, 1, 2), 60)
                .colored(PonderPalette.GREEN)
                .text(
                        "The most challenging part of automating the Metallurgic Infuser is how to get the materials and infusing items into the correct slots")
                .pointAt(vec.of(2.5, 2, 2.5));
        scene.idle(80);
        scene.text(60, "Recalling the subnetwork distribution of Pattern Provider...");
        scene.rotateCameraY(180);
        scene.idle(80);
        scene.world().showSection(sel.position(1, 2, 2), Direction.DOWN);
        scene
                .text(
                        80,
                        "We use an Interface to receive items from the Pattern Provider, forming a subnetwork locally")
                .pointAt(vec.of(1.5, 2.5, 2.5))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(80);

        scene.world().showSection(sel.position(2, 2, 2), Direction.WEST);
        scene.idle(40);
        scene.world().showSection(sel.fromTo(2, 1, 3, 2, 2, 3), Direction.NORTH);

        scene.idle(40);
        scene
                .text(60, "Don't forget to power the subnetwork with Quartz Fiber")
                .pointAt(vec.of(1.2, 0.5, 2.5))
                .placeNearTarget();
        scene
                .overlay()
                .showControls(vec.of(1.2, 0.5, 2.5), Pointing.RIGHT, 60)
                .withItem(AEParts.QUARTZ_FIBER.stack());

        scene.idle(100);

        scene.overlay().showOutline(PonderPalette.GREEN, new Object(), sel.position(2, 2, 2), 80);
        scene
                .overlay()
                .showOutlineWithText(sel.position(2, 1, 3), 80)
                .colored(PonderPalette.GREEN)
                .text(
                        "Two Storage Buses represent different input slots, distributing items within the subnetwork")
                .attachKeyFrame();
        scene.idle(100);

        scene.rotateCameraY(90);
        scene
                .text(
                        80,
                        "Setting the output direction of the Pattern Provider to prevent it from distributing materials directly to the adjacent Metallurgic Infuser")
                .attachKeyFrame();
        scene.idle(100);

        scene
                .overlay()
                .showControls(vec.of(1.5, 1, 2), Pointing.RIGHT, 40)
                .withItem(AEItems.CERTUS_QUARTZ_WRENCH.stack())
                .rightClick();
        scene
                .world()
                .modifyBlock(
                        pos.at(1, 1, 2),
                        (state) -> state.setValue(PatternProviderBlock.PUSH_DIRECTION, PushDirection.UP),
                        false);
        scene.idle(60);
        scene.rotateCameraY(-90);

        scene
                .text(
                        100,
                        "Now, we need to program the Pattern Provider with the correct patterns for automation")
                .attachKeyFrame();
        scene.idle(40);
        scene
                .overlay()
                .showControls(vec.of(2.5, 2, 2.5), Pointing.DOWN, 80)
                .withItem(new ItemStack(Items.IRON_INGOT));
        scene
                .overlay()
                .showControls(vec.of(2, 1, 2.5), Pointing.RIGHT, 80)
                .withItem(MekanismItems.ENRICHED_REDSTONE.getItemStack());
        scene.idle(100);

        scene
                .text(40, "Open the configuration interface of the Metallurgic Infuser...")
                .pointAt(vec.of(2.5, 1.5, 2.5))
                .placeNearTarget();
        scene.idle(40);
        scene
                .overlay()
                .showOutlineWithText(sel.position(2, 2, 2), 60)
                .colored(PonderPalette.GREEN)
                .text("Set the top to input (red)")
                .pointAt(vec.of(2.5, 2, 2.5))
                .placeNearTarget();
        scene.idle(60);
        scene
                .overlay()
                .showOutlineWithText(sel.position(2, 1, 3), 60)
                .colored(PonderPalette.GREEN)
                .text("Set the side to infusing (yellow)")
                .pointAt(vec.of(2, 1, 2.5))
                .placeNearTarget();
        scene.idle(80);

        scene.rotateCameraY(90);

        scene
                .overlay()
                .showOutlineWithText(sel.position(1, 1, 2), 80)
                .colored(PonderPalette.BLUE)
                .text("Set the output to Pattern Provider (blue), remember to enable auto-eject")
                .pointAt(vec.of(1.5, 1.5, 2.5))
                .placeNearTarget();

        scene.idle(100);

        scene
                .text(80, "Encoding Pattern: 8 Iron Ingots + 1 Enriched Redstone = 8 Infused Alloys")
                .pointAt(vec.of(2, 1, 2.5))
                .attachKeyFrame();
        scene
                .overlay()
                .showControls(vec.of(2, 1.5, 2), Pointing.DOWN, 80)
                .withItem(AEItems.BLANK_PATTERN.stack());
        scene.idle(100);

        scene.text(
                80,
                "With that, the automation of the Metallurgic Infuser is complete! Note that for different recipes, you need to mark the Storage Bus filter as needed");
        scene.idle(80);
    }
}
