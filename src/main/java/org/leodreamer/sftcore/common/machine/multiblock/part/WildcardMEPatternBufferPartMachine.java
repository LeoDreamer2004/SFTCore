package org.leodreamer.sftcore.common.machine.multiblock.part;

import org.leodreamer.sftcore.api.feature.IMEPatternBufferCache;
import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternLogic;
import org.leodreamer.sftcore.common.item.wildcard.impl.WildcardPatternDecoder;
import org.leodreamer.sftcore.common.machine.trait.WildcardInternalSlotRecipeHandler;
import org.leodreamer.sftcore.integration.ae2.feature.HackyContainerGroupProxy;
import org.leodreamer.sftcore.integration.ae2.feature.IScaleUpCraftingProvider;
import org.leodreamer.sftcore.integration.ae2.logic.ScaledProcessingPattern;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.ButtonConfigurator;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.CircuitFancyConfigurator;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.FancyInvConfigurator;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.FancyTankConfigurator;
import com.gregtechceu.gtceu.api.machine.feature.IDataStickInteractable;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.api.sync_system.annotations.SyncToClient;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.item.behavior.IntCircuitBehaviour;
import com.gregtechceu.gtceu.integration.ae2.gui.widget.slot.AEPatternViewSlotWidget;
import com.gregtechceu.gtceu.integration.ae2.machine.MEBusPartMachine;
import com.gregtechceu.gtceu.utils.GTMath;
import com.gregtechceu.gtceu.utils.ItemStackHashStrategy;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.*;
import appeng.api.storage.StorageHelper;
import appeng.helpers.patternprovider.PatternContainer;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import it.unimi.dsi.fastutil.objects.Object2LongOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Wildcard version for {@link com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferPartMachine},
 * as mixin the pattern buffer with wildcard pattern is too tricky and hard to maintain and optimize
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WildcardMEPatternBufferPartMachine extends MEBusPartMachine
    implements IScaleUpCraftingProvider, PatternContainer, IDataStickInteractable, IMEPatternBufferCache {

    private final InternalInventory internalPatternInventory = new InternalInventory() {

        @Override
        public int size() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slotIndex) {
            return patternInventory.getStackInSlot(slotIndex);
        }

        @Override
        public void setItemDirect(int slotIndex, ItemStack stack) {
            patternInventory.setStackInSlot(slotIndex, stack);
            patternInventory.onContentsChanged(slotIndex);
            onPatternChange(slotIndex);
        }
    };

    @Getter
    @SaveField
    @SyncToClient
    private final CustomItemStackHandler patternInventory = new CustomItemStackHandler(1);

    @Getter
    @SaveField
    protected final NotifiableItemStackHandler shareInventory;

    @Getter
    @SaveField
    protected final NotifiableFluidTank shareTank;

    /**
     * One internal slot per generated pattern.
     */
    @Getter
    @SaveField
    protected InternalSlot[] internalInventory = new InternalSlot[0];

    /**
     * Stable ordered generated pattern list.
     */
    private volatile List<IPatternDetails> wildcardDetails = List.of();

    /**
     * Generated pattern -> internal slot index.
     */
    private final Map<IPatternDetails, Integer> wildcardDetailsSlotMap = new ConcurrentHashMap<>();

    @Getter
    protected final WildcardInternalSlotRecipeHandler internalRecipeHandler;

    @Nullable
    protected TickableSubscription updateSubs;

    private boolean needPatternSync;

    /**
     * One cached recipe per generated pattern / internal slot.
     */
    private GTRecipe[] cachedRecipes = new GTRecipe[0];

    public WildcardMEPatternBufferPartMachine(BlockEntityCreationInfo info) {
        super(info, IO.IN);

        patternInventory.setOnContentsChanged(
            () -> getSyncDataHolder().markClientSyncFieldDirty("patternInventory")
        );

        patternInventory.setFilter(WildcardPatternDecoder.INSTANCE::isEncodedPattern);

        getMainNode().addService(ICraftingProvider.class, this);

        this.shareInventory = attachTrait(new NotifiableItemStackHandler(9, IO.IN, IO.NONE));
        this.shareTank = attachTrait(new NotifiableFluidTank(9, 8 * FluidType.BUCKET_VOLUME, IO.IN, IO.NONE));
        this.internalRecipeHandler = new WildcardInternalSlotRecipeHandler(this);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (!isRemote()) {
            rebuildWildcardPatterns();
            needPatternSync = true;
        }
    }

    public void onPatternChange(int index) {
        if (isRemote()) {
            return;
        }
        if (index != 0) {
            return;
        }
        refundAllInternalSlots();
        clearAllCachedRecipes();

        var generatedPatterns = collectWildcardPatterns();
        installGeneratedPatterns(generatedPatterns, false);

        needPatternSync = true;
        markAsDirty();
    }

    private void rebuildWildcardPatterns() {
        var generatedPatterns = collectWildcardPatterns();

        // Preserve already deserialized internal contents on world load
        installGeneratedPatterns(generatedPatterns, true);
        clearAllCachedRecipes();
    }

    private List<IPatternDetails> collectWildcardPatterns() {
        var stack = patternInventory.getStackInSlot(0);
        if (stack.isEmpty() || !WildcardPatternDecoder.INSTANCE.isEncodedPattern(stack)) {
            return List.of();
        }

        return WildcardPatternLogic.on(stack)
            .generateAllPatterns(getLevel(), true)
            .toList();
    }

    private void installGeneratedPatterns(List<IPatternDetails> generatedPatterns, boolean preserveInternalContents) {
        int size = generatedPatterns.size();

        resizeInternalInventory(size, preserveInternalContents);
        resizeCachedRecipes(size);
        internalRecipeHandler.setActiveSize(size);

        wildcardDetailsSlotMap.clear();

        for (int i = 0; i < generatedPatterns.size(); i++) {
            wildcardDetailsSlotMap.put(generatedPatterns.get(i), i);
        }

        wildcardDetails = List.copyOf(generatedPatterns);
    }

    private void resizeInternalInventory(int size, boolean preserveInternalContents) {
        var oldSlots = internalInventory;
        var newSlots = new InternalSlot[size];

        for (int i = 0; i < size; i++) {
            if (preserveInternalContents && i < oldSlots.length && oldSlots[i] != null) {
                newSlots[i] = oldSlots[i];
            } else {
                newSlots[i] = new InternalSlot();
            }

            int slotIndex = i;
            newSlots[i].setOnContentsChanged(() -> {
                internalRecipeHandler.notifySlotChanged(slotIndex);
                markAsDirty();
            });
        }

        internalInventory = newSlots;
    }

    private void resizeCachedRecipes(int size) {
        cachedRecipes = new GTRecipe[size];
    }

    private void clearAllCachedRecipes() {
        Arrays.fill(cachedRecipes, null);
    }

    private void refundAllInternalSlots() {
        for (var internalSlot : internalInventory) {
            if (internalSlot != null) {
                internalSlot.refund();
            }
        }
    }

    @Override
    public List<RecipeHandlerList> getRecipeHandlers() {
        return internalRecipeHandler.getSlotHandlers();
    }

    @Override
    public boolean isWorkingEnabled() {
        return true;
    }

    @Override
    public void setWorkingEnabled(boolean ignored) {}

    @Override
    public boolean isDistinct() {
        return true;
    }

    @Override
    public void setDistinct(boolean ignored) {}

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        super.onMainNodeStateChanged(reason);
        updateSubscription();
    }

    protected void updateSubscription() {
        if (getMainNode().isOnline()) {
            updateSubs = subscribeServerTick(updateSubs, this::update);
        } else if (updateSubs != null) {
            updateSubs.unsubscribe();
            updateSubs = null;
        }
    }

    protected void update() {
        if (needPatternSync) {
            ICraftingProvider.requestUpdate(getMainNode());
            needPatternSync = false;
        }
    }

    private void refundAll(ClickData clickData) {
        if (!clickData.isRemote) {
            refundAllInternalSlots();
            clearAllCachedRecipes();
        }
    }

    @Override
    public void attachConfigurators(ConfiguratorPanel configuratorPanel) {
        configuratorPanel.attachConfigurators(
            new ButtonConfigurator(
                new GuiTextureGroup(GuiTextures.BUTTON, GuiTextures.REFUND_OVERLAY),
                this::refundAll
            ).setTooltips(List.of(Component.translatable("gui.gtceu.refund_all.desc")))
        );

        if (isHasCircuitSlot() && isCircuitSlotEnabled()) {
            configuratorPanel.attachConfigurators(new CircuitFancyConfigurator(circuitInventory.storage));
        }

        configuratorPanel.attachConfigurators(
            new FancyInvConfigurator(
                shareInventory.storage,
                Component.translatable("gui.gtceu.share_inventory.title")
            ).setTooltips(
                List.of(
                    Component.translatable("gui.gtceu.share_inventory.desc.0"),
                    Component.translatable("gui.gtceu.share_inventory.desc.1")
                )
            )
        );

        configuratorPanel.attachConfigurators(
            new FancyTankConfigurator(
                shareTank.getStorages(),
                Component.translatable("gui.gtceu.share_tank.title")
            ).setTooltips(
                List.of(
                    Component.translatable("gui.gtceu.share_tank.desc.0"),
                    Component.translatable("gui.gtceu.share_inventory.desc.1")
                )
            )
        );
    }

    @Override
    public Widget createUIWidget() {
        int rowSize = 9;
        int colSize = 3;

        var group = new WidgetGroup(0, 0, 18 * rowSize + 16, 18 * colSize + 16);

        var slot = new AEPatternViewSlotWidget(patternInventory, 0, 80, 32) {

            @Override
            public ItemStack slotClick(int dragType, ClickType clickTypeIn, Player player) {
                if (!player.level().isClientSide) {
                    clearAllCachedRecipes();
                }
                return super.slotClick(dragType, clickTypeIn, player);
            }
        };

        slot.setOccupiedTexture(GuiTextures.SLOT)
            .setChangeListener(() -> onPatternChange(0))
            .setBackground(GuiTextures.SLOT, GuiTextures.PATTERN_OVERLAY);

        group.addWidget(slot);

        group.addWidget(
            new LabelWidget(
                8,
                2,
                () -> this.isOnline ? "gtceu.gui.me_network.online" : "gtceu.gui.me_network.offline"
            )
        );

        return group;
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        return new ArrayList<>(wildcardDetails);
    }

    @Override
    public boolean pushPattern(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
        if (!isFormed() || !getMainNode().isActive() || !checkInput(inputHolder)) {
            return false;
        }

        int slotIndex = getSlotIndexForPattern(patternDetails);
        if (slotInvalid(slotIndex)) {
            return false;
        }

        internalInventory[slotIndex].pushPattern(patternDetails, inputHolder);
        return true;
    }

    private int getSlotIndexForPattern(IPatternDetails patternDetails) {
        var key = patternDetails;

        if (patternDetails instanceof ScaledProcessingPattern scaled) {
            key = scaled.original();
        }

        return wildcardDetailsSlotMap.getOrDefault(key, -1);
    }

    private boolean slotInvalid(int slot) {
        return slot < 0 || slot >= internalInventory.length || internalInventory[slot] == null;
    }

    public @Nullable InternalSlot getInternalSlot(int slot) {
        if (slotInvalid(slot)) {
            return null;
        }
        return internalInventory[slot];
    }

    @Override
    public boolean isBusy() {
        return false;
    }

    private boolean checkInput(KeyCounter[] inputHolder) {
        for (var input : inputHolder) {
            var illegal = input.keySet().stream()
                .map(AEKey::getType)
                .map(AEKeyType::getId)
                .anyMatch(
                    id -> !id.equals(AEKeyType.items().getId()) &&
                        !id.equals(AEKeyType.fluids().getId())
                );

            if (illegal) {

                return false;

            }
        }

        return true;
    }

    @Override
    public @Nullable IGrid getGrid() {
        return getMainNode().getGrid();
    }

    @Override
    public InternalInventory getTerminalPatternInventory() {
        return internalPatternInventory;
    }

    @Override
    public PatternContainerGroup getTerminalGroup() {
        PatternContainerGroup group;

        if (isFormed()) {
            var controller = getControllers().first();
            var controllerDefinition = controller.getDefinition();

            var circuitStack = isHasCircuitSlot() ? circuitInventory.storage.getStackInSlot(0) : ItemStack.EMPTY;
            int circuitConfiguration = circuitStack.isEmpty() ? -1 :
                IntCircuitBehaviour.getCircuitConfiguration(circuitStack);

            var groupName = circuitConfiguration != -1 ?
                Component.translatable(controllerDefinition.getDescriptionId()).append(" - " + circuitConfiguration) :
                Component.translatable(controllerDefinition.getDescriptionId());

            group = new PatternContainerGroup(
                AEItemKey.of(controllerDefinition.asStack()),
                groupName,
                Collections.emptyList()
            );

            return HackyContainerGroupProxy.of(group)
                .setBlockPos(controller.getBlockPos())
                .recordPartFrom(Component.translatable(getDefinition().getDescriptionId()))
                .get();
        }

        group = new PatternContainerGroup(
            AEItemKey.of(getDefinition().asStack()),
            Component.translatable(getDefinition().getDescriptionId()),
            Collections.emptyList()
        );

        return group;
    }

    @Override
    public void onMachineDestroyed() {
        patternInventory.dropInventoryInWorld(getLevel(), getBlockPos());
        shareInventory.dropInventoryInWorld();
    }

    @Override
    public InteractionResult onDataStickShiftUse(Player player, ItemStack dataStick) {
        dataStick.getOrCreateTag().putIntArray(
            "pos", new int[] {
                getBlockPos().getX(),
                getBlockPos().getY(),
                getBlockPos().getZ()
            }
        );
        return InteractionResult.SUCCESS;
    }

    @Override
    public int sftcore$getSlotCount() {
        return internalInventory.length;
    }

    @Override
    public boolean sftcore$hasInternalContent(int slot) {
        if (slotInvalid(slot)) {
            return false;
        }
        var internalSlot = internalInventory[slot];
        return !internalSlot.isItemEmpty() || !internalSlot.isFluidEmpty();
    }

    @Override
    public boolean sftcore$isSlotCached(int slot) {
        return slot >= 0 &&
            slot < cachedRecipes.length &&
            cachedRecipes[slot] != null;
    }

    @Override
    public @Nullable GTRecipe sftcore$getCachedRecipe(int slot) {
        if (slot < 0 || slot >= cachedRecipes.length) {
            return null;
        }

        return cachedRecipes[slot];
    }

    @Override
    public void sftcore$setCachedRecipe(int slot, GTRecipe recipe) {
        if (slot < 0 || slot >= cachedRecipes.length) {
            return;
        }

        cachedRecipes[slot] = recipe;
    }

    @Override
    public void sftcore$clearCachedRecipe(int slot) {
        if (slot < 0 || slot >= cachedRecipes.length) {
            return;
        }

        cachedRecipes[slot] = null;
    }

    @Override
    public @Nullable RecipeHandlerList sftcore$getSlotHandler(int slot) {
        return internalRecipeHandler.getSlotHandler(slot);
    }

    public class InternalSlot implements INBTSerializable<CompoundTag> {

        @Getter
        @Setter
        private Runnable onContentsChanged = () -> {};

        private final Object2LongOpenCustomHashMap<ItemStack> itemInventory = new Object2LongOpenCustomHashMap<>(
            ItemStackHashStrategy.comparingAllButCount()
        );

        private final Object2LongOpenHashMap<FluidStack> fluidInventory = new Object2LongOpenHashMap<>();

        private @Nullable List<ItemStack> itemStacks = null;
        private @Nullable List<FluidStack> fluidStacks = null;

        public boolean isItemEmpty() {
            return itemInventory.isEmpty();
        }

        public boolean isFluidEmpty() {
            return fluidInventory.isEmpty();
        }

        public void onContentsChanged() {
            itemStacks = null;
            fluidStacks = null;
            onContentsChanged.run();
        }

        private void add(AEKey what, long amount) {
            if (amount <= 0L) {
                return;
            }
            if (what instanceof AEItemKey itemKey) {
                var stack = itemKey.toStack();
                itemInventory.addTo(stack, amount);
            } else if (what instanceof AEFluidKey fluidKey) {
                var stack = fluidKey.toStack(1);
                fluidInventory.addTo(stack, amount);
            }
        }

        public List<ItemStack> getItems() {
            if (itemStacks == null) {
                itemStacks = new ArrayList<>();
                itemInventory.object2LongEntrySet().stream()
                    .map(e -> GTMath.splitStacks(e.getKey(), e.getLongValue()))
                    .forEach(itemStacks::addAll);
            }
            return itemStacks;
        }

        public List<FluidStack> getFluids() {
            if (fluidStacks == null) {
                fluidStacks = new ArrayList<>();
                fluidInventory.object2LongEntrySet().stream()
                    .map(e -> GTMath.splitFluidStacks(e.getKey(), e.getLongValue()))
                    .forEach(fluidStacks::addAll);
            }
            return fluidStacks;
        }

        public void refund() {
            var network = getMainNode().getGrid();
            if (network == null) {
                return;
            }
            var networkInv = network.getStorageService().getInventory();
            var energy = network.getEnergyService();

            for (var it = itemInventory.object2LongEntrySet().iterator(); it.hasNext();) {
                var entry = it.next();
                var stack = entry.getKey();
                var count = entry.getLongValue();

                if (stack.isEmpty() || count == 0) {
                    it.remove();
                    continue;
                }

                var key = AEItemKey.of(stack);
                if (key == null) continue;

                long inserted = StorageHelper.poweredInsert(energy, networkInv, key, count, actionSource);
                if (inserted > 0) {
                    count -= inserted;
                    if (count == 0) it.remove();
                    else entry.setValue(count);
                }
            }

            for (var it = fluidInventory.object2LongEntrySet().iterator(); it.hasNext();) {
                var entry = it.next();
                var stack = entry.getKey();
                var amount = entry.getLongValue();

                if (stack.isEmpty() || amount == 0) {
                    it.remove();
                    continue;
                }

                var key = AEFluidKey.of(stack);
                if (key == null) continue;

                long inserted = StorageHelper.poweredInsert(energy, networkInv, key, amount, actionSource);
                if (inserted > 0) {
                    amount -= inserted;
                    if (amount == 0) it.remove();
                    else entry.setValue(amount);
                }
            }

            onContentsChanged();
        }

        public void pushPattern(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
            patternDetails.pushInputsToExternalInventory(inputHolder, this::add);
            onContentsChanged();
        }

        public @Nullable List<Ingredient> handleItemInternal(List<Ingredient> left, boolean simulate) {
            boolean changed = false;

            for (var it = left.listIterator(); it.hasNext();) {
                var ingredient = it.next();

                if (ingredient.isEmpty()) {
                    it.remove();
                    continue;
                }

                var items = ingredient.getItems();
                if (items.length == 0 || items[0].isEmpty()) {
                    it.remove();
                    continue;
                }

                int amount = items[0].getCount();

                for (var it2 = itemInventory.object2LongEntrySet().iterator(); it2.hasNext();) {
                    var entry = it2.next();
                    var stack = entry.getKey();
                    var count = entry.getLongValue();

                    if (stack.isEmpty() || count == 0) {
                        it2.remove();
                        continue;
                    }

                    if (!ingredient.test(stack)) continue;

                    int extracted = Math.min(GTMath.saturatedCast(count), amount);

                    if (!simulate && extracted > 0) {
                        changed = true;
                        count -= extracted;
                        if (count == 0) it2.remove();
                        else entry.setValue(count);
                    }

                    amount -= extracted;

                    if (amount <= 0) {
                        it.remove();
                        break;
                    }
                }

                if (amount > 0) {
                    if (ingredient instanceof SizedIngredient sizedIngredient) {
                        sizedIngredient.setAmount(amount);
                    } else {
                        items[0].setCount(amount);
                    }
                }
            }

            if (changed) onContentsChanged();
            return left.isEmpty() ? null : left;
        }

        public @Nullable List<FluidIngredient> handleFluidInternal(List<FluidIngredient> left, boolean simulate) {
            boolean changed = false;

            for (var it = left.listIterator(); it.hasNext();) {
                var ingredient = it.next();

                if (ingredient.isEmpty()) {
                    it.remove();
                    continue;
                }

                var fluids = ingredient.getStacks();
                if (fluids.length == 0 || fluids[0].isEmpty()) {
                    it.remove();
                    continue;
                }

                int amount = fluids[0].getAmount();

                for (var it2 = fluidInventory.object2LongEntrySet().iterator(); it2.hasNext();) {
                    var entry = it2.next();
                    var stack = entry.getKey();
                    var count = entry.getLongValue();

                    if (stack.isEmpty() || count == 0) {
                        it2.remove();
                        continue;
                    }

                    if (!ingredient.test(stack)) continue;

                    int extracted = Math.min(GTMath.saturatedCast(count), amount);

                    if (!simulate && extracted > 0) {
                        changed = true;
                        count -= extracted;
                        if (count == 0) it2.remove();
                        else entry.setValue(count);
                    }

                    amount -= extracted;

                    if (amount <= 0) {
                        it.remove();
                        break;
                    }
                }

                if (amount > 0) {
                    ingredient.setAmount(amount);
                }
            }

            if (changed) onContentsChanged();
            return left.isEmpty() ? null : left;
        }

        @Override
        public CompoundTag serializeNBT() {
            var tag = new CompoundTag();

            var itemsTag = new ListTag();
            for (var entry : itemInventory.object2LongEntrySet()) {
                var ct = entry.getKey().serializeNBT();
                ct.putLong("real", entry.getLongValue());
                itemsTag.add(ct);
            }
            if (!itemsTag.isEmpty()) {
                tag.put("inventory", itemsTag);
            }

            var fluidsTag = new ListTag();
            for (var entry : fluidInventory.object2LongEntrySet()) {
                var ct = entry.getKey().writeToNBT(new CompoundTag());
                ct.putLong("real", entry.getLongValue());
                fluidsTag.add(ct);
            }
            if (!fluidsTag.isEmpty()) {
                tag.put("fluidInventory", fluidsTag);
            }

            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            itemInventory.clear();
            fluidInventory.clear();

            var items = tag.getList("inventory", Tag.TAG_COMPOUND);
            for (var t : items) {
                if (!(t instanceof CompoundTag ct)) continue;

                var stack = ItemStack.of(ct);
                var count = ct.getLong("real");

                if (!stack.isEmpty() && count > 0) {
                    itemInventory.put(stack, count);
                }
            }

            var fluids = tag.getList("fluidInventory", Tag.TAG_COMPOUND);
            for (var t : fluids) {
                if (!(t instanceof CompoundTag ct)) continue;

                var stack = FluidStack.loadFluidStackFromNBT(ct);
                var amount = ct.getLong("real");

                if (!stack.isEmpty() && amount > 0) {
                    fluidInventory.put(stack, amount);
                }
            }

            itemStacks = null;
            fluidStacks = null;
        }
    }
}
