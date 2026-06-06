package org.leodreamer.sftcore.common.item.wildcard.feature;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Predicate;

@DataGenScanned
public interface IWildcardFilterComponent extends Predicate<Material>, IWildcardComponentUI {

    boolean isWhitelist();

    void setWhitelist(boolean whiteList);

    @RegisterLanguage("[Whitelist]")
    String WHITELIST_KEY = "item.sftcore.wildcard_pattern.tooltip.filter.whitelist";

    @RegisterLanguage("[Blacklist]")
    String BLACKLIST_KEY = "item.sftcore.wildcard_pattern.tooltip.filter.blacklist";

    default MutableComponent whitelistTooltip() {
        return Component.translatable(isWhitelist() ? WHITELIST_KEY : BLACKLIST_KEY)
            .withStyle(ChatFormatting.GRAY);
    }
}
