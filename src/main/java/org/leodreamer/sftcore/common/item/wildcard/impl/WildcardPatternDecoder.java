package org.leodreamer.sftcore.common.item.wildcard.impl;

import org.leodreamer.sftcore.common.data.SFTItems;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.IPatternDetailsDecoder;
import appeng.api.stacks.AEItemKey;
import org.jetbrains.annotations.Nullable;

public class WildcardPatternDecoder implements IPatternDetailsDecoder {

    public static final WildcardPatternDecoder INSTANCE = new WildcardPatternDecoder();

    @Override
    public boolean isEncodedPattern(ItemStack stack) {
        return stack.is(SFTItems.WILDCARD_PATTERN.get());
    }

    @Override
    public @Nullable IPatternDetails decodePattern(AEItemKey what, Level level) {
        // we use our decode logic because AE only allow one pattern here
        return null;
    }

    @Override
    public @Nullable IPatternDetails decodePattern(ItemStack what, Level level, boolean tryRecovery) {
        return null;
    }
}
