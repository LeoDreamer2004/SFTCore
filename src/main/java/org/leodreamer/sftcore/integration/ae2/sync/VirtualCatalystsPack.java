package org.leodreamer.sftcore.integration.ae2.sync;

import org.leodreamer.sftcore.util.RLUtils;

import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record VirtualCatalystsPack(List<ItemPack> items) {

    public record ItemPack(ResourceLocation item, String tag) {

        private static ItemPack pack(ItemStack stack) {
            return new ItemPack(RLUtils.getItemRL(stack.getItem()),
                stack.hasTag() ? stack.getTag().toString() : "");
        }

        private ItemStack unpack() {
            var value = RLUtils.getItemByRL(item);
            if (value == null) return ItemStack.EMPTY;
            var stack = new ItemStack(value);
            if (!tag.isEmpty()) {
                try {
                    stack.setTag(TagParser.parseTag(tag));
                } catch (Exception ignored) {}
            }
            return stack;
        }
    }

    public static VirtualCatalystsPack pack(List<ItemStack> stacks) {
        return new VirtualCatalystsPack(stacks.stream().map(ItemPack::pack).toList());
    }

    public List<ItemStack> unpack() {
        return items.stream().map(ItemPack::unpack).filter(stack -> !stack.isEmpty()).toList();
    }
}
