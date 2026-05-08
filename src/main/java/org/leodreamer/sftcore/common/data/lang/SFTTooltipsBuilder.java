package org.leodreamer.sftcore.common.data.lang;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

@DataGenScanned
public class SFTTooltipsBuilder {

    private final List<Component> tooltips;
    @Nullable
    private final ResourceLocation id;

    public static final Object2ObjectArrayMap<String, String> TOOLTIPS_LANG = new Object2ObjectArrayMap<>();

    SFTTooltipsBuilder(@Nullable ResourceLocation id) {
        tooltips = new ArrayList<>();
        this.id = id;
    }

    public static SFTTooltipsBuilder of() {
        return new SFTTooltipsBuilder(null);
    }

    public static SFTTooltipsBuilder machine(ResourceLocation id) {
        return new SFTTooltipsBuilder(id);
    }

    public SFTTooltipsBuilder insert(Component component) {
        tooltips.add(component);
        return this;
    }

    public SFTTooltipsBuilder insert(Component... component) {
        tooltips.addAll(Arrays.asList(component));
        return this;
    }

    public List<Component> list() {
        return tooltips;
    }

    public void addTo(List<Component> components) {
        components.addAll(tooltips);
    }

    public Component[] array() {
        return tooltips.toArray(new Component[0]);
    }

    public SFTTooltipsBuilder tip(String tooltip) {
        if (this.id == null) {
            throw new IllegalStateException("Cannot insert a tip without an id");
        }
        var key = id.getNamespace() + ".machine." + id.getPath() + ".tooltip";
        TOOLTIPS_LANG.put(key, tooltip); // the tooltip is auto-shown

        return this;
    }

    public SFTTooltipsBuilder intro(String... contents) {
        if (this.id == null) {
            throw new IllegalStateException("Cannot insert an intro tooltip without a machine");
        }

        List<String> keys = new ArrayList<>();

        for (int i = 0; i < contents.length; i++) {
            var key = id.getNamespace() + ".machine." + id.getPath() + ".tooltip." + i;
            TOOLTIPS_LANG.put(key, contents[i]);
            keys.add(key);
        }
        tooltips.addAll(keys.stream().map(Component::translatable).toList());

        return this;
    }

    @RegisterLanguage(
        "§2- §lBlast Furnace Coil Bonus: §7§oFor every voltage tier above §bMV§7§o, temperature is increased by §r100K."
    )
    static final String EBF_0 = "gtceu.machine.electric_blast_furnace.tooltip.0";

    @RegisterLanguage(
        "§7§o   For every §f900K§7§o above the recipe temperature, energy consumption is reduced by §f5%%.§r"
    )
    static final String EBF_1 = "gtceu.machine.electric_blast_furnace.tooltip.1";

    @RegisterLanguage("§7§o   For every §f1800K§7§o above the recipe temperature, one perfect overclock is granted.§r")
    static final String EBF_2 = "gtceu.machine.electric_blast_furnace.tooltip.2";

    public SFTTooltipsBuilder ebf() {
        return this.insert(
            Component.translatable(EBF_0),
            Component.translatable(EBF_1),
            Component.translatable(EBF_2)
        );
    }

    @RegisterLanguage("Textures come from: %s")
    static final String TEXTURE_COME_FROM = "sftcore.texture_come_from";

    public SFTTooltipsBuilder textureComeFrom(String where) {
        return this.insert(
            Component.translatable(TEXTURE_COME_FROM, where).withStyle(ChatFormatting.GRAY)
        );
    }

    @RegisterLanguage("Structures come from: %s")
    static final String STRUCTURE_COME_FROM = "sftcore.structure_come_from";

    public SFTTooltipsBuilder structureComeFrom(String where) {
        return this.insert(
            Component.translatable(STRUCTURE_COME_FROM, where).withStyle(ChatFormatting.GRAY)
        );
    }

    @RegisterLanguage("* Modified By SFT *")
    static final String MODIFIED_BY_SFT = "sftcore.modified_by_sft";

    public SFTTooltipsBuilder modifiedBySFT() {
        return this.insert(
            Component.translatable(MODIFIED_BY_SFT)
                .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC, ChatFormatting.UNDERLINE)
        );
    }
}
