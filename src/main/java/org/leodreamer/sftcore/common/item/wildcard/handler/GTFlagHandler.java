package org.leodreamer.sftcore.common.item.wildcard.handler;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlag;

import net.minecraft.MethodsReturnNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GTFlagHandler extends GTMaterialHandler {

    public static final Codec<MaterialFlag> MATERIAL_FLAG_CODEC = Codec.STRING.comapFlatMap(name -> {
        var flag = flagFromName(name);
        if (flag == null) {
            return DataResult.error(() -> "Unknown material flag: " + name);
        }
        return DataResult.success(flag);
    }, MaterialFlag::toString);

    @Nullable
    @Getter
    @Setter
    private MaterialFlag flag;

    public GTFlagHandler(@Nullable MaterialFlag flag, Material example) {
        super(example);
        this.flag = flag;
    }

    public void setFlagName(String flagName) {
        this.flag = flagFromName(flagName);
    }

    public static @Nullable MaterialFlag flagFromName(String flagName) {
        if (flagName.isBlank()) {
            return null;
        }
        return MaterialFlag.getByName(flagName);
    }
}
