package org.leodreamer.sftcore.common.item.terminal;

import org.leodreamer.sftcore.common.item.terminal.builder.IMekMultiblockBuilder;
import org.leodreamer.sftcore.common.item.terminal.builder.impl.*;
import org.leodreamer.sftcore.common.item.terminal.gui.MekTerminalTab;
import org.leodreamer.sftcore.common.item.terminal.gui.impl.*;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class MekBuilderRegistry {

    /**
     * Builder's id -> Registry entry
     */
    private static final Map<ResourceLocation, Entry<?>> ENTRIES = new LinkedHashMap<>();

    // spotless:off
    @SuppressWarnings("unused")
    public static final Entry<?>
        INDUCTION_MATRIX = register(InductionMatrixBuilder::new, InductionMatrixTab::new),
        THERMAL_EVAPORATION = register(ThermalEvaporationBuilder::new, ThermalEvaporationTab::new),
        SUPERCRITICAL_PHASE_SHIFTER = register(SPSBuilder::new, SPSTab::new),
        FUSION_REACTOR = register(FusionReactorBuilder::new, FusionReactorTab::new),
        THERMAL_BOILER = register(ThermalBoilerBuilder::new, ThermalBoilerTab::new),
        INDUSTRIAL_TURBINE = register(IndustrialTurbineBuilder::new, IndustrialTurbineTab::new);
    // spotless:on

    public static <
        T extends IMekMultiblockBuilder> Entry<T> register(Supplier<T> builderSupplier, TabFactory<T> tabFactory) {
        var builder = builderSupplier.get();
        var entry = new Entry<>(builder, tabFactory);
        ENTRIES.put(builder.id(), entry);
        return entry;
    }

    public static Collection<Entry<?>> entries() {
        return ENTRIES.values();
    }

    public static void setSelected(ItemStack terminal, IMekMultiblockBuilder builder) {
        terminal.getOrCreateTag().putString(MekTerminalTags.SELECTED, builder.id().toString());
    }

    public static Entry<?> selected(ItemStack terminal) {
        var selected = terminal.getOrCreateTag().getString(MekTerminalTags.SELECTED);

        if (!selected.isEmpty()) {
            var id = ResourceLocation.tryParse(selected);
            if (id != null) {
                var entry = ENTRIES.get(id);
                if (entry != null) {
                    return entry;
                }
            }
        }
        return INDUCTION_MATRIX; // fallback
    }

    public record Entry<T extends IMekMultiblockBuilder>(T builder, TabFactory<T> tabFactory) {

        public ResourceLocation id() {
            return builder.id();
        }

        public MekTerminalTab<T> createTab(
            ItemStack terminal, Consumer<ItemStack> onSave
        ) {
            return tabFactory.create(builder, terminal, onSave);
        }
    }

    @FunctionalInterface
    public interface TabFactory<T extends IMekMultiblockBuilder> {

        MekTerminalTab<T> create(T builder, ItemStack terminal, Consumer<ItemStack> onSave);
    }
}
