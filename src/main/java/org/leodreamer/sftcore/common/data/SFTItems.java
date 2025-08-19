package org.leodreamer.sftcore.common.data;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import org.leodreamer.sftcore.common.item.SelectStickItem;
import org.leodreamer.sftcore.common.item.behavior.TimeBottleBehavior;

import static com.gregtechceu.gtceu.common.data.GTItems.attach;
import static org.leodreamer.sftcore.SFTCore.REGISTRATE;

public final class SFTItems {

    public static final ItemEntry<SelectStickItem> SELECT_STICK =
            REGISTRATE.item("select_stick", (p) -> new SelectStickItem())
                    .lang("Select Stick")
                    .register();

    public static final ItemEntry<ComponentItem> TIME_BOTTLE =
            REGISTRATE.item("time_bottle", ComponentItem::create)
                    .lang("Time Bottle")
                    .properties(p -> p.stacksTo(1))
                    .onRegister(attach(new TimeBottleBehavior()))
                    .register();

    public static final ItemEntry<Item> UU_MATTER =
            REGISTRATE.item("uu_matter", Item::new).lang("UU Matter").register();
    public static final ItemEntry<Item> INCOMPLETE_UU_MATTER =
            REGISTRATE.item("incomplete_uu_matter", Item::new).lang("Incomplete UU Matter").register();

    @SuppressWarnings("unchecked")
    public static final ItemEntry<Item>[] UNIVERSAL_CIRCUITS = new ItemEntry[]{
            registerUniversalCircuit(GTValues.ULV),
            registerUniversalCircuit(GTValues.LV),
            registerUniversalCircuit(GTValues.MV),
            registerUniversalCircuit(GTValues.HV),
            registerUniversalCircuit(GTValues.EV),
            registerUniversalCircuit(GTValues.IV),
            registerUniversalCircuit(GTValues.LuV),
            registerUniversalCircuit(GTValues.ZPM),
            registerUniversalCircuit(GTValues.UV),
            registerUniversalCircuit(GTValues.UHV)
    };

    private static ItemEntry<Item> registerUniversalCircuit(int tier) {
        var name = GTValues.VN[tier].toLowerCase();
        return REGISTRATE.item("%s_universal_circuit".formatted(name), Item::new)
                .lang("%s §rUniversal Circuit".formatted(GTValues.VNF[tier]))
                .tag(CustomTags.CIRCUITS_ARRAY[tier]).register();
    }

    public static void init() {
    }
}
