package org.leodreamer.sftcore.integration.ae2.feature;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IVirtualCatalystEncoding {

    boolean sftcore$getVirtualCatalystsEnabled();

    void sftcore$setVirtualCatalystsEnabled(boolean enabled);

    void sftcore$setVirtualCatalysts(List<ItemStack> catalysts);
}
