package org.leodreamer.sftcore.common.data;

import org.leodreamer.sftcore.common.data.material.MekanismMaterials;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;

public final class SFTMaterials {

    public static void init() {
        MekanismMaterials.register();
    }

    // mek
    public static Material EnergeticFissileFuel;
    public static Material LowPurityDeuterium;
    public static Material LowPurityTritium;
    public static Material FilteredPolonium;
    public static Material FilteredPlutonium;
}
