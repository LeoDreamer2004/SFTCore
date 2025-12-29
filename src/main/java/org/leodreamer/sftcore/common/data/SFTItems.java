package org.leodreamer.sftcore.common.data;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;
import org.leodreamer.sftcore.common.cover.AccelerateCover;
import org.leodreamer.sftcore.common.item.SelectStickItem;
import org.leodreamer.sftcore.common.item.behavior.OrderBehavior;
import org.leodreamer.sftcore.common.item.behavior.TimeBottleBehavior;
import org.leodreamer.sftcore.common.item.behavior.WildcardPatternBehavior;
import org.leodreamer.sftcore.integration.IntegrateMods;
import org.leodreamer.sftcore.integration.ae2.item.SuperUpgradeCardItem;
import org.leodreamer.sftcore.integration.mek.SuperUpgradeItem;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.common.item.CoverPlaceBehavior;
import com.gregtechceu.gtceu.common.item.TooltipBehavior;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import appeng.core.definitions.AEItems;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import mekanism.api.Upgrade;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.common.data.GTItems.attach;
import static org.leodreamer.sftcore.SFTCore.REGISTRATE;

public final class SFTItems {

    public static final ItemEntry<SelectStickItem> SELECT_STICK = REGISTRATE.item("select_stick", SelectStickItem::new)
        .lang("Select Stick")
        .model((ctx, prov) -> prov.generated(ctx::getEntry, ResourceLocation.withDefaultNamespace("item/stick")))
        .register();

    public static final ItemEntry<ComponentItem> TIME_BOTTLE = REGISTRATE
        .item("time_bottle", ComponentItem::create)
        .lang("Time Bottle")
        .properties(p -> p.stacksTo(1))
        .onRegister(attach(new TimeBottleBehavior()))
        .register();

    public static final ItemEntry<ComponentItem> ORDER = REGISTRATE
        .item("order", ComponentItem::create)
        .lang("%sOrder")
        .properties(p -> p.stacksTo(1))
        .onRegister(attach(new OrderBehavior()))
        .register();

    public static final ItemEntry<SuperUpgradeItem> SPEED_SUPER_UPGRADE = registerSuperUpgrade(Upgrade.SPEED);
    public static final ItemEntry<SuperUpgradeItem> ENERGY_SUPER_UPGRADE = registerSuperUpgrade(Upgrade.ENERGY);
    public static final ItemEntry<SuperUpgradeItem> FILTER_SUPER_UPGRADE = registerSuperUpgrade(Upgrade.FILTER);
    public static final ItemEntry<SuperUpgradeItem> GAS_SUPER_UPGRADE = registerSuperUpgrade(Upgrade.GAS);
    public static final ItemEntry<SuperUpgradeItem> MUFFLING_SUPER_UPGRADE = registerSuperUpgrade(Upgrade.MUFFLING);
    public static final ItemEntry<SuperUpgradeItem> ANCHOR_SUPER_UPGRADE = registerSuperUpgrade(Upgrade.ANCHOR);
    public static final ItemEntry<SuperUpgradeItem> STONE_GENERATOR_SUPER_UPGRADE = registerSuperUpgrade(
        Upgrade.STONE_GENERATOR
    );

    public static final ItemEntry<SuperUpgradeCardItem> SUPER_SPEED_CARD = REGISTRATE
        .item("super_speed_card", p -> new SuperUpgradeCardItem(AEItems.SPEED_CARD.asItem(), p))
        .lang("Super Acceleration Card")
        .model(
            (ctx, prov) -> prov.generated(
                ctx::getEntry, ResourceLocation.fromNamespaceAndPath(IntegrateMods.AE, "item/card_speed"),
                SFTCore.id("item/overlay/super_upgrade")
            )
        )
        .register();

    public static final ItemEntry<ComponentItem> WILDCARD_PATTERN = REGISTRATE
        .item("wildcard_pattern", ComponentItem::create)
        .lang("Wildcard Pattern")
        .model(
            (ctx, prov) -> prov.generated(
                ctx::getEntry, ResourceLocation.fromNamespaceAndPath(IntegrateMods.AE, "item/processing_pattern")
            )
        )
        .properties(p -> p.stacksTo(1))
        .onRegister(attach(new WildcardPatternBehavior()))
        .register();

    public static final ItemEntry<ComponentItem> UU_MATTER = REGISTRATE.item("uu_matter", ComponentItem::create)
        .lang("UU Matter")
        .onRegister(
            tooltip(SFTTooltipsBuilder.of().textureComeFrom("Industrial Craft 2")::addTo)
        )
        .register();

    public static final ItemEntry<ComponentItem> INCOMPLETE_UU_MATTER = REGISTRATE
        .item("incomplete_uu_matter", ComponentItem::create)
        .onRegister(
            tooltip(SFTTooltipsBuilder.of().textureComeFrom("Industrial Craft 2")::addTo)
        )
        .lang("Incomplete UU Matter").register();

    public static final ItemEntry<ComponentItem>[] COVER_ACCELERATES = registerAccelerateCovers();

    public static final List<ItemEntry<ComponentItem>> UNIVERSAL_CIRCUITS = Arrays
        .stream(GTValues.tiersBetween(GTValues.ULV, GTValues.UHV))
        .mapToObj(SFTItems::registerUniversalCircuit)
        .toList();

    private static ItemEntry<SuperUpgradeItem> registerSuperUpgrade(Upgrade upgrade) {
        return REGISTRATE
            .item("super_upgrade_" + upgrade.getRawName(), (p) -> new SuperUpgradeItem(p, upgrade))
            .model(NonNullBiConsumer.noop())
            .register();
    }

    private static ItemEntry<ComponentItem>[] registerAccelerateCovers() {
        @SuppressWarnings("unchecked")
        ItemEntry<ComponentItem>[] entries = new ItemEntry[GTValues.TIER_COUNT];

        for (int index = 0; index < AccelerateCover.TIERS.length; index++) {
            int tier = AccelerateCover.TIERS[index];
            var cover = SFTCovers.ACCELERATE_COVERS.get(index);
            int accel = tier * 50;
            entries[tier] = REGISTRATE
                .item(
                    "%s_accelerate_cover".formatted(GTValues.VN[tier].toLowerCase()),
                    ComponentItem::create
                )
                .lang("%s §rAccelerate Cover".formatted(GTValues.VNF[tier]))
                .onRegister(
                    attach(
                        new CoverPlaceBehavior(cover),
                        new TooltipBehavior(
                            lines -> SFTTooltipsBuilder.of()
                                .insert(Component.translatable(AccelerateCover.TOOLTIP, accel))
                                .textureComeFrom("Thermal Expansion")
                                .addTo(lines)
                        )
                    )
                )
                .register();
        }

        return entries;
    }

    private static ItemEntry<ComponentItem> registerUniversalCircuit(int tier) {
        var name = GTValues.VN[tier].toLowerCase();
        return REGISTRATE
            .item("%s_universal_circuit".formatted(name), ComponentItem::create)
            .lang("%s §rUniversal Circuit".formatted(GTValues.VNF[tier]))
            .tag(CustomTags.CIRCUITS_ARRAY[tier])
            .onRegister(
                attach(
                    new TooltipBehavior(
                        lines -> SFTTooltipsBuilder.of()
                            .textureComeFrom("GregTech New Horizon")
                            .addTo(lines)
                    )
                )
            )
            .register();
    }

    private static <T extends IComponentItem> NonNullConsumer<T> tooltip(Consumer<List<Component>> tooltips) {
        return attach(new TooltipBehavior(tooltips));
    }

    public static void init() {}
}
