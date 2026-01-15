package org.leodreamer.sftcore.common.data.lang;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;
import org.leodreamer.sftcore.integration.IntegrateMods;
import org.leodreamer.sftcore.util.RLUtils;

import com.gregtechceu.gtceu.common.data.machines.GTAEMachines;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import appeng.core.definitions.AEParts;
import de.castcrafter.travelanchors.ModItems;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@DataGenScanned
@Mod.EventBusSubscriber(modid = SFTCore.MOD_ID)
public class MixinTooltips {

    @RegisterLanguage("- Can automatically refill the blank pattern")
    public static final String PATTERN_ENCODER_0 = "sftcore.mixin.ae2.pattern_encoder.tooltip.0";

    @RegisterLanguage("- Can automatically transfer encoded pattern to assembly matrix")
    public static final String PATTERN_ENCODER_1 = "sftcore.mixin.ae2.pattern_encoder.tooltip.1";

    @RegisterLanguage("- Can multiply the number of items processed by patterns")
    public static final String PATTERN_ENCODER_2 = "sftcore.mixin.ae2.pattern_encoder.tooltip.2";

    @RegisterLanguage("- Can guess the GregTech recipe type from EMI to transfer pattern automatically")
    public static final String PATTERN_ENCODER_3 = "sftcore.mixin.ae2.pattern_encoder.tooltip.3";

    @RegisterLanguage("Prompt...")
    public static final String PATTERN_ENCODER_PROMPT = "sftcore.mixin.ae2.pattern_encoder.prompt";

    @RegisterLanguage("Change the pattern")
    public static final String CHANGE_PATTERN = "sftcore.mixin.ae2.pattern_encoding.change_pattern";

    @RegisterLanguage(
        "Multiply the pattern by %d. This will increase the item assigning speed when CPU does not have enough accelerators, and be more performance friendly for large amount processing."
    )
    public static final String MULTIPLY_PATTERN = "sftcore.mixin.ae2.pattern_encoding.multiply_pattern";

    @RegisterLanguage("Divide the pattern by %d.")
    public static final String DIVIDE_PATTERN = "sftcore.mixin.ae2.pattern_encoding.divide_pattern";

    @RegisterLanguage("Send to Assembly Matrix Enabled")
    public static final String SEND_TO_ASSEMBLY_MATRIX_ON = "sftcore.mixin.ae2.pattern_encoding.craft_to_matrix_enabled";

    @RegisterLanguage("The encoded pattern will be automatically sent to the assembly matrix (if possible)")
    public static final String SEND_TO_ASSEMBLY_MATRIX_DESC_ENABLED = "sftcore.mixin.ae2.pattern_encoding.craft_to_matrix_enabled.tooltip";

    @RegisterLanguage("Send to Assembly Matrix Disabled")
    public static final String SEND_TO_ASSEMBLY_MATRIX_OFF = "sftcore.mixin.ae2.pattern_encoding.craft_to_matrix_disabled";

    @RegisterLanguage("The encoded pattern will not be automatically sent to the assembly matrix")
    public static final String SEND_TO_ASSEMBLY_MATRIX_DESC_DISABLED = "sftcore.mixin.ae2.pattern_encoding.craft_to_matrix_disabled.tooltip";

    @RegisterLanguage("This pattern is already craftable")
    public static final String ALREADY_CRAFTABLE = "sftcore.mixin.ae2.pattern_encoding.already_craftable";

    @RegisterLanguage("Do not have the cooldown time")
    public static final String TRAVELER_ANCHOR = "sftcore.mixin.travel_anchor.travel_staff.tooltip";

    @RegisterLanguage("Also available in the void")
    public static final String AVAILABLE_IN_VOID = "sftcore.mixin.gtceu.condition.demension.available_in_void";

    @RegisterLanguage("Select Target: %d, %d, %d")
    public static final String WIRELESS_SELECTED = "sftcore.mixin.extended_ae.wireless_connector.selected";

    @RegisterLanguage("The machine is connected successfully.")
    public static final String WIRELESS_CONNECT = "sftcore.mixin.extended_ae.wireless_connector.connect";

    @RegisterLanguage("The wireless controller is missing.")
    public static final String WIRELESS_MISSING = "sftcore.mixin.extended_ae.wireless_connector.missing";

    @RegisterLanguage("Hold a (certus) quartz knife in the offhand to enable the cut-paste mode")
    public static final String MEMORY_CARD_CUT_MODE = "sftcore.mixin.ae2.memory_card.cut_mode.tooltip";

    @RegisterLanguage("Push TAB to autocraft")
    public static final String EMI_AUTOCRAFT = "sftcore.mixin.emi.autocraft";

    @RegisterLanguage("- Has been optimized to automatically scale the patterns when crafting")
    public static final String PATTERN_BUFFER_0 = "sftcore.mixin.ae2.pattern_buffer.tooltip.0";

    @RegisterLanguage("- Compatible with the Wildcard Pattern")
    public static final String PATTERN_BUFFER_1 = "sftcore.mixin.ae2.pattern_buffer.tooltip.1";

    private static final Lazy<Object2ObjectMap<Item, SFTTooltipsBuilder>> TOOLTIPS = Lazy.of(MixinTooltips::build);

    private static Object2ObjectMap<Item, SFTTooltipsBuilder> build() {
        var map = new Object2ObjectOpenHashMap<Item, SFTTooltipsBuilder>();

        var patternEncoders = new Item[] {
            AEParts.PATTERN_ENCODING_TERMINAL.asItem(),
            RLUtils.getItemById(IntegrateMods.AE2WT, "wireless_pattern_encoding_terminal"),
            RLUtils.getItemById(IntegrateMods.AE2WT, "wireless_universal_terminal"),
        };

        for (var patternEncoder : patternEncoders) {
            map.put(
                patternEncoder,
                SFTTooltipsBuilder.of()
                    .insert(Component.translatable(PATTERN_ENCODER_0).withStyle(ChatFormatting.GOLD))
                    .insert(Component.translatable(PATTERN_ENCODER_1).withStyle(ChatFormatting.GOLD))
                    .insert(Component.translatable(PATTERN_ENCODER_2).withStyle(ChatFormatting.GOLD))
                    .insert(Component.translatable(PATTERN_ENCODER_3).withStyle(ChatFormatting.GOLD))
                    .modifiedBySFT()
            );
        }

        map.put(
            ModItems.travelStaff.asItem(),
            SFTTooltipsBuilder.of()
                .insert(Component.translatable(TRAVELER_ANCHOR).withStyle(ChatFormatting.AQUA))
                .modifiedBySFT()
        );

        map.put(
            GTAEMachines.ME_PATTERN_BUFFER.getItem(),
            SFTTooltipsBuilder.of()
                .insert(Component.translatable(PATTERN_BUFFER_0).withStyle(ChatFormatting.GOLD))
                .insert(Component.translatable(PATTERN_BUFFER_1).withStyle(ChatFormatting.GOLD))
                .modifiedBySFT()
        );

        return map;
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        var tooltip = event.getToolTip();
        var builder = TOOLTIPS.get().get(event.getItemStack().getItem());
        if (builder != null) builder.addTo(tooltip);
    }
}
