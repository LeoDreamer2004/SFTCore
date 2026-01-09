package org.leodreamer.sftcore.mixin.gregtech.data;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.SFTTools;
import org.leodreamer.sftcore.util.ReflectUtils;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.ToolProperty;

import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Material.Builder.class)
public class MaterialBuilderMixin {

    @Inject(method = "toolStats", at = @At("HEAD"), remap = false)
    private void allowNeutroniumForVajra(ToolProperty toolProperty, CallbackInfoReturnable<Material.Builder> cir) {
        // everything is private yay...
        // so use reflection to get the materialInfo field
        // thanks to Java's kindness.
        var materialInfoClazz = ReflectUtils.getInnerClassByName(Material.class, "MaterialInfo");
        var materialInfo = ReflectUtils.getFieldValue(this, "materialInfo", materialInfoClazz);
        var rl = ReflectUtils.getFieldValue(materialInfo, "resourceLocation", ResourceLocation.class);
        if (rl.getPath().contains("neutronium")) {
            SFTCore.LOGGER.info("Adding Vajra tool type to Neutronium material");
            toolProperty.addTypes(SFTTools.VAJRA);
        }
    }
}
