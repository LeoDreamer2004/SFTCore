package org.leodreamer.sftcore.common.data.lang;

import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.data.lang.LangHandler;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.createmod.ponder.foundation.PonderIndex;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;
import org.leodreamer.sftcore.integration.ponder.SFTPonderPlugin;

public class SFTLangHandler extends LangHandler {
    public static void init(RegistrateLangProvider provider) {
        // recipe types autogen
        // I wonder why gt cannot generate them for me...
        for (var k : GTRegistries.RECIPE_TYPES.keys()) {
            if (k.getNamespace().equals(SFTCore.MOD_ID)) {
                provider.add("%s.%s".formatted(SFTCore.MOD_ID, k.getPath()),
                        RegistrateLangProvider.toEnglishName(k.getPath()));
            }
        }

        for (var entry : SFTTooltipsBuilder.TOOLTIPS_REGISTRATE.entrySet()) {
            provider.add(entry.getKey(), entry.getValue());
        }

        LanguageScanner.scan(provider);

        PonderIndex.addPlugin(new SFTPonderPlugin());
        PonderIndex.getLangAccess().provideLang(SFTCore.MOD_ID, provider::add);

        //// Cover the tooltip of GTCEu ////

        // circuit
        provider.add("item.gtceu.basic_integrated_circuit.tooltip.0", "§7Cheaper Circuits");
        provider.add("item.gtceu.good_integrated_circuit.tooltip.0", "§7Smaller and More Powerful");
        provider.add("item.gtceu.advanced_integrated_circuit.tooltip.0", "§7Integrated Manufacturing, Trustworthy and Reliable");

        provider.add("item.gtceu.micro_processor.tooltip.0", "§7Transistors Packed Tight");
        provider.add("item.gtceu.micro_processor_assembly.tooltip.0", "§7Exquisitely Crafted");
        provider.add("item.gtceu.micro_processor_computer.tooltip.0", "§7Intricately Interconnected Circuits");
        provider.add("item.gtceu.micro_processor_mainframe.tooltip.0", "§7Amazing Computation Speed!");

        provider.add("item.gtceu.nano_processor.tooltip.0", "§7Advanced Nanotechnology");
        provider.add("item.gtceu.nano_processor_assembly.tooltip.0", "§7Smaller than Ever");
        provider.add("item.gtceu.nano_processor_computer.tooltip.0", "§7Process Nearing Picometer");
        provider.add("item.gtceu.nano_processor_mainframe.tooltip.0", "§7The Limit of Moore's Law");

        provider.add("item.gtceu.quantum_processor.tooltip.0", "§7Quantum Superposition");
        provider.add("item.gtceu.quantum_processor_assembly.tooltip.0", "§7Around Thirty Thousand Hadamard Gates");
        provider.add("item.gtceu.quantum_processor_computer.tooltip.0", "§7Error Rate Less Than One in a Billion");
        provider.add("item.gtceu.quantum_processor_mainframe.tooltip.0", "§7Quantum Computing Comes to Life!");

        provider.add("item.gtceu.crystal_processor.tooltip.0", "§7Taking Advantage of Crystal Engraving");
        provider.add("item.gtceu.crystal_processor_assembly.tooltip.0", "§7As If You Can Hear the Crystals Grow");
        provider.add("item.gtceu.crystal_processor_computer.tooltip.0", "§7Perfect Unit Cells");
        provider.add("item.gtceu.crystal_processor_mainframe.tooltip.0", "§7The Strongest Inorganic Creation");

        // machines
        provider.add("gtceu.dummy", "Null");
        provider.add("gtceu.machine.cracker.tooltip.1", "- §7For every 1 level above §6Cupronickel§7, recipe energy consumption is reduced by 10%%");
    }
}
