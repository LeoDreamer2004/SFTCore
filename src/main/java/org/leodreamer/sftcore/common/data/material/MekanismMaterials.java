package org.leodreamer.sftcore.common.data.material;

import org.leodreamer.sftcore.SFTCore;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;

import static org.leodreamer.sftcore.common.data.SFTMaterials.*;

public class MekanismMaterials {

    public static void register() {
        EnergeticFissileFuel = new Material.Builder(SFTCore.id("energetic_fissile_fuel"))
            .fluid()
            .color(0x282c29)
            .buildAndRegister();
        FilteredPolonium = new Material.Builder(SFTCore.id("filtered_polonium"))
            .fluid()
            .color(0x1a7b60)
            .buildAndRegister();
        FilteredPlutonium = new Material.Builder(SFTCore.id("filtered_plutonium"))
            .fluid()
            .color(0x1e7d87)
            .buildAndRegister();
        LowPurityDeuterium = new Material.Builder(SFTCore.id("low_purity_deuterium"))
            .fluid()
            .color(0xffca37)
            .buildAndRegister();
        LowPurityTritium = new Material.Builder(SFTCore.id("low_purity_tritium"))
            .fluid()
            .color(0x17dcbe)
            .buildAndRegister();
    }
}
