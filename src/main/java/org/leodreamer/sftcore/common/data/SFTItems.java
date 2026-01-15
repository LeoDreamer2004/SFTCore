package org.leodreamer.sftcore.common.data;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;
import org.leodreamer.sftcore.common.cover.AccelerateCover;
import org.leodreamer.sftcore.common.item.OrderBehavior;
import org.leodreamer.sftcore.common.item.SelectStickBehavior;
import org.leodreamer.sftcore.common.item.TimeBottleBehavior;
import org.leodreamer.sftcore.common.item.WildcardPatternBehavior;
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
import net.minecraft.world.item.Item;

import appeng.api.stacks.AEKeyType;
import appeng.core.definitions.AEItems;
import appeng.items.materials.StorageComponentItem;
import appeng.items.storage.BasicStorageCell;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
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

    public static final ItemEntry<ComponentItem> SELECT_STICK = REGISTRATE.item("select_stick", ComponentItem::create)
        .lang("Select Stick")
        .model(generatedModel(ResourceLocation.withDefaultNamespace("item/stick")))
        .onRegister(attach(new SelectStickBehavior()))
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
            generatedModel(
                ResourceLocation.fromNamespaceAndPath(IntegrateMods.AE, "item/card_speed"),
                SFTCore.id("item/overlay/super_upgrade")
            )
        )
        .register();

    public static final ItemEntry<StorageComponentItem> HUGE_CELL_COMPONENT = REGISTRATE
        .item("huge_cell_component", p -> new StorageComponentItem(p, Integer.MAX_VALUE / 1024))
        .register();

    public static final ItemEntry<BasicStorageCell> HUGE_ITEM_CELL = REGISTRATE
        .item(
            "huge_item_cell", p -> new BasicStorageCell(
                p.stacksTo(1),
                HUGE_CELL_COMPONENT,
                AEItems.ITEM_CELL_HOUSING,
                4.0,
                Integer.MAX_VALUE / 1024,
                Integer.MAX_VALUE / 1024 / 64,
                63,
                AEKeyType.items()
            )
        )
        .register();

    public static final ItemEntry<BasicStorageCell> HUGE_FLUID_CELL = REGISTRATE
        .item(
            "huge_fluid_cell", p -> new BasicStorageCell(
                p.stacksTo(1),
                HUGE_CELL_COMPONENT,
                AEItems.FLUID_CELL_HOUSING,
                4.0,
                Integer.MAX_VALUE / 1024,
                Integer.MAX_VALUE / 1024 / 64,
                63,
                AEKeyType.fluids()
            )
        )
        .register();

    public static final ItemEntry<ComponentItem> WILDCARD_PATTERN = REGISTRATE
        .item("wildcard_pattern", ComponentItem::create)
        .lang("Wildcard Pattern")
        .model(generatedModel(ResourceLocation.fromNamespaceAndPath(IntegrateMods.AE, "item/processing_pattern")))
        .properties(p -> p.stacksTo(1))
        .onRegister(attach(new WildcardPatternBehavior()))
        .register();

    public static final ItemEntry<ComponentItem> UU_MATTER = REGISTRATE.item("uu_matter", ComponentItem::create)
        .lang("UU Matter")
        .onRegister(tooltip(SFTTooltipsBuilder.of().textureComeFrom("Industrial Craft 2")::addTo))
        .register();

    public static final ItemEntry<ComponentItem> INCOMPLETE_UU_MATTER = REGISTRATE
        .item("incomplete_uu_matter", ComponentItem::create)
        .onRegister(tooltip(SFTTooltipsBuilder.of().textureComeFrom("Industrial Craft 2")::addTo))
        .lang("Incomplete UU Matter").register();

    public static final ItemEntry<ComponentItem>[] COVER_ACCELERATES = registerAccelerateCovers();

    public static final List<ItemEntry<ComponentItem>> UNIVERSAL_CIRCUITS = Arrays
        .stream(GTValues.tiersBetween(GTValues.ULV, GTValues.UHV))
        .mapToObj(SFTItems::registerUniversalCircuit)
        .toList();

    private static ItemEntry<SuperUpgradeItem> registerSuperUpgrade(Upgrade upgrade) {
        return REGISTRATE
            .item("super_upgrade_" + upgrade.getRawName(), (p) -> new SuperUpgradeItem(p, upgrade))
            .model(
                generatedModel(
                    ResourceLocation.fromNamespaceAndPath(IntegrateMods.MEK, "item/upgrade_" + upgrade.getRawName())
                )
            )
            .register();
    }

    private static ItemEntry<ComponentItem>[] registerAccelerateCovers() {
        @SuppressWarnings("unchecked")
        ItemEntry<ComponentItem>[] entries = new ItemEntry[GTValues.TIER_COUNT];

        for (int index = 0; index < AccelerateCover.TIERS.length; index++) {
            int tier = AccelerateCover.TIERS[index];
            var cover = SFTCovers.ACCELERATE_COVERS.get(index);
            entries[tier] = REGISTRATE
                .item(
                    "%s_accelerate_cover".formatted(GTValues.VN[tier].toLowerCase()),
                    ComponentItem::create
                )
                .lang("%s §rAccelerate Cover".formatted(GTValues.VNF[tier]))
                .onRegister(attach(new CoverPlaceBehavior(cover)))
                .onRegister(tooltip(AccelerateCover.getTooltips(tier)::addTo))
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
            .onRegister(tooltip(SFTTooltipsBuilder.of().textureComeFrom("GregTech New Horizon")::addTo))
            .register();
    }

    private static <T extends IComponentItem> NonNullConsumer<T> tooltip(Consumer<List<Component>> tooltips) {
        return attach(new TooltipBehavior(tooltips));
    }

    private static <
        T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> generatedModel(
            ResourceLocation... layers
        ) {
        return (ctx, prov) -> prov.generated(ctx::getEntry, layers);
    }

    public static void init() {}
}
