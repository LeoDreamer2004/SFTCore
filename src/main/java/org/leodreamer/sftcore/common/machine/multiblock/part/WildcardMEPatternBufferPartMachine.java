package org.leodreamer.sftcore.common.machine.multiblock.part;

import org.leodreamer.sftcore.api.feature.IMEPatternBufferCache;
import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternDecoder;
import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternLogic;
import org.leodreamer.sftcore.integration.ae2.feature.HackyContainerGroupProxy;
import org.leodreamer.sftcore.integration.ae2.feature.IScaleUpCraftingProvider;
import org.leodreamer.sftcore.integration.ae2.logic.ScaledProcessingPattern;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IFilteredHandler;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.item.component.ISpoilableItem;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IDataStickInteractable;
import com.gregtechceu.gtceu.api.machine.mui.MachineUIPanelBuilder;
import com.gregtechceu.gtceu.api.machine.trait.notifiable.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.notifiable.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.machine.trait.notifiable.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.machine.trait.recipe.RecipeHandlerGroupDistinctness;
import com.gregtechceu.gtceu.api.machine.trait.recipe.RecipeHandlerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.api.sync_system.annotations.SyncToClient;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;
import com.gregtechceu.gtceu.common.item.behavior.IntCircuitBehaviour;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;
import com.gregtechceu.gtceu.common.mui.GTMuiMachineUtil;
import com.gregtechceu.gtceu.common.mui.widgets.PopupPanel;
import com.gregtechceu.gtceu.integration.ae2.machine.MEBusPartMachine;
import com.gregtechceu.gtceu.utils.GTMath;
import com.gregtechceu.gtceu.utils.ItemStackHashStrategy;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
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
import brachy.modularui.api.IPanelHandler;
import brachy.modularui.api.drawable.Text;
import brachy.modularui.drawable.DynamicDrawable;
import brachy.modularui.drawable.ItemDrawable;
import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.RichTooltip;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.sync.BooleanSyncValue;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.value.sync.SyncHandlers;
import brachy.modularui.widget.ParentWidget;
import brachy.modularui.widgets.ButtonWidget;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.layout.Grid;
import brachy.modularui.widgets.slot.ItemSlot;
import brachy.modularui.widgets.slot.SlotGroup;
import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.Object2LongOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
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

    private final List<Worker> workers = new ArrayList<>();

    @Getter
    private final AggregateItemHandler aggregateItemHandler;

    @Getter
    private final AggregateFluidHandler aggregateFluidHandler;

    @Getter
    protected final RecipeHandlerList bufferRecipeHandler;

    private final List<RecipeHandlerList> bufferHandlers;

    private final List<RecipeHandlerList> cacheSlotHandlers = new ArrayList<>();

    @Nullable
    protected TickableSubscription updateSubs;

    private boolean needPatternSync;

    /**
     * One cached recipe per generated pattern / internal slot.
     */
    private GTRecipe[] cachedRecipes = new GTRecipe[0];

    @SaveField
    private String[] cachedRecipeIds = new String[0];

    public WildcardMEPatternBufferPartMachine(BlockEntityCreationInfo info) {
        super(info, IO.IN, new NotifiableItemStackHandler(9, IO.IN, IO.NONE));

        patternInventory.setOnContentsChanged(
            () -> getSyncDataHolder().markClientSyncFieldDirty("patternInventory")
        );

        patternInventory.setFilter(WildcardPatternDecoder.INSTANCE::isEncodedPattern);

        getMainNode().addService(ICraftingProvider.class, this);

        this.shareInventory = attachTrait(new NotifiableItemStackHandler(9, IO.IN, IO.NONE));
        this.shareTank = attachTrait(new NotifiableFluidTank(9, 8 * FluidType.BUCKET_VOLUME, IO.IN, IO.NONE));
        this.aggregateItemHandler = attachTrait(new AggregateItemHandler());
        this.aggregateFluidHandler = attachTrait(new AggregateFluidHandler());
        this.bufferRecipeHandler = new BufferRecipeHandlerList();
        this.bufferHandlers = List.of(bufferRecipeHandler);
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
        setChanged();
    }

    private void rebuildWildcardPatterns() {
        var generatedPatterns = collectWildcardPatterns();

        // Preserve already deserialized internal contents on world load
        installGeneratedPatterns(generatedPatterns, true);
        restoreCachedRecipes();
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
        cacheSlotHandlers.clear();
        syncWorkerCount(size);

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

            newSlots[i].setOnContentsChanged(this::onSlotChanged);
        }

        internalInventory = newSlots;
    }

    private void syncWorkerCount(int size) {
        workers.forEach(worker -> worker.slot.setOnContentsChanged(() -> {}));
        workers.clear();
        while (workers.size() < size) {
            addWorker();
        }
    }

    private void addWorker() {
        int idx = workers.size();
        if (idx >= internalInventory.length) {
            return;
        }
        var slot = internalInventory[idx];
        slot.setOnContentsChanged(this::onSlotChanged);
        workers.add(new Worker(slot));
    }

    private void onSlotChanged() {
        aggregateItemHandler.notifyListeners();
        aggregateFluidHandler.notifyListeners();
        setChanged();
    }

    private void resizeCachedRecipes(int size) {
        cachedRecipes = Arrays.copyOf(cachedRecipes, size);
        resizeCachedRecipeIdsTag(size);
    }

    private void clearAllCachedRecipes() {
        Arrays.fill(cachedRecipes, null);
        Arrays.fill(cachedRecipeIds, "");
        setChanged();
    }

    private void restoreCachedRecipes() {
        Arrays.fill(cachedRecipes, null);
        resizeCachedRecipeIdsTag(cachedRecipes.length);

        for (int slot = 0; slot < cachedRecipeIds.length; slot++) {
            var recipeId = cachedRecipeIds[slot];
            if (recipeId == null || recipeId.isEmpty()) {
                cachedRecipeIds[slot] = "";
                continue;
            }

            var id = ResourceLocation.tryParse(recipeId);
            var recipe = id == null ? null : getLevel().getRecipeManager().byKey(id).orElse(null);
            if (recipe instanceof GTRecipe gtRecipe) {
                cachedRecipes[slot] = gtRecipe;
            } else {
                cachedRecipeIds[slot] = "";
            }
        }
    }

    private void resizeCachedRecipeIdsTag(int size) {
        int oldSize = cachedRecipeIds.length;
        cachedRecipeIds = Arrays.copyOf(cachedRecipeIds, size);
        if (size > oldSize) {
            Arrays.fill(cachedRecipeIds, oldSize, size, "");
        }
    }

    private void refundAllInternalSlots() {
        for (var internalSlot : internalInventory) {
            if (internalSlot != null) {
                internalSlot.refund();
            }
        }
    }

    private boolean couldSlotMatchContents(
        InternalSlot slot,
        Map<RecipeCapability<?>, List<Object>> contents
    ) {
        var itemContents = contents.get(ItemRecipeCapability.CAP);
        if (itemContents != null && !slot.isItemEmpty()) {
            Set<Item> itemTypes = slot.getItemTypes();
            for (var obj : itemContents) {
                if (!(obj instanceof Ingredient ingredient) || ingredient.isEmpty()) {
                    continue;
                }
                for (var stack : ingredient.getItems()) {
                    if (itemTypes.contains(stack.getItem())) {
                        return true;
                    }
                }
            }
        }

        var fluidContents = contents.get(FluidRecipeCapability.CAP);
        if (fluidContents != null && !slot.isFluidEmpty()) {
            Set<Fluid> fluidTypes = slot.getFluidTypes();
            for (var obj : fluidContents) {
                if (!(obj instanceof FluidIngredient ingredient) || ingredient.isEmpty()) {
                    continue;
                }
                for (var stack : ingredient.getStacks()) {
                    if (fluidTypes.contains(stack.getFluid())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private class Worker {

        final InternalSlot slot;
        final SlotItemHandler itemHandler;
        final SlotFluidHandler fluidHandler;

        Worker(InternalSlot slot) {
            this.slot = slot;
            this.itemHandler = new SlotItemHandler(slot);
            this.fluidHandler = new SlotFluidHandler(slot);
        }
    }

    private class SlotItemHandler implements IRecipeHandler<Ingredient> {

        private final InternalSlot slot;

        SlotItemHandler(InternalSlot slot) {
            this.slot = slot;
        }

        @Override
        public List<Ingredient> handleRecipeInner(IO io, GTRecipe recipe, List<Ingredient> left, boolean simulate) {
            if (io != IO.IN || slot.isItemEmpty()) {
                return left;
            }
            return slot.handleItemInternal(recipe, left, simulate);
        }

        @Override
        public List<Object> getContents() {
            return new ArrayList<>(slot.getItems());
        }

        @Override
        public double getTotalContentAmount() {
            return slot.getItems().stream().mapToLong(ItemStack::getCount).sum();
        }

        @Override
        public RecipeCapability<Ingredient> getCapability() {
            return ItemRecipeCapability.CAP;
        }

        @Override
        public boolean isDistinct() {
            return true;
        }

        @Override
        public int getPriority() {
            return IFilteredHandler.HIGH;
        }
    }

    private class SlotFluidHandler implements IRecipeHandler<FluidIngredient> {

        private final InternalSlot slot;

        SlotFluidHandler(InternalSlot slot) {
            this.slot = slot;
        }

        @Override
        public List<FluidIngredient> handleRecipeInner(
            IO io,
            GTRecipe recipe,
            List<FluidIngredient> left,
            boolean simulate
        ) {
            if (io != IO.IN || slot.isFluidEmpty()) {
                return left;
            }
            return slot.handleFluidInternal(recipe, left, simulate);
        }

        @Override
        public List<Object> getContents() {
            return new ArrayList<>(slot.getFluids());
        }

        @Override
        public double getTotalContentAmount() {
            return slot.getFluids().stream().mapToLong(FluidStack::getAmount).sum();
        }

        @Override
        public RecipeCapability<FluidIngredient> getCapability() {
            return FluidRecipeCapability.CAP;
        }

        @Override
        public boolean isDistinct() {
            return true;
        }

        @Override
        public int getPriority() {
            return IFilteredHandler.HIGH;
        }
    }

    @Getter
    private class AggregateItemHandler extends NotifiableRecipeHandlerTrait<Ingredient> {

        private final RecipeCapability<Ingredient> capability = ItemRecipeCapability.CAP;
        private final IO handlerIO = IO.IN;
        private final boolean isDistinct = true;

        @Override
        public List<Ingredient> handleRecipeInner(IO io, GTRecipe recipe, List<Ingredient> left, boolean simulate) {
            return left;
        }

        @Override
        public List<Object> getContents() {
            var contents = new ArrayList<>();
            for (var worker : workers) {
                contents.addAll(worker.slot.getItems());
            }
            return contents;
        }

        @Override
        public double getTotalContentAmount() {
            double sum = 0;
            for (var worker : workers) {
                sum += worker.slot.getItems().stream().mapToLong(ItemStack::getCount).sum();
            }
            return sum;
        }

        @Override
        public int getPriority() {
            return IFilteredHandler.HIGH;
        }
    }

    @Getter
    private class AggregateFluidHandler extends NotifiableRecipeHandlerTrait<FluidIngredient> {

        private final RecipeCapability<FluidIngredient> capability = FluidRecipeCapability.CAP;
        private final IO handlerIO = IO.IN;
        private final boolean isDistinct = true;

        @Override
        public List<FluidIngredient> handleRecipeInner(
            IO io,
            GTRecipe recipe,
            List<FluidIngredient> left,
            boolean simulate
        ) {
            return left;
        }

        @Override
        public List<Object> getContents() {
            var contents = new ArrayList<>();
            for (var worker : workers) {
                contents.addAll(worker.slot.getFluids());
            }
            return contents;
        }

        @Override
        public double getTotalContentAmount() {
            double sum = 0;
            for (var worker : workers) {
                sum += worker.slot.getFluids().stream().mapToLong(FluidStack::getAmount).sum();
            }
            return sum;
        }

        @Override
        public int getPriority() {
            return IFilteredHandler.HIGH;
        }
    }

    private class BufferRecipeHandlerList extends RecipeHandlerList {

        BufferRecipeHandlerList() {
            super(IO.IN);
            addHandlers(
                getCircuitSlot(), getShareInventory(), getShareTank(), aggregateItemHandler, aggregateFluidHandler
            );
            setGroup(RecipeHandlerGroupDistinctness.BUS_DISTINCT);
        }

        @Override
        public Map<RecipeCapability<?>, List<Object>> handleRecipe(
            IO io,
            GTRecipe recipe,
            Map<RecipeCapability<?>, List<Object>> contents,
            boolean simulate
        ) {
            if (io != IO.IN || contents.isEmpty()) {
                return contents;
            }

            for (var worker : workers) {
                var slot = worker.slot;
                if (slot.isEmpty() || !couldSlotMatchContents(slot, contents)) {
                    continue;
                }

                var left = consumeWorker(worker, io, recipe, contents, true);
                if (!left.isEmpty()) {
                    continue;
                }

                return simulate ? left : consumeWorker(worker, io, recipe, contents, false);
            }

            return contents;
        }

        private Map<RecipeCapability<?>, List<Object>> consumeWorker(
            Worker worker,
            IO io,
            GTRecipe recipe,
            Map<RecipeCapability<?>, List<Object>> contents,
            boolean simulate
        ) {
            var copy = new Reference2ObjectOpenHashMap<>(contents);
            for (var it = copy.reference2ObjectEntrySet().fastIterator(); it.hasNext();) {
                var entry = it.next();
                var handlers = handlersFor(entry.getKey(), worker);
                for (var handler : handlers) {
                    if (
                        io == IO.IN && handler.getTotalContentAmount() == 0 &&
                            !handler.getCapability().skipEmptyContentCheck()
                    ) {
                        continue;
                    }

                    var handlerLeft = handler.handleRecipe(io, recipe, entry.getValue(), simulate);
                    if (handlerLeft.isEmpty()) {
                        it.remove();
                        break;
                    }
                    entry.setValue(new ArrayList<>(handlerLeft));
                }
            }
            return copy;
        }

        private List<IRecipeHandler<?>> handlersFor(RecipeCapability<?> capability, Worker worker) {
            if (capability == ItemRecipeCapability.CAP) {
                return List.of(getCircuitSlot(), getShareInventory(), worker.itemHandler);
            }
            if (capability == FluidRecipeCapability.CAP) {
                return List.of(getShareTank(), worker.fluidHandler);
            }
            return List.of();
        }

        @Override
        public boolean isDistinct() {
            return true;
        }

        @Override
        public void setDistinct(boolean ignored, boolean notify) {}
    }

    @Override
    public List<RecipeHandlerList> getRecipeHandlers() {
        return bufferHandlers;
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

    private void refundAll() {
        refundAllInternalSlots();
        clearAllCachedRecipes();
    }

    @Override
    public MachineUIPanelBuilder getPanelBuilder(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        IPanelHandler sharedItemsPanelHandler = syncManager.syncedPanel(
            "wildcard_shared_items", true,
            (panelSyncManager, handler) -> {
                SlotGroup sharedItemSlotGroup = new SlotGroup("wildcard_shared_item_slots", 3, false);
                return PopupPanel.createPopupPanel("wildcard_shared_items_panel", 80, 86)
                    .child(Text.lang("gui.gtceu.share_inventory.title").asWidget().margin(4))
                    .child(
                        new Grid()
                            .name("wildcard_shared_item_grid")
                            .top(26)
                            .height(18 * 3)
                            .minElementMargin(0, 0)
                            .minColWidth(18)
                            .minRowHeight(18)
                            .leftRel(0.5f)
                            .gridOfSizeWidth(
                                9, 3, (x, y, index) -> new ItemSlot()
                                    .slot(
                                        SyncHandlers.itemSlot(shareInventory.storage, index)
                                            .slotGroup(sharedItemSlotGroup)
                                            .accessibility(true, true)
                                    )
                            )
                    );
            }
        );

        IPanelHandler sharedFluidsPanelHandler = syncManager.syncedPanel(
            "wildcard_shared_fluids", true,
            (panelSyncManager, handler) -> PopupPanel.createPopupPanel("wildcard_shared_fluids_panel", 85, 86)
                .child(Text.lang("gui.gtceu.share_tank.title").asWidget().margin(4))
                .child(
                    GTMuiMachineUtil.createSlotGroupFromInventory(
                        panelSyncManager,
                        shareTank,
                        "wildcard_shared_fluid_slots",
                        9,
                        'F',
                        GTMuiMachineUtil.createSquareMatrix(9, 'F')
                    )
                        .top(26)
                        .leftRel(0.5f)
                )
        );

        BooleanSyncValue canRefundValue = new BooleanSyncValue(this::canRefund, b -> {});
        syncManager.syncValue("wildcard_can_refund", canRefundValue);
        syncManager.registerServerSyncedAction("wildcard_refund_all", packet -> refundAll());

        return MachineUIPanelBuilder.panelBuilder(this).leftConfigurators(configurators -> {
            configurators.child(
                new ButtonWidget<>()
                    .size(18)
                    .onMousePressed((context, button) -> {
                        if (button == InputConstants.MOUSE_BUTTON_LEFT) {
                            sharedItemsPanelHandler.openPanel();
                            return true;
                        }
                        return false;
                    })
                    .overlay(GTGuiTextures.BUTTON_ITEM_OUTPUT)
                    .tooltip(
                        new RichTooltip()
                            .addLine(Text.lang("gui.gtceu.share_inventory.desc.0"))
                            .addLine(Text.lang("gui.gtceu.share_inventory.desc.1"))
                    )
            );
            configurators.child(
                new ButtonWidget<>()
                    .size(18)
                    .onMousePressed((context, button) -> {
                        if (button == InputConstants.MOUSE_BUTTON_LEFT) {
                            sharedFluidsPanelHandler.openPanel();
                            return true;
                        }
                        return false;
                    })
                    .overlay(GTGuiTextures.BUTTON_FLUID_OUTPUT)
                    .tooltip(
                        new RichTooltip()
                            .addLine(Text.lang("gui.gtceu.share_tank.desc.0"))
                            .addLine(Text.lang("gui.gtceu.share_inventory.desc.1"))
                    )
            );
            configurators.child(
                new ButtonWidget<>()
                    .size(18)
                    .onMousePressed((context, button) -> {
                        if (canRefundValue.getBoolValue() && button == InputConstants.MOUSE_BUTTON_LEFT) {
                            syncManager.callSyncedAction("wildcard_refund_all");
                            return true;
                        }
                        return false;
                    })
                    .overlay(
                        new DynamicDrawable(
                            () -> canRefundValue.getBoolValue() ?
                                GTGuiTextures.REFUND_OVERLAY.asIcon().size(16) :
                                new ItemDrawable(ItemStack.EMPTY).asIcon().size(16)
                        )
                    )
                    .tooltip(new RichTooltip().addLine(Text.lang("gui.gtceu.refund_all.desc")))
            );
        });
    }

    @Override
    public void buildMainUI(
        ParentWidget<?> mainWidget, PosGuiData guiData, PanelSyncManager syncManager,
        UISettings settings
    ) {
        SlotGroup patternSlotGroup = new SlotGroup("wildcard_pattern_slots", 1, 0, true);

        BooleanSyncValue isOnlineValue = new BooleanSyncValue(this::isOnline, this::setOnline);
        syncManager.syncValue("wildcard_is_online", isOnlineValue);

        var flow = Flow.col().coverChildren();
        flow.child(
            Text.dynamic(
                () -> isOnlineValue.getBoolValue() ?
                    Component.translatable("gtceu.gui.me_network.online") :
                    Component.translatable("gtceu.gui.me_network.offline")
            )
                .asWidget().marginTop(2).marginBottom(4)
        );
        flow.child(
            new ItemSlot()
                .slot(
                    SyncHandlers.itemSlot(patternInventory, 0)
                        .slotGroup(patternSlotGroup)
                        .accessibility(true, true)
                        .filter(WildcardPatternDecoder.INSTANCE::isEncodedPattern)
                        .changeListener((index, oldStack, newStack, init) -> onPatternChange(0))
                )
                .background(GTGuiTextures.SLOT, GTGuiTextures.PATTERN_OVERLAY)
        );

        mainWidget.child(flow.center());
    }

    public boolean canRefund() {
        return Arrays.stream(internalInventory)
            .anyMatch(slot -> slot != null && (!slot.isItemEmpty() || !slot.isFluidEmpty()));
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

        workers.get(slotIndex).slot.pushPattern(patternDetails, inputHolder);
        return true;
    }

    private int getSlotIndexForPattern(IPatternDetails patternDetails) {
        return wildcardDetailsSlotMap.getOrDefault(unwrapScaledPattern(patternDetails), -1);
    }

    private IPatternDetails unwrapScaledPattern(IPatternDetails patternDetails) {
        return patternDetails instanceof ScaledProcessingPattern scaled ? scaled.original() : patternDetails;
    }

    private boolean slotInvalid(int slot) {
        return slot < 0 || slot >= internalInventory.length || internalInventory[slot] == null;
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

            var circuitStack = getCircuitSlot().isEnabled() ? getCircuitSlot().storage.getStackInSlot(0) :
                ItemStack.EMPTY;
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
        resizeCachedRecipeIdsTag(cachedRecipes.length);
        cachedRecipeIds[slot] = recipe.getId().toString();
        setChanged();
    }

    @Override
    public void sftcore$clearCachedRecipe(int slot) {
        if (slot < 0 || slot >= cachedRecipes.length) {
            return;
        }

        cachedRecipes[slot] = null;
        resizeCachedRecipeIdsTag(cachedRecipes.length);
        cachedRecipeIds[slot] = "";
        setChanged();
    }

    @Override
    public @Nullable RecipeHandlerList sftcore$getSlotHandler(int slot) {
        if (slotInvalid(slot)) {
            return null;
        }

        while (cacheSlotHandlers.size() <= slot) {
            cacheSlotHandlers.add(null);
        }

        var handler = cacheSlotHandlers.get(slot);
        if (handler == null) {
            handler = new CacheSlotRecipeHandlerList(internalInventory[slot]);
            cacheSlotHandlers.set(slot, handler);
        }

        return handler;
    }

    private class CacheSlotRecipeHandlerList extends RecipeHandlerList {

        CacheSlotRecipeHandlerList(InternalSlot slot) {
            super(IO.IN);
            addHandlers(
                getCircuitSlot(),
                getShareInventory(),
                getShareTank(),
                new SlotItemHandler(slot),
                new SlotFluidHandler(slot)
            );
            setGroup(RecipeHandlerGroupDistinctness.BUS_DISTINCT);
        }

        @Override
        public boolean isDistinct() {
            return true;
        }

        @Override
        public void setDistinct(boolean ignored, boolean notify) {}
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
        private @Nullable Set<Item> cachedItemTypes = null;
        private @Nullable Set<Fluid> cachedFluidTypes = null;

        public boolean isItemEmpty() {
            return itemInventory.isEmpty();
        }

        public boolean isFluidEmpty() {
            return fluidInventory.isEmpty();
        }

        public boolean isEmpty() {
            return isItemEmpty() && isFluidEmpty();
        }

        public Set<Item> getItemTypes() {
            if (cachedItemTypes == null) {
                cachedItemTypes = new ReferenceOpenHashSet<>(itemInventory.size());
                itemInventory.keySet().forEach(stack -> cachedItemTypes.add(stack.getItem()));
            }
            return cachedItemTypes;
        }

        public Set<Fluid> getFluidTypes() {
            if (cachedFluidTypes == null) {
                cachedFluidTypes = new ReferenceOpenHashSet<>(fluidInventory.size());
                fluidInventory.keySet().forEach(stack -> cachedFluidTypes.add(stack.getFluid()));
            }
            return cachedFluidTypes;
        }

        public void onContentsChanged() {
            itemStacks = null;
            fluidStacks = null;
            cachedItemTypes = null;
            cachedFluidTypes = null;
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
                if (key == null) {
                    continue;
                }

                long inserted = StorageHelper.poweredInsert(energy, networkInv, key, count, actionSource);
                if (inserted > 0) {
                    count -= inserted;
                    if (count == 0) {
                        it.remove();
                    } else {
                        entry.setValue(count);
                    }
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
                if (key == null) {
                    continue;
                }

                long inserted = StorageHelper.poweredInsert(energy, networkInv, key, amount, actionSource);
                if (inserted > 0) {
                    amount -= inserted;
                    if (amount == 0) {
                        it.remove();
                    } else {
                        entry.setValue(amount);
                    }
                }
            }

            onContentsChanged();
        }

        public void pushPattern(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
            patternDetails.pushInputsToExternalInventory(inputHolder, this::add);
            onContentsChanged();
        }

        public List<Ingredient> handleItemInternal(GTRecipe recipe, List<Ingredient> left, boolean simulate) {
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

                    if (!ingredient.test(stack)) {
                        continue;
                    }

                    int extracted = Math.min(GTMath.saturatedCast(count), amount);

                    if (extracted > 0) {
                        var copied = stack.copyWithCount(extracted);
                        ISpoilableItem spoilable = GTCapabilityHelper.getSpoilable(copied);
                        if (spoilable != null) {
                            spoilable.freezeSpoiling();
                        }
                        recipe.spoilageData.addConsumedInput(GTRecipeCapabilities.ITEM, Ingredient.of(copied));
                    }

                    if (!simulate && extracted > 0) {
                        changed = true;
                        count -= extracted;
                        if (count == 0) {
                            it2.remove();
                        } else {
                            entry.setValue(count);
                        }
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

            if (changed) {
                onContentsChanged();
            }
            return left;
        }

        public List<FluidIngredient> handleFluidInternal(
            GTRecipe recipe,
            List<FluidIngredient> left,
            boolean simulate
        ) {
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

                    if (!ingredient.test(stack)) {
                        continue;
                    }

                    int extracted = Math.min(GTMath.saturatedCast(count), amount);

                    if (extracted > 0) {
                        var copied = stack.copy();
                        copied.setAmount(extracted);
                        recipe.spoilageData.addConsumedInput(
                            GTRecipeCapabilities.FLUID, FluidIngredient.of(copied)
                        );
                    }

                    if (!simulate && extracted > 0) {
                        changed = true;
                        count -= extracted;
                        if (count == 0) {
                            it2.remove();
                        } else {
                            entry.setValue(count);
                        }
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

            if (changed) {
                onContentsChanged();
            }
            return left;
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
                if (!(t instanceof CompoundTag ct)) {
                    continue;
                }

                var stack = ItemStack.of(ct);
                var count = ct.getLong("real");

                if (!stack.isEmpty() && count > 0) {
                    itemInventory.put(stack, count);
                }
            }

            var fluids = tag.getList("fluidInventory", Tag.TAG_COMPOUND);
            for (var t : fluids) {
                if (!(t instanceof CompoundTag ct)) {
                    continue;
                }

                var stack = FluidStack.loadFluidStackFromNBT(ct);
                var amount = ct.getLong("real");

                if (!stack.isEmpty() && amount > 0) {
                    fluidInventory.put(stack, amount);
                }
            }

            itemStacks = null;
            fluidStacks = null;
            cachedItemTypes = null;
            cachedFluidTypes = null;
        }
    }
}
