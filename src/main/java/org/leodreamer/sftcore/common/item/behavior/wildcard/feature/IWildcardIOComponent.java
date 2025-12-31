package org.leodreamer.sftcore.common.item.behavior.wildcard.feature;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;

import appeng.api.stacks.GenericStack;
import org.jetbrains.annotations.Nullable;

public interface IWildcardIOComponent extends IWildcardSerializable<IWildcardIOComponent>, IWildcardComponentUI {

    @Nullable
    GenericStack apply(Material material);
}
