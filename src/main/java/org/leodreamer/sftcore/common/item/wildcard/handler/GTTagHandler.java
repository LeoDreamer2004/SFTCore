package org.leodreamer.sftcore.common.item.wildcard.handler;

import org.leodreamer.sftcore.integration.ae2.item.GenericGTTag;

import com.gregtechceu.gtceu.api.registry.GTRegistries;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GTTagHandler extends GenericStackHandler {

    private GenericGTTag tag = GenericGTTag.EMPTY;

    public GTTagHandler(GenericGTTag tag) {
        setTag(tag);
    }

    public GenericGTTag getTag() {
        var stack = getStackInSlot(0);
        return stack.isEmpty() ? tag : GenericGTTag.fromItemOrBucket(stack.getItem());
    }

    public void setTag(GenericGTTag tag) {
        this.tag = tag;
        setStackInSlot(0, findExampleForTag(this.tag));
    }

    public static ItemStack findExampleForTag(GenericGTTag tag) {
        if (tag.equals(GenericGTTag.EMPTY)) {
            return ItemStack.EMPTY;
        }
        for (var material : GTRegistries.MATERIALS.values()) {
            var stack = tag.createItemOrBucket(material);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
