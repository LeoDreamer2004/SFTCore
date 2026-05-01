package org.leodreamer.sftcore.common.advancement;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.advancement.trigger.FormedGTMultiblockTrigger;
import org.leodreamer.sftcore.util.RLUtils;

public class SFTAdvancementBuilder {

    private final String id;
    private final Advancement.Builder builder;
    private boolean show = true;
    private static final ResourceLocation BACKGROUND = null;
    public static final Object2ObjectArrayMap<String, String> ADVANCEMENT_LANG = new Object2ObjectArrayMap<>();

    private SFTAdvancementBuilder(String id) {
        this.id = id;
        this.builder = Advancement.Builder.advancement();
    }

    public static SFTAdvancementBuilder create(String id) {
        return new SFTAdvancementBuilder(id);
    }

    public SFTAdvancementBuilder parent(Advancement parent) {
        builder.parent(parent);
        return this;
    }

    public Advancement build() {
        return builder.build(SFTCore.id(id));
    }

    /// ---------- Display ---------- ///

    public SFTAdvancementBuilder show(boolean show) {
        this.show = show;
        return this;
    }

    public SFTAdvancementBuilder silent() {
        return show(false);
    }

    public SFTAdvancementBuilder display(ItemLike icon, String title, String description) {
        var titleKey = "advancements.sftcore." + id + ".title";
        var descriptionKey = "advancements.sftcore." + id + ".description";
        ADVANCEMENT_LANG.put(titleKey, title);
        ADVANCEMENT_LANG.put(descriptionKey, description);

        builder.display(
            icon,
            Component.translatable(titleKey),
            Component.translatable(descriptionKey),
            BACKGROUND,
            FrameType.TASK,
            show,
            show,
            false
        );
        return this;
    }

    /// ---------- Criteria ---------- ///

    public SFTAdvancementBuilder free() {
        return obtain();
    }

    public SFTAdvancementBuilder obtain(ItemLike... what) {

        // has_{namespace}_{path}[_and_{namespace}_{path}]*
        var key = new StringBuilder("has_");
        for (int i = 0; i < what.length; i++) {
            if (i > 0) {
                key.append("_and_");
            }
            var rl = RLUtils.getItemRL(what[i]);
            key.append(rl.getNamespace()).append("_").append(rl.getPath());
        }

        builder.addCriterion(key.toString(), InventoryChangeTrigger.TriggerInstance.hasItems(what));
        return this;
    }

    public SFTAdvancementBuilder onFormed(MachineDefinition controller) {
        var id = controller.getId();
        builder.addCriterion("formed_" + id.getNamespace() + "_" + id.getPath(),
            FormedGTMultiblockTrigger.Instance.machine(id));
        return this;
    }

}
