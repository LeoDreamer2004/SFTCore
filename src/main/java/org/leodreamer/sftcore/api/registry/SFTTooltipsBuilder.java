package org.leodreamer.sftcore.api.registry;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.common.data.recipe.SFTRecipeModifiers;

import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.utils.FormattingUtil;

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

    public static Object2ObjectArrayMap<String, String> TOOLTIPS_REGISTRATE = new Object2ObjectArrayMap<>();

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
        TOOLTIPS_REGISTRATE.put(key, tooltip); // the tooltip is auto-shown

        return this;
    }

    public SFTTooltipsBuilder intro(String... contents) {
        if (this.id == null) {
            throw new IllegalStateException("Cannot insert an intro tooltip without a machine");
        }

        List<String> keys = new ArrayList<>();

        for (int i = 0; i < contents.length; i++) {
            var key = id.getNamespace() + ".machine." + id.getPath() + ".tooltip." + i;
            TOOLTIPS_REGISTRATE.put(key, contents[i]);
            keys.add(key);
        }
        tooltips.addAll(keys.stream().map(Component::translatable).toList());

        return this;
    }

    @RegisterLanguage("§2- §lBlast Furnace Coil Bonus: §7§oFor every voltage tier above §bMV§7§o, temperature is increased by §r100K.")
    static final String EBF_0 = "gtceu.machine.electric_blast_furnace.tooltip.0";

    @RegisterLanguage("§7§o   For every §f900K§7§o above the recipe temperature, energy consumption is reduced by §f5%%.§r")
    static final String EBF_1 = "gtceu.machine.electric_blast_furnace.tooltip.1";

    @RegisterLanguage("§7§o   For every §f1800K§7§o above the recipe temperature, one perfect overclock is granted.§r")
    static final String EBF_2 = "gtceu.machine.electric_blast_furnace.tooltip.2";

    public SFTTooltipsBuilder ebf() {
        return this.insert(
                Component.translatable(EBF_0),
                Component.translatable(EBF_1),
                Component.translatable(EBF_2));
    }

    @RegisterLanguage("§co §lParallelizable§r")
    static final String PARALLELIZABLE = "gtceu.multiblock.parallelizable.tooltip";

    public SFTTooltipsBuilder parallelizable() {
        return this.insert(Component.translatable(PARALLELIZABLE));
    }

    @RegisterLanguage("§e- Sharing: §a✔§r")
    static final String ENABLE_SHARING = "gtceu.part_sharing.enabled";

    public SFTTooltipsBuilder enableSharing() {
        return this.insert(Component.translatable(ENABLE_SHARING));
    }

    @RegisterLanguage("§e- Sharing: §4✘§r")
    static final String DISABLE_SHARING = "gtceu.part_sharing.disabled";

    public SFTTooltipsBuilder disableSharing() {
        return this.insert(Component.translatable(DISABLE_SHARING));
    }

    @RegisterLanguage("§a- §lEnergy Multiplier§r§a: %s§r")
    static final String ENERGY_MULTIPLIER = "sftcore.multiblock.energy_multiplier.tooltip";

    public SFTTooltipsBuilder energyMultiplier(double multiplier) {
        return this.insert(
                Component.translatable(ENERGY_MULTIPLIER, FormattingUtil.formatNumbers(multiplier)));
    }

    @RegisterLanguage("§9- §lTime Multiplier§r§9: %s§r")
    static final String TIME_MULTIPLIER = "sftcore.multiblock.time_multiplier.tooltip";

    public SFTTooltipsBuilder timeMultiplier(double multiplier) {
        return this.insert(
                Component.translatable(TIME_MULTIPLIER, FormattingUtil.formatNumbers(multiplier)));
    }

    @RegisterLanguage("§6o §lPerfect Overclock§r")
    static final String PERFECT_OVERLOCK = "sftcore.multiblock.perfect_overclock.tooltip";

    @RegisterLanguage("§7§o   Increase power casually without increasing total energy consumption§r")
    static final String PERFECT_OVERLOCK_1 = "sftcore.multiblock.perfect_overclock.tooltip.1";

    public SFTTooltipsBuilder perfectOverlock() {
        return this.insert(
                Component.translatable(PERFECT_OVERLOCK), Component.translatable(PERFECT_OVERLOCK_1));
    }

    @RegisterLanguage("§do §lHalf Perfect Overclock§r")
    static final String HALF_PERFECT_OVERLOCK = "sftcore.multiblock.half_perfect_overclock.tooltip";

    @RegisterLanguage("§7§o   Process 3 times faster when overclocked with 4 times power§r")
    static final String HALF_PERFECT_OVERLOCK_1 = "sftcore.multiblock.half_perfect_overclock.tooltip.1";

    public SFTTooltipsBuilder halfPerfectOverlock() {
        return this.insert(
                Component.translatable(HALF_PERFECT_OVERLOCK),
                Component.translatable(HALF_PERFECT_OVERLOCK_1));
    }

    @RegisterLanguage("§eo §lLaser Hatch: §a✔§r")
    static final String ALLOW_LASER = "sftcore.multiblock.allow_laser";

    @RegisterLanguage("§7§o   Laser hatch can provide huge energy, and must be used together with a normal energy hatch.")
    static final String ALLOW_LASER_1 = "sftcore.multiblock.allow_laser.1";

    public SFTTooltipsBuilder allowLaser() {
        return this.insert(Component.translatable(ALLOW_LASER), Component.translatable(ALLOW_LASER_1));
    }

    public SFTTooltipsBuilder gcymReduce() {
        return this.energyMultiplier(SFTRecipeModifiers.GCYM_EUT_MULTIPLIER)
                .timeMultiplier(SFTRecipeModifiers.GCYM_DURATION_MULTIPLIER);
    }

    @RegisterLanguage("§co §lCoil Discount§r")
    static final String MEGA_REDUCE_WITH_COIL = "sftcore.multiblock.mega_reduce_with_coil";

    @RegisterLanguage("§7§o   For every §d%dK§7§o above coil temperature, recipe energy is multiplied by §a%s§r§7§o and time by §9%s§r")
    static final String MEGA_REDUCE_WITH_COIL_1 = "sftcore.multiblock.mega_reduce_with_coil.1";

    public SFTTooltipsBuilder megaReduceWithCoil() {
        return this.insert(
                Component.translatable(MEGA_REDUCE_WITH_COIL),
                Component.translatable(
                        MEGA_REDUCE_WITH_COIL_1,
                        SFTRecipeModifiers.MEGA_COIL_TEMP_LEVEL,
                        FormattingUtil.formatNumbers(SFTRecipeModifiers.MEGA_COIL_DURATION_MULTIPLIER),
                        FormattingUtil.formatNumbers(SFTRecipeModifiers.MEGA_COIL_EUT_MULTIPLIER)));
    }

    @RegisterLanguage("§5Δ §lRecipe Types: %s")
    static final String AVAILABLE_RECIPE_MAP_1 = "gtceu.machine.available_recipe_map_1.tooltip";

    @RegisterLanguage("§5Δ §lRecipe Types: %s, %s")
    static final String AVAILABLE_RECIPE_MAP_2 = "gtceu.machine.available_recipe_map_2.tooltip";

    @RegisterLanguage("§5Δ §lRecipe Types: %s, %s, %s")
    static final String AVAILABLE_RECIPE_MAP_3 = "gtceu.machine.available_recipe_map_3.tooltip";

    @RegisterLanguage("§5Δ §lRecipe Types: %s, %s, %s, %s")
    static final String AVAILABLE_RECIPE_MAP_4 = "gtceu.machine.available_recipe_map_4.tooltip";

    public SFTTooltipsBuilder availableTypes(GTRecipeType... recipeTypes) {
        int number = recipeTypes.length;
        if (number == 0) {
            return this;
        }
        if (number > 4) {
            throw new IllegalArgumentException("Too many recipe types to generate a component");
        }
        return this.insert(
                Component.translatable(
                        "gtceu.machine.available_recipe_map_" + number + ".tooltip",
                        Arrays.stream(recipeTypes)
                                .map(rt -> Component.translatable(rt.registryName.toLanguageKey()))
                                .toArray()));
    }

    @RegisterLanguage("Textures come from: %s")
    static final String TEXTURE_COME_FROM = "sftcore.texture_come_from";

    public SFTTooltipsBuilder textureComeFrom(String where) {
        return this.insert(
                Component.translatable(TEXTURE_COME_FROM, where).withStyle(ChatFormatting.GRAY));
    }

    @RegisterLanguage("Structures come from: %s")
    static final String STRUCTURE_COME_FROM = "sftcore.structure_come_from";

    public SFTTooltipsBuilder structureComeFrom(String where) {
        return this.insert(
                Component.translatable(STRUCTURE_COME_FROM, where).withStyle(ChatFormatting.GRAY));
    }

    @RegisterLanguage("* Modified By SFT *")
    static final String MODIFIED_BY_SFT = "sftcore.modified_by_sft";

    public SFTTooltipsBuilder modifiedBySFT() {
        return this.insert(
                Component.translatable(MODIFIED_BY_SFT)
                        .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC, ChatFormatting.UNDERLINE));
    }
}
