package org.leodreamer.sftcore.api.recipe.content;

import com.gregtechceu.gtceu.api.recipe.content.IContentSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.resources.ResourceLocation;

public class SerializerGasStack implements IContentSerializer<GasStack> {
    public static final SerializerGasStack INSTANCE = new SerializerGasStack();

    private static final Codec<GasStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("gas").forGetter(GasStack::getTypeRegistryName),
        Codec.LONG.fieldOf("amount").forGetter(GasStack::getAmount)
    ).apply(instance, SerializerGasStack::fromId));

    private static GasStack fromId(ResourceLocation id, long amount) {
        var gas = MekanismAPI.gasRegistry().getValue(id);
        if (gas == null || gas.isEmptyType() || amount <= 0) {
            return GasStack.EMPTY;
        }
        return new GasStack(gas, amount);
    }

    @Override
    public GasStack of(Object o) {
        if (o instanceof GasStack stack) {
            return stack.copy();
        }
        throw new IllegalArgumentException("Cannot convert object to GasStack: " + o);
    }

    @Override
    public GasStack defaultValue() {
        return GasStack.EMPTY;
    }

    @Override
    public Class<GasStack> contentClass() {
        return GasStack.class;
    }

    @Override
    public Codec<GasStack> codec() {
        return CODEC;
    }
}
