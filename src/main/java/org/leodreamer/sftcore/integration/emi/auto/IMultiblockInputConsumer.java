package org.leodreamer.sftcore.integration.emi.auto;

import net.minecraft.world.item.ItemStack;

import java.util.stream.Stream;

public interface IMultiblockInputConsumer {

    void sftcore$onInputUpdated(Stream<ItemStack> updated);
}
