package org.leodreamer.sftcore.common.data.lang;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.registry.SFTTooltipsBuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import appeng.core.definitions.AEParts;
import de.castcrafter.travelanchors.ModItems;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.List;
import java.util.function.Consumer;

@DataGenScanned
@Mod.EventBusSubscriber(modid = SFTCore.MOD_ID)
public class MixinTooltips {

    @RegisterLanguage("- Can automatically refill the blank pattern")
    public static final String PATTERN_ENCODER_0 = "sftcore.mixin.ae2.pattern_encoder.tooltip.0";

    @RegisterLanguage("- Can automatically transfer encoded pattern to assembly matrix")
    public static final String PATTERN_ENCODER_1 = "sftcore.mixin.ae2.pattern_encoder.tooltip.1";

    @RegisterLanguage("- Can multiply the number of items processed by patterns")
    public static final String PATTERN_ENCODER_2 = "sftcore.mixin.ae2.pattern_encoder.tooltip.2";

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

    @RegisterLanguage("Do not have the cooldown time")
    public static final String TRAVELER_ANCHOR = "sftcore.mixin.travel_anchor.travel_staff.tooltip";

    @RegisterLanguage("Also available in the void")
    public static final String AVAILABLE_IN_VOID = "sftcore.mixin.gtceu.condition.demension.available_in_void";

    private static final Object2ObjectMap<Item, Consumer<List<Component>>> TOOLTIPS = new Object2ObjectOpenHashMap<>();

    static {
        TOOLTIPS.put(
            AEParts.PATTERN_ENCODING_TERMINAL.asItem(),
            SFTTooltipsBuilder.of()
                .insert(Component.translatable(PATTERN_ENCODER_0).withStyle(ChatFormatting.GOLD))
                .insert(Component.translatable(PATTERN_ENCODER_1).withStyle(ChatFormatting.GOLD))
                .insert(Component.translatable(PATTERN_ENCODER_2).withStyle(ChatFormatting.GOLD))
                .modifiedBySFT()::addTo
        );

        TOOLTIPS.put(
            ModItems.travelStaff.asItem(),
            SFTTooltipsBuilder.of()
                .insert(Component.translatable(TRAVELER_ANCHOR).withStyle(ChatFormatting.AQUA))
                .modifiedBySFT()::addTo
        );
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        var tooltip = event.getToolTip();
        var consumer = TOOLTIPS.get(event.getItemStack().getItem());
        if (consumer != null) consumer.accept(tooltip);
    }
}
