package org.leodreamer.sftcore.mixin.ae2.menu;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.world.Container;
import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.integration.ae2.feature.IPatternMultiply;
import org.leodreamer.sftcore.integration.ae2.feature.IPromptProvider;
import org.leodreamer.sftcore.integration.ae2.feature.ISendToAssemblyMatrix;
import org.leodreamer.sftcore.integration.ae2.feature.ISendToGTMachine;
import org.leodreamer.sftcore.integration.ae2.logic.AvailableGTRow;
import org.leodreamer.sftcore.integration.ae2.logic.GTTransferLogic;
import org.leodreamer.sftcore.integration.ae2.sync.AvailableGTMachinesPacket;

import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.ITerminalHost;
import appeng.core.definitions.AEItems;
import appeng.helpers.IMenuCraftingPacket;
import appeng.helpers.IPatternTerminalMenuHost;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.menu.guisync.GuiSync;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.RestrictedInputSlot;
import appeng.util.ConfigInventory;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixPattern;
import org.leodreamer.sftcore.integration.ae2.sync.RecipeInfoPack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

@Mixin(PatternEncodingTermMenu.class)
public abstract class PatternEncodingTermMenuMixin extends MEStorageMenu
    implements IMenuCraftingPacket, ISendToGTMachine,
    ISendToAssemblyMatrix, IPatternMultiply {

    @Unique
    private static final AEItemKey sftcore$key = AEItemKey.of(AEItems.BLANK_PATTERN);

    @Shadow(remap = false)
    @Final
    private RestrictedInputSlot blankPatternSlot;

    // server side
    @Unique
    private List<PatternContainer> sftcore$gtContainerTargets = new ArrayList<>();

    @Unique
    @GuiSync(150)
    public boolean sftcore$transferToMatrix = true;

    @Unique
    @Nullable
    private RecipeInfo sftcore$curRecipe = null;

    @Shadow(remap = false)
    @Final
    private RestrictedInputSlot encodedPatternSlot;

    @Shadow(remap = false)
    @Final
    private ConfigInventory encodedInputsInv;

    @Shadow(remap = false)
    @Final
    private ConfigInventory encodedOutputsInv;

    @Unique
    private static final String TRANSFER_TO_MATRIX = "transferToMatrix";

    @Unique
    private static final String SET_GT_RECIPE_INFO = "setGTRecipeInfo";

    @Unique
    private static final String SEND_TO_GT_MACHINE = "sendToGTMachine";

    @Unique
    private static final String MULTIPLY_PATTERN = "multiplyPattern";

    public PatternEncodingTermMenuMixin(
        MenuType<?> menuType,
        int id,
        Inventory ip,
        ITerminalHost host
    ) {
        super(menuType, id, ip, host);
    }

    @Inject(
        method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lappeng/helpers/IPatternTerminalMenuHost;Z)V",
        at = @At("TAIL"),
        remap = false
    )
    private void initPattern(
        MenuType<?> menuType,
        int id,
        Inventory ip,
        IPatternTerminalMenuHost host,
        boolean bindInventory,
        CallbackInfo ci
    ) {
        blankPatternSlot.setAllowEdit(false);
        blankPatternSlot.setStackLimit(Integer.MAX_VALUE);

        registerClientAction(TRANSFER_TO_MATRIX, Boolean.class, this::sftcore$setTransferToMatrix);
        registerClientAction(
                SET_GT_RECIPE_INFO,
            RecipeInfoPack.class,
            (pack) -> sftcore$setGTRecipeInfo(pack.unpack())
        );
        registerClientAction(SEND_TO_GT_MACHINE, Integer.class, this::sftcore$sendToGTMachine);
        registerClientAction(MULTIPLY_PATTERN, Integer.class, this::sftcore$multiplyPattern);
    }

    @Override
    @Unique
    public boolean sftcore$getTransferToMatrix() {
        return sftcore$transferToMatrix;
    }

    @Override
    @Unique
    public void sftcore$setTransferToMatrix(boolean transferToMatrix) {
        if (isClientSide()) {
            sendClientAction(TRANSFER_TO_MATRIX, transferToMatrix);
        } else {
            sftcore$transferToMatrix = transferToMatrix;
        }
    }

    @Override
    @Unique
    public void sftcore$setGTRecipeInfo(RecipeInfo info) {
        if (isClientSide()) {
            sendClientAction(SET_GT_RECIPE_INFO, RecipeInfoPack.pack(info));
        } else {
            sftcore$curRecipe = info;
        }
    }

    @Override
    public void sftcore$sendToGTMachine(int chooseIndex) {
        if (isClientSide()) {
            sendClientAction(SEND_TO_GT_MACHINE, chooseIndex);
            return;
        }

        var container = sftcore$gtContainerTargets.get(chooseIndex);
        var inv = container.getTerminalPatternInventory();
        var pattern = encodedPatternSlot.getItem();
        if (pattern.isEmpty()) return;

        var remainder = inv.addItems(pattern);
        encodedPatternSlot.set(remainder);
    }

    @Override
    @Unique
    public void sftcore$multiplyPattern(int multiplier) {
        if (isClientSide()) {
            sendClientAction(MULTIPLY_PATTERN, multiplier);
            return;
        }

        sftcore$multiplySlotStack(encodedOutputsInv, multiplier);
        sftcore$multiplySlotStack(encodedInputsInv, multiplier);
    }

    @Inject(method = "broadcastChanges", at = @At("TAIL"))
    private void updateSlotOnChanged(CallbackInfo ci) {
        sftcore$updateSlot();
    }

    @Unique
    private void sftcore$updateSlot() {
        var host = getHost();
        if (host == null) {
            blankPatternSlot.set(ItemStack.EMPTY);
            return;
        }

        var inventory = host.getInventory();
        if (inventory == null) {
            return;
        }

        int left = (int) inventory.getAvailableStacks().get(sftcore$key);
        if (left == 0) {
            blankPatternSlot.set(ItemStack.EMPTY);
            return;
        }
        var stack = blankPatternSlot.getItem();
        if (stack.isEmpty()) {
            blankPatternSlot.set(new ItemStack(AEItems.BLANK_PATTERN, left));
        } else {
            stack.setCount(left); // faster
        }
    }

    @Inject(method = "encode", at = @At("HEAD"), remap = false, cancellable = true)
    private void preventEncodingWhenDisconnected(CallbackInfo ci) {
        if (!isPowered()) {
            ci.cancel();
        }
    }

    @Inject(method = "encode", at = @At("TAIL"), remap = false)
    private void AutoTransferAfterEncoding(CallbackInfo ci) {
        sftcore$gtContainerTargets.clear();
        var packet = sftcore$tryTransferToMatrix() ? AvailableGTMachinesPacket.empty() :
            sftcore$checkAvailableGTMachine();
        sendPacketToClient(packet);
    }

    @Unique
    private boolean sftcore$tryTransferToMatrix() {
        var node = getNetworkNode();
        if (node == null) return false;
        if (!sftcore$transferToMatrix) return false;

        var pattern = encodedPatternSlot.getItem();
        if (pattern.isEmpty()) return false;

        // check pattern type
        if (
            !AEItems.CRAFTING_PATTERN.isSameAs(pattern) && !AEItems.STONECUTTING_PATTERN.isSameAs(pattern) &&
                !AEItems.SMITHING_TABLE_PATTERN.isSameAs(pattern)
        )
            return false;

        for (var mat : node.getGrid().getActiveMachines(TileAssemblerMatrixPattern.class)) {
            var inv = mat.getPatternInventory();
            if (!pattern.isEmpty()) {
                pattern = inv.addItems(pattern); // overflow, try on
            }
            if (pattern.isEmpty()) {
                encodedPatternSlot.set(ItemStack.EMPTY);
                return true;
            }
        }

        return false;
    }

    @Unique
    private AvailableGTMachinesPacket sftcore$checkAvailableGTMachine() {
        var thisNode = getNetworkNode();
        if (thisNode == null) return AvailableGTMachinesPacket.empty();

        var pattern = encodedPatternSlot.getItem();
        if (pattern.isEmpty() || !AEItems.PROCESSING_PATTERN.isSameAs(pattern))
            return AvailableGTMachinesPacket.empty();

        SFTCore.LOGGER.info("Trying to check available GT machines to auto transfer");

        List<AvailableGTRow> rows = new ArrayList<>();
        Map<AvailableGTRow, PatternContainer> rowMap = new Object2ObjectArrayMap<>();
        for (var clazz : thisNode.getGrid().getMachineClasses()) {
            if (PatternContainer.class.isAssignableFrom(clazz)) {
                @SuppressWarnings("unchecked")
                var machineClz = (Class<? extends PatternContainer>) clazz;

                for (var node : thisNode.getGrid().getMachineNodes(machineClz)) {
                    var owner = node.getOwner();
                    if (!machineClz.isInstance(owner)) continue;
                    var container = machineClz.cast(owner);
                    var row = GTTransferLogic.tryBuild(container, node, sftcore$curRecipe);
                    row.ifPresent(
                        r -> {
                            if (container instanceof IPromptProvider promptProvider) {
                                r = r.withPrompt(promptProvider.sftcore$getPrompt());
                            }
                            rows.add(r);
                            rowMap.put(r, container);
                        }
                    );
                }
            }
        }
        // sort the rows by the given weight
        rows.sort((r1, r2) -> Integer.compare(r2.weight(), r1.weight()));
        SFTCore.LOGGER.info("Found {} machines to provide pattern", rows.size());
        sftcore$gtContainerTargets = rows.stream().map(rowMap::get).toList();
        return new AvailableGTMachinesPacket(rows);
    }

    @Redirect(
        method = "encode",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V")
    )
    private void extractPatternFromStorage(ItemStack instance, int pDecrement) {
        if (storage != null) {
            storage.extract(sftcore$key, 1, Actionable.MODULATE, getActionSource());
        }
    }

    @Redirect(
        method = "transferStackToMenu",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/menu/slot/RestrictedInputSlot;mayPlace(Lnet/minecraft/world/item/ItemStack;)Z",
            ordinal = 0,
            remap = true
        ),
        remap = false
    )
    private boolean transferStack$skipBlankPattern(RestrictedInputSlot instance, ItemStack itemStack) {
        return instance.mayPlace(itemStack) && itemStack.getItem() != AEItems.BLANK_PATTERN.asItem();
    }

    @Unique
    private void sftcore$multiplySlotStack(ConfigInventory inv, int multiplier) {
        for (int i = 0; i < inv.size(); i++) {
            var stack = inv.getStack(i);
            if (stack == null) continue;
            var amount = stack.amount();
            if (multiplier >= 0) {
                amount *= multiplier;
            } else {
                amount /= -multiplier;
                if (amount == 0) amount = 1;
            }
            if (amount > Integer.MAX_VALUE) {
                amount = Integer.MAX_VALUE;
            }
            inv.setStack(i, new GenericStack(stack.what(), amount));
        }
    }
}
