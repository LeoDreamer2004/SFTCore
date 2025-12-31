package org.leodreamer.sftcore.integration.ae2.item;

import org.leodreamer.sftcore.util.ReflectUtils;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKey;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.api.item.GTBucketItem;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import appeng.api.stacks.GenericStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.leodreamer.sftcore.integration.ae2.item.GenericGTTag.GenericType.FLUID;
import static org.leodreamer.sftcore.integration.ae2.item.GenericGTTag.GenericType.ITEM;

public class GenericGTTag {

    private final TagPrefix itemTag;
    private final FluidStorageKey fluidTag;
    private final GenericType type;

    public enum GenericType {

        ITEM("item"),
        FLUID("fluid");

        public final String key;

        GenericType(String key) {
            this.key = key;
        }
    }

    public static final GenericGTTag EMPTY = item(TagPrefix.NULL_PREFIX);

    private GenericGTTag(GenericType type, TagPrefix itemTag, FluidStorageKey fluidTag) {
        this.type = type;
        this.itemTag = itemTag;
        this.fluidTag = fluidTag;
    }

    public static GenericGTTag item(TagPrefix itemTag) {
        return new GenericGTTag(ITEM, itemTag, null);
    }

    public static GenericGTTag fluid(FluidStorageKey fluidTag) {
        return new GenericGTTag(FLUID, null, fluidTag);
    }

    public String name() {
        if (type == ITEM) {
            return itemTag.name;
        } else {
            return fluidTag.getResourceLocation().getPath();
        }
    }

    public GenericStack toGenericStack(Material material, int amount) {
        if (type == ITEM) {
            var item = ChemicalHelper.get(itemTag, material, amount);
            return GenericStack.fromItemStack(item);
        } else {
            var fluid = getFluidByKey(material, fluidTag);
            var stack = fluid == null ? FluidStack.EMPTY : new FluidStack(fluid, amount);
            return GenericStack.fromFluidStack(stack);
        }
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        if (type == ITEM) {
            tag.putString(ITEM.key, itemTag.name);
        } else {
            tag.putString(FLUID.key, fluidTag.getResourceLocation().toString());
        }
        return tag;
    }

    public static GenericGTTag fromNBT(CompoundTag tag) {
        if (tag.contains(ITEM.key)) {
            var item = TagPrefix.get(tag.getString(ITEM.key));
            return item(item);
        } else if (tag.contains(FLUID.key)) {
            var rl = ResourceLocation.parse(tag.getString(FLUID.key));
            var fluid = FluidStorageKey.getByName(rl);
            return fluid(fluid);
        }
        return EMPTY;
    }

    @NotNull
    public ItemStack createItemOrBucket(Material material) {
        if (type == ITEM) {
            return ChemicalHelper.get(itemTag, material);
        } else {
            var fluid = getFluidByKey(material, fluidTag);
            if (fluid == null) return ItemStack.EMPTY;
            return new ItemStack(fluid.getBucket());
        }
    }

    public static GenericGTTag fromItemOrBucket(Item item) {
        if (item instanceof BucketItem bucket) {
            var fluid = bucket.getFluid();
            if (bucket instanceof GTBucketItem gtBucket) {
                var material = ReflectUtils.getFieldValue(gtBucket, "material", Material.class);
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
    private static Fluid getFluidByKey(Material material, FluidStorageKey tag) {
        try {
            return material.getProperty(PropertyKey.FLUID).getStorage().get(tag);
        } catch (Exception ignored) {
            return null;
        }
    }
}
