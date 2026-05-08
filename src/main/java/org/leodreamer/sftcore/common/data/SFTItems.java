package org.leodreamer.sftcore.common.data;

import org.leodreamer.sftcore.common.item.OrderBehavior;
import org.leodreamer.sftcore.common.item.WildcardPatternBehavior;
import org.leodreamer.sftcore.integration.IntegrateMods;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.common.item.TooltipBehavior;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import java.util.List;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.common.data.GTItems.attach;
import static org.leodreamer.sftcore.SFTCore.REGISTRATE;

public final class SFTItems {

    public static final ItemEntry<ComponentItem> ORDER = REGISTRATE
        .item("order", ComponentItem::create)
        .lang("%sOrder")
        .properties(p -> p.stacksTo(1))
        .onRegister(attach(new OrderBehavior()))
        .register();

    public static final ItemEntry<ComponentItem> WILDCARD_PATTERN = REGISTRATE
        .item("wildcard_pattern", ComponentItem::create)
        .lang("Wildcard Pattern")
        .model(generatedModel(ResourceLocation.fromNamespaceAndPath(IntegrateMods.AE, "item/processing_pattern")))
        .properties(p -> p.stacksTo(1))
        .onRegister(attach(new WildcardPatternBehavior()))
        .register();

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
