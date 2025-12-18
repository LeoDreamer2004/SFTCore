package org.leodreamer.sftcore.integration.ponder.scene;

import mekanism.common.block.attribute.Attribute;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismItems;
import mekanism.generators.common.block.attribute.AttributeStateFissionPortMode;
import mekanism.generators.common.registries.GeneratorsBlocks;
import mekanism.generators.common.registries.GeneratorsItems;
import mekanism.generators.common.tile.turbine.TileEntityTurbineRotor;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.leodreamer.sftcore.integration.ponder.api.SFTSceneBuilder;
import org.leodreamer.sftcore.integration.ponder.api.annotation.PonderScene;
import org.leodreamer.sftcore.integration.ponder.api.annotation.PonderSceneScanned;
import org.leodreamer.sftcore.integration.ponder.api.annotation.WithTags;

import static org.leodreamer.sftcore.integration.ponder.misc.SFTPonderGroup.*;
import static org.leodreamer.sftcore.integration.ponder.misc.SFTPonderTag.MEK_MULTIBLOCK;

@PonderSceneScanned
@WithTags(MEK_MULTIBLOCK)
public class MEKMultiblockScenes {

    @PonderScene(groups = TURBINE, file = "ponder_turbine")
    public static void turbine(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("turbine", "Build a Steam Turbine");
        var sel = util.select();
        var vec = util.vector();

        scene.setSceneOffsetY(-1.5F);
        scene.scaleSceneView(0.5F);
        scene.showBasePlate();
        scene.text(30, "To build a Steam Turbine...");
        scene.idle(50);

        scene.world().showSection(sel.layer(1), Direction.DOWN);
        scene.text(80, "First, build a base with Turbine Casing with an odd length, up to 17");
        scene.idle(80);

        for (int h = 2; h <= 6; h++) {
            scene.world().showSection(sel.position(4, h, 4), Direction.DOWN);
            scene.idle(10);
        }

        scene.idle(40);
        scene.overlay().showOutlineWithText(sel.fromTo(4, 2, 4, 4, 6, 4), 60)
                .colored(PonderPalette.BLUE)
                .text("Place Turbine Rotors vertically in the center")
                .pointAt(vec.of(4, 4.5, 4))
                .placeNearTarget();
        scene.idle(60);

        scene.text(60, "Hold a Turbine Blade and right-click the Turbine Rotor to install blades, two blades are required for each rotor");
        scene.idle(30);
        scene.overlay().showControls(vec.of(4, 2.5, 4), Pointing.RIGHT, 60)
                .rightClick()
                .withItem(GeneratorsItems.TURBINE_BLADE.getItemStack());
        scene.idle(30);

        for (int h = 2; h <= 6; h++) {
            scene.world().modifyBlockEntityNBT(sel.position(4, h, 4), TileEntityTurbineRotor.class, (nbt) -> nbt.putInt("blades", 1));
            scene.idle(20 / h);
            scene.world().modifyBlockEntityNBT(sel.position(4, h, 4), TileEntityTurbineRotor.class, (nbt) -> nbt.putInt("blades", 2));
            scene.idle(20 / h);
        }

        var complex = sel.position(4, 7, 4);
        scene.world().showSection(complex, Direction.DOWN);
        scene.idle(20);

        scene.overlay().showOutlineWithText(complex, 60)
                .colored(PonderPalette.GREEN)
                .text("Place a Rotation Complex on top of the rotors")
                .pointAt(vec.of(4, 7.5, 4))
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(80);

        scene.world().showSection(sel.fromTo(1, 2, 1, 1, 6, 7), Direction.EAST);
        scene.idle(10);
        scene.world().showSection(sel.fromTo(7, 2, 1, 7, 6, 7), Direction.WEST);
        scene.idle(10);
        scene.world().showSection(sel.fromTo(2, 2, 1, 6, 6, 1), Direction.SOUTH);
        scene.idle(10);
        scene.world().showSection(sel.fromTo(2, 2, 7, 6, 6, 7), Direction.NORTH);
        scene.idle(10);

        scene.idle(20);
        for (int i = 2; i <= 6; i++) {
            for (int j = 2; j <= 6; j++) {
                if (i == 4 && j == 4) {
                    continue;
                }
                scene.world().showSection(sel.position(i, 7, j), Direction.DOWN);
                scene.idle(2);
            }
        }
        scene.overlay().showOutlineWithText(sel.fromTo(2, 7, 2, 6, 7, 6), 60)
                .colored(PonderPalette.BLUE)
                .text("Fill the layer of the Rotation Complex with Pressure Dispersers")
                .placeNearTarget();
        scene.idle(60);

        scene.world().showSection(sel.fromTo(4, 8, 3, 4, 8, 5), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showOutlineWithText(sel.fromTo(4, 8, 3, 4, 8, 5), 100)
                .colored(PonderPalette.BLUE)
                .text("Place Electromagnetic Coils directly above the Rotation Complex, connected and at least half the number of rotors")
                .pointAt(vec.of(4, 8.5, 4))
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(100);

        scene.world().showSection(sel.fromTo(1, 7, 1, 1, 8, 7), Direction.EAST);
        scene.idle(10);
        scene.world().showSection(sel.fromTo(7, 7, 1, 7, 8, 7), Direction.WEST);
        scene.idle(10);
        scene.world().showSection(sel.fromTo(2, 7, 1, 6, 8, 1), Direction.SOUTH);
        scene.idle(10);
        scene.world().showSection(sel.fromTo(2, 7, 7, 6, 8, 7), Direction.NORTH);
        scene.idle(10);

        scene.overlay().showOutlineWithText(sel.fromTo(1, 7, 1, 7, 8, 7), 80)
                .colored(PonderPalette.GREEN)
                .text("Replace Turbine Casing around and above the Pressure Dispersers with as many Turbine Vanes as possible")
                .pointAt(vec.of(3, 7.5, 3));
        scene.idle(80);

        for (int i = 2; i <= 6; i++) {
            for (int j = 2; j <= 6; j++) {
                if (i == 4 && j >= 3 && j <= 5) {
                    continue;
                }
                scene.world().showSection(sel.position(i, 8, j), Direction.DOWN);
                scene.idle(2);
            }
        }
        scene.text(60, "If needed, you can also place several Saturated Condensers in the upper layer to recycle water");
        scene.idle(60);

        scene.world().setBlocks(sel.fromTo(2, 8, 2, 3, 8, 6),
                Blocks.AIR.defaultBlockState(), true);
        scene.idle(10);
        scene.world().setBlocks(sel.fromTo(5, 8, 2, 6, 8, 6),
                Blocks.AIR.defaultBlockState(), true);
        scene.idle(40);

        scene.world().showSection(sel.layer(9), Direction.DOWN);
        scene.idle(20);
        scene.text(40, "Finally, cap it off with Turbine Casing and Structure Glass")
                .attachKeyFrame();
        scene.idle(80);

        scene.world().setBlocks(sel.fromTo(3, 2, 1, 5, 2, 1), GeneratorsBlocks.TURBINE_VALVE.getBlock().defaultBlockState(), true);
        scene.idle(10);
        scene.text(60, "Replace Turbine Casing and Structure Glass with Turbine Valves for input and output")
                .pointAt(vec.of(4, 2.5, 1))
                .placeNearTarget();
        scene.idle(60);
        scene.text(60, "Input steam, and you will get a large amount of power!");
        scene.idle(80);
    }

    @PonderScene(groups = FISSION_FACTOR, file = "ponder_fission_reactor")
    public static void fissionReactor(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("fission_reactor", "Fission Reactor");
        var sel = util.select();
        var vec = util.vector();
        var pos = util.grid();

        scene.setSceneOffsetY(-1);
        scene.scaleSceneView(0.7F);
        scene.showBasePlate();
        scene.idle(25);
        var sec = scene.world().showIndependentSection(sel.fromTo(2, 2, 2, 5, 5, 5), Direction.DOWN);
        scene.world().showSection(sel.fromTo(1, 1, 1, 5, 1, 5), Direction.DOWN);
        scene.world().showSection(sel.fromTo(1, 1, 1, 1, 5, 5), Direction.DOWN);
        scene.world().showSection(sel.fromTo(1, 1, 1, 5, 5, 1), Direction.DOWN);
        scene.idle(15);
        scene.text(70, "Use Fission Reactor Casing to build a cuboid frame with length 3~18 blocks, width 3~18 blocks, and height 4~18 blocks");
        scene.idle(80);
        var sec1 = scene.world().showIndependentSection(sel.fromTo(1, 6, 1, 5, 10, 5), Direction.DOWN);
        scene.world().moveSection(sec1, vec.of(0, -5, 0), 0);
        scene.addKeyframe();
        scene.text(70, "Then fill all faces of the cuboid with Reactor Glass or Fission Reactor Casing");
        scene.idle(85);
        scene.addKeyframe();
        var sec2 = scene.world().showIndependentSection(sel.fromTo(1, 11, 1, 5, 12, 5), Direction.DOWN);
        scene.world().moveSection(sec2, vec.of(0, -9, 0), 0);
        scene.text(70, "Replace some of the casing on the faces with Fission Reactor Ports");
        scene.idle(85);
        scene.addKeyframe();
        scene.overlay().showControls(vec.of(2, 3, 1), Pointing.LEFT, 50)
                .rightClick()
                .withItem(MekanismItems.CONFIGURATOR.getItemStack())
                .whileSneaking();
        scene.text(50, "Shift right-click the port with a Configurator to change its input/output mode");
        scene.idle(65);
        scene.world().modifyBlock(pos.at(2, 11, 1),
                (state) -> state.setValue(AttributeStateFissionPortMode.modeProperty, AttributeStateFissionPortMode.FissionPortMode.OUTPUT_WASTE));
        scene.world().modifyBlock(pos.at(2, 12, 1),
                (state) -> state.setValue(AttributeStateFissionPortMode.modeProperty, AttributeStateFissionPortMode.FissionPortMode.OUTPUT_COOLANT));
        scene.idle(17);
        scene.rotateCameraY(180);
        scene.idle(20);
        scene.world().hideIndependentSection(sec, Direction.UP);
        scene.world().moveSection(sec1, vec.of(0, 35, 0), 35);
        scene.addKeyframe();
        scene.world().setBlocks(sel.fromTo(3, 2, 3, 3, 3, 3),
                GeneratorsBlocks.FISSION_FUEL_ASSEMBLY.getBlock().defaultBlockState());
        scene.world().setBlock(pos.at(3, 4, 3), GeneratorsBlocks.CONTROL_ROD_ASSEMBLY.getBlock().defaultBlockState());
        scene.idle(20);
        scene.text(40, "Place Fission Fuel Assemblies vertically inside the reactor");
        scene.world().showSection(sel.position(3, 2, 3), Direction.DOWN);
        scene.world().showSection(sel.position(3, 3, 3), Direction.DOWN);
        scene.idle(50);
        scene.text(40, "Place Control Rod Assemblies on top of the Fuel Assemblies");
        scene.world().showSection(sel.position(3, 4, 3), Direction.DOWN);
        scene.idle(50);
        scene.rotateCameraY(180);
        scene.world().showSection(sel.fromTo(2, 2, 2, 5, 5, 5), Direction.DOWN);
        scene.world().moveSection(sec1, vec.of(0, -35, 0), 35);
        scene.idle(40);
        scene.addKeyframe();
        scene.text(40, "You can also automate the reactor using the Fission Reactor Logic Adapter");
        scene.world().setBlock(pos.at(3, 5, 3),
                GeneratorsBlocks.REACTOR_GLASS.getBlock().defaultBlockState(), true);
        scene.world().setBlock(pos.at(3, 5, 3),
                GeneratorsBlocks.FISSION_REACTOR_LOGIC_ADAPTER.getBlock().defaultBlockState(), true);
        scene.idle(40);
    }

    @PonderScene(groups = SPS, file = "ponder_sps")
    public static void sps(SFTSceneBuilder scene, SceneBuildingUtil util) {
        scene.title("sps", "Supercritical Phase Shifter");
        var sel = util.select();
        var vec = util.vector();
        var pos = util.grid();

        scene.setSceneOffsetY(-1);
        scene.scaleSceneView(0.6F);
        scene.showBasePlate();
        scene.idle(25);

        scene.world().showSection(sel.fromTo(0, 2, 0, 8, 2, 8), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showOutlineWithText(sel.fromTo(1, 2, 1, 7, 2, 7), 60)
                .colored(PonderPalette.BLUE)
                .text("Use Supercritical Phase Shifter Casing and Structure Glass to build the shape like this...")
                .pointAt(vec.of(1.5, 3.5, 1));
        scene.idle(75);
        scene.addKeyframe();

        scene.text(40, "And fill all 6 sides with the same shape, edge to edge");
        scene.idle(55);
        scene.rotateCameraY(180);
        scene.addKeyframe();
        scene.idle(30);

        scene.world().showSection(sel.fromTo(1, 3, 1, 7, 8, 1), Direction.DOWN);
        scene.idle(30);
        scene.world().showSection(sel.fromTo(1, 3, 2, 1, 8, 7), Direction.DOWN);
        scene.idle(30);
        var rem1 = scene.world().showIndependentSection(sel.fromTo(7, 3, 2, 7, 8, 7), Direction.DOWN);
        scene.idle(30);
        var rem2 = scene.world().showIndependentSection(sel.fromTo(2, 3, 7, 6, 8, 7), Direction.DOWN);
        scene.idle(30);
        var rem3 = scene.world().showIndependentSection(sel.fromTo(2, 8, 6, 6, 8, 2), Direction.DOWN);
        scene.idle(30);
        scene.addKeyframe();
        scene.idle(55);

        scene.world().hideIndependentSection(rem1, Direction.UP);
        scene.world().hideIndependentSection(rem2, Direction.UP);
        scene.world().hideIndependentSection(rem3, Direction.UP);
        scene.idle(30);
        var spsPort = MekanismBlocks.SPS_PORT.getBlock().defaultBlockState();
        scene.world().setBlock(pos.at(4, 2, 4), spsPort);
        scene.idle(20);
        scene.addKeyframe();

        scene.overlay().showOutlineWithText(sel.fromTo(4, 2, 4, 4, 2, 4), 60)
                .colored(PonderPalette.BLUE)
                .text("Place the Supercritical Phase Shifter Port here and supply it with a large amount of power")
                .pointAt(vec.of(4.5, 2.5, 4));
        scene.idle(70);
        scene.world().setBlock(pos.at(4, 3, 4), MekanismBlocks.SUPERCHARGED_COIL.getBlock().defaultBlockState());
        scene.world().showSection(sel.position(4, 3, 4), Direction.DOWN);
        scene.world().modifyBlock(pos.at(4, 3, 4),
                (state) -> state.setValue(BlockStateProperties.FACING, Direction.UP));
        scene.idle(20);
        scene.addKeyframe();

        scene.overlay().showOutlineWithText(sel.fromTo(4, 3, 4, 4, 3, 4), 60)
                .colored(PonderPalette.BLUE)
                .text("And place a Supercharged Coil here")
                .pointAt(vec.of(4.5, 3.5, 4));
        scene.idle(70);
        scene.world().showSection(sel.fromTo(7, 3, 2, 7, 8, 7), Direction.DOWN);
        scene.world().showSection(sel.fromTo(2, 3, 7, 6, 8, 7), Direction.DOWN);
        scene.world().showSection(sel.fromTo(2, 8, 6, 6, 8, 2), Direction.DOWN);
        scene.idle(20);
        scene.world().setBlock(pos.at(7, 5, 3), spsPort);
        scene.world().setBlock(pos.at(7, 5, 5), spsPort);
        scene.idle(10);
        scene.addKeyframe();

        scene.overlay().showOutlineWithText(sel.fromTo(7, 5, 3, 7, 5, 5), 60)
                .colored(PonderPalette.BLUE)
                .text("Replace several structure glass on other sides with Supercritical Phase Shifter Ports")
                .pointAt(vec.of(7, 5, 5));
        scene.idle(70);
        scene.overlay().showControls(vec.of(6.5, 5, 5), Pointing.RIGHT, 60)
                .rightClick()
                .withItem(MekanismItems.CONFIGURATOR.getItemStack())
                .whileSneaking();
        scene.addKeyframe();

        scene.text(60, "Shift right-click to configure the port to output mode");
        scene.world().modifyBlock(pos.at(7, 5, 5), state -> Attribute.setActive(state, true));
        scene.idle(70);
        scene.text(60, "Now use Pressurized Tubes to input Polonium into the input port to produce Antimatter!")
                .pointAt(vec.of(7, 3.5, 5));
        scene.idle(70);
    }
}
