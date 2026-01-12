package org.leodreamer.sftcore.common;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.common.data.GTMaterialItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.item.tool.behavior.DisableShieldBehavior;
import com.gregtechceu.gtceu.common.item.tool.behavior.ToolModeSwitchBehavior;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

@DataGenScanned
public class SFTTools {

    @RegisterLanguage("%s Vajra")
    private static final String VAJRA_NAME = "item.gtceu.tool.vajra";

    public static final GTToolType VAJRA = GTToolType.builder("vajra")
        .modelLocation(SFTCore.id("item/vajra"))
        .toolTag(CustomTags.WRENCHES)
        .toolTag(CustomTags.WRENCH)
        .toolTag(CustomTags.WIRE_CUTTERS)
        .toolTag(ItemTags.PICKAXES)
        .toolTag(ItemTags.SHOVELS)
        .toolTag(ItemTags.AXES)
        .harvestTag(BlockTags.MINEABLE_WITH_PICKAXE)
        .harvestTag(BlockTags.MINEABLE_WITH_SHOVEL)
        .harvestTag(BlockTags.MINEABLE_WITH_AXE)
        .harvestTag(CustomTags.MINEABLE_WITH_WRENCH)
        .toolStats(
            b -> b.crafting().blockBreaking().sneakBypassUse().attacking().attackDamage(10.0F).attackSpeed(4.0F)
                .behaviors(DisableShieldBehavior.INSTANCE, ToolModeSwitchBehavior.INSTANCE)
        )
        .toolClasses(GTToolType.WRENCH, GTToolType.WIRE_CUTTER, GTToolType.PICKAXE, GTToolType.SHEARS, GTToolType.AXE)
        .build();

    public static ItemStack getVajra() {
        return Objects.requireNonNull(GTMaterialItems.TOOL_ITEMS.get(GTMaterials.Neutronium, VAJRA)).asStack();
    }
}
