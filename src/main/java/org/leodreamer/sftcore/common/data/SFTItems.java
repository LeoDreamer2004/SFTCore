package org.leodreamer.sftcore.common.data;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.common.item.CoverPlaceBehavior;
import com.gregtechceu.gtceu.common.item.TooltipBehavior;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.tterrag.registrate.util.entry.ItemEntry;
import mekanism.api.Upgrade;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;
import org.leodreamer.sftcore.common.cover.AccelerateCover;
import org.leodreamer.sftcore.common.item.SelectStickItem;
import org.leodreamer.sftcore.common.item.behavior.TimeBottleBehavior;
import org.leodreamer.sftcore.integration.mek.SuperUpgradeItem;

import java.util.Arrays;
import java.util.List;

import static com.gregtechceu.gtceu.common.data.GTItems.attach;
import static org.leodreamer.sftcore.SFTCore.REGISTRATE;

public final class SFTItems {

    public static final ItemEntry<SelectStickItem> SELECT_STICK =
            REGISTRATE.item("select_stick", SelectStickItem::new)
                    .lang("Select Stick")
                    .register();

    public static final ItemEntry<ComponentItem> TIME_BOTTLE =
            REGISTRATE.item("time_bottle", ComponentItem::create)
                    .lang("Time Bottle")
                    .properties(p -> p.stacksTo(1))
                    .onRegister(attach(new TimeBottleBehavior()))
                    .register();

    public static final ItemEntry<SuperUpgradeItem> SPEED_SUPER_UPGRADE = registerSuperUpgrade(Upgrade.SPEED);
    public static final ItemEntry<SuperUpgradeItem> ENERGY_SUPER_UPGRADE = registerSuperUpgrade(Upgrade.ENERGY);
    public static final ItemEntry<SuperUpgradeItem> FILTER_SUPER_UPGRADE = registerSuperUpgrade(Upgrade.FILTER);
    public static final ItemEntry<SuperUpgradeItem> GAS_SUPER_UPGRADE = registerSuperUpgrade(Upgrade.GAS);
    public static final ItemEntry<SuperUpgradeItem> MUFFLING_SUPER_UPGRADE = registerSuperUpgrade(Upgrade.MUFFLING);
    public static final ItemEntry<SuperUpgradeItem> ANCHOR_SUPER_UPGRADE = registerSuperUpgrade(Upgrade.ANCHOR);
    public static final ItemEntry<SuperUpgradeItem> STONE_GENERATOR_SUPER_UPGRADE = registerSuperUpgrade(Upgrade.STONE_GENERATOR);

    public static final ItemEntry<Item> UU_MATTER =
            REGISTRATE.item("uu_matter", Item::new).lang("UU Matter").register();

    public static final ItemEntry<Item> INCOMPLETE_UU_MATTER =
            REGISTRATE.item("incomplete_uu_matter", Item::new).lang("Incomplete UU Matter").register();

    public static final ItemEntry<ComponentItem>[] COVER_ACCELERATES = registerAccelerateCovers();

    public static final List<ItemEntry<ComponentItem>> UNIVERSAL_CIRCUITS =
            Arrays.stream(GTValues.tiersBetween(GTValues.ULV, GTValues.UHV))
                    .mapToObj(SFTItems::registerUniversalCircuit)
                    .toList();

    private static ItemEntry<SuperUpgradeItem> registerSuperUpgrade(Upgrade upgrade) {
        return REGISTRATE.item("super_upgrade_" + upgrade.getRawName(),
                        (p) -> new SuperUpgradeItem(p, upgrade))
                .register();
    }

    private static ItemEntry<ComponentItem>[] registerAccelerateCovers() {
        ItemEntry<ComponentItem>[] entries = new ItemEntry[GTValues.TIER_COUNT];

        for (int index = 0; index < AccelerateCover.TIERS.length; index++) {
            int tier = AccelerateCover.TIERS[index];
            var cover = SFTCovers.ACCELERATE_COVERS.get(index);
            int accel = tier * 50;
            entries[tier] = REGISTRATE.item("%s_accelerate_cover".formatted(GTValues.VN[tier].toLowerCase()), ComponentItem::create)
                    .lang("%s §rAccelerate Cover".formatted(GTValues.VNF[tier]))
                    .onRegister(attach(new CoverPlaceBehavior(cover),
                            new TooltipBehavior(lines -> SFTTooltipsBuilder.of()
                                    .insert(Component.translatable(AccelerateCover.TOOLTIP, accel))
                                    .textureComeFrom("Thermal Expansion").addTo(lines))))
                    .register();
        }

        return entries;
    }

    private static ItemEntry<ComponentItem> registerUniversalCircuit(int tier) {
        var name = GTValues.VN[tier].toLowerCase();
        return REGISTRATE.item("%s_universal_circuit".formatted(name), ComponentItem::create)
                .lang("%s §rUniversal Circuit".formatted(GTValues.VNF[tier]))
                .tag(CustomTags.CIRCUITS_ARRAY[tier])
                .onRegister(attach(new TooltipBehavior(lines -> SFTTooltipsBuilder.of().textureComeFrom("GregTech New Horizon").addTo(lines))))
                .register();
    }

    public static void init() {
    }
}
