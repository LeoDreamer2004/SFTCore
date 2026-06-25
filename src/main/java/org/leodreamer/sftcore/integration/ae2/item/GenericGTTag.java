package org.leodreamer.sftcore.integration.ae2.item;

import org.leodreamer.sftcore.mixin.gregtech.data.GTBucketItemAccessor;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKey;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.item.GTBucketItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import appeng.api.stacks.GenericStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * A Union for {@link TagPrefix} and {@link FluidStorageKey} to represent either an item tag or a fluid tag.
 */
public record GenericGTTag(GenericType type, String value) {

    public static final Codec<GenericGTTag> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            GenericType.CODEC.fieldOf("type").forGetter(GenericGTTag::type),
            Codec.STRING.fieldOf("value").forGetter(GenericGTTag::value)
        ).apply(instance, GenericGTTag::new)
    );

    public static final GenericGTTag EMPTY = item(TagPrefix.NULL_PREFIX);

    public enum GenericType {

        ITEM,
        FLUID;

        public static final Codec<GenericType> CODEC = Codec.STRING.xmap(
            name -> GenericType.valueOf(name.toUpperCase(Locale.ROOT)),
            type -> type.name().toLowerCase(Locale.ROOT)
        );
    }

    public static GenericGTTag item(@Nullable TagPrefix prefix) {
        if (prefix == null) {
            prefix = TagPrefix.NULL_PREFIX;
        }

        return new GenericGTTag(GenericType.ITEM, prefix.name);
    }

    public static GenericGTTag fluid(@Nullable FluidStorageKey key) {
        if (key == null) {
            return EMPTY;
        }

        return new GenericGTTag(GenericType.FLUID, key.getResourceLocation().toString());
    }

    public @Nullable TagPrefix itemTag() {
        if (type != GenericType.ITEM) {
            return null;
        }

        return GTRegistries.TAG_PREFIXES.get(GTCEu.id(value));
    }

    public @Nullable FluidStorageKey fluidTag() {
        if (type != GenericType.FLUID) {
            return null;
        }

        return FluidStorageKey.getByName(ResourceLocation.parse(value));
    }

    public String name() {
        return switch (type) {
            case ITEM -> value;
            case FLUID -> ResourceLocation.parse(value).getPath();
        };
    }

    public @Nullable GenericStack toGenericStack(Material material, int amount) {
        return switch (type) {
            case ITEM -> {
                var prefix = itemTag();

                if (prefix == null || prefix == TagPrefix.NULL_PREFIX) {
                    yield null;
                }

                yield GenericStack.fromItemStack(ChemicalHelper.get(prefix, material, amount));
            }

            case FLUID -> {
                var key = fluidTag();

                if (key == null) {
                    yield null;
                }

                var fluid = getFluidByKey(material, key);
                var stack = fluid == null ? FluidStack.EMPTY : new FluidStack(fluid, amount);
                yield GenericStack.fromFluidStack(stack);
            }
        };
    }

    @NotNull
    public ItemStack createItemOrBucket(Material material) {
        return switch (type) {
            case ITEM -> {
                var prefix = itemTag();

                if (prefix == null || prefix == TagPrefix.NULL_PREFIX) {
                    yield ItemStack.EMPTY;
                }

                yield ChemicalHelper.get(prefix, material);
            }

            case FLUID -> {
                FluidStorageKey key = fluidTag();

                if (key == null) {
                    yield ItemStack.EMPTY;
                }

                var fluid = getFluidByKey(material, key);
                yield fluid == null ? ItemStack.EMPTY : new ItemStack(fluid.getBucket());
            }
        };
    }

    public static GenericGTTag fromItemOrBucket(Item item) {
        if (item instanceof BucketItem bucket) {
            var fluid = bucket.getFluid();
            if (bucket instanceof GTBucketItem gtBucket) {
                var material = ((GTBucketItemAccessor) gtBucket).getMaterial();
                for (var key : FluidStorageKey.allKeys()) {
                    // test all fluid storage keys for a match
                    if (fluid == getFluidByKey(material, key)) {
                        return fluid(key);
                    }
                }
            }
            return fluid(FluidStorageKeys.LIQUID);
        }
        return item(ChemicalHelper.getPrefix(item));
    }

    @Nullable
    private static Fluid getFluidByKey(Material material, FluidStorageKey key) {
        try {
            return material.getProperty(PropertyKey.FLUID).getStorage().get(key);
        } catch (Exception ignored) {
            return null;
        }
    }
}
