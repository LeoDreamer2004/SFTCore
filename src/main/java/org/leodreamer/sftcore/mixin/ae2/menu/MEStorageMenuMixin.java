package org.leodreamer.sftcore.mixin.ae2.menu;

import org.leodreamer.sftcore.integration.ae2.feature.IGetCraftables;

import appeng.api.stacks.AEKey;
import appeng.menu.me.common.MEStorageMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(MEStorageMenu.class)
public abstract class MEStorageMenuMixin implements IGetCraftables {

    @Shadow(remap = false)
    protected abstract Set<AEKey> getCraftablesFromGrid();

    @Override
    public Set<AEKey> sftcore$getCraftables() {
        return getCraftablesFromGrid();
    }
}
