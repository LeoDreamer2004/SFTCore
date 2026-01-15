package org.leodreamer.sftcore.integration.emi;

import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.stream.Stream;

public interface IMultiblockInputProvider {

    void sftcore$setInputsUpdateResponser(Consumer<Stream<ItemStack>> onUpdate);
}
