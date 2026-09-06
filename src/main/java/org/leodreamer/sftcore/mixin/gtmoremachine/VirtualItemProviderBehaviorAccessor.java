package org.leodreamer.sftcore.mixin.gtmoremachine;

import net.minecraft.world.item.ItemStack;

import cn.qiuye.gtmoremachine.common.item.VirtualItemProviderBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = VirtualItemProviderBehavior.class, remap = false)
public interface VirtualItemProviderBehaviorAccessor {

    @Invoker("setVirtualItem")
    static ItemStack sftcore$setVirtualItem(ItemStack stack, ItemStack virtualItem) {
        throw new AssertionError();
    }
}
