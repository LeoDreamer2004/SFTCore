package org.leodreamer.sftcore.mixin.ae2.menu;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.common.data.lang.MixinTooltips;
import org.leodreamer.sftcore.common.item.wildcard.WildcardPatternDecoder;
import org.leodreamer.sftcore.integration.ae2.feature.*;
import org.leodreamer.sftcore.integration.ae2.logic.AvailableGTRow;
import org.leodreamer.sftcore.integration.ae2.logic.GTTransferLogic;
import org.leodreamer.sftcore.integration.ae2.sync.AvailableGTMachinesPacket;
import org.leodreamer.sftcore.integration.ae2.sync.RecipeInfoPack;
import org.leodreamer.sftcore.integration.ae2.sync.VirtualCatalystsPack;
import org.leodreamer.sftcore.mixin.gtmoremachine.VirtualItemProviderBehaviorAccessor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import appeng.api.config.Actionable;
import appeng.api.crafting.PatternDetailsHelper;
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
import cn.qiuye.gtmoremachine.integration.ae.item.GTMMAEItems;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixPattern;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
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

import javax.annotation.Nullable;

@Mixin(PatternEncodingTermMenu.class)
public abstract class PatternEncodingTermMenuMixin extends MEStorageMenu
    implements IMenuCraftingPacket, ISendToGTMachine,
    ISendToAssemblyMatrix, IPatternMultiply, IVirtualCatalystEncoding {

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
    @GuiSync(151)
    public boolean sftcore$virtualCatalysts = false;

    @Unique
    @Nullable
    private RecipeInfo sftcore$curRecipe = null;

    @Unique
    private List<ItemStack> sftcore$virtualCatalystItems = List.of();

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

    @Unique
    private static final String VIRTUAL_CATALYSTS = "virtualCatalysts";

    @Unique
    private static final String SET_VIRTUAL_CATALYSTS = "setVirtualCatalysts";

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
        registerClientAction(VIRTUAL_CATALYSTS, Boolean.class, this::sftcore$setVirtualCatalystsEnabled);
        registerClientAction(
            SET_VIRTUAL_CATALYSTS, VirtualCatalystsPack.class,
            pack -> sftcore$setVirtualCatalysts(pack.unpack())
        );
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
    @Unique
    public boolean sftcore$getVirtualCatalystsEnabled() {
        return sftcore$virtualCatalysts;
    }

    @Override
    @Unique
    public void sftcore$setVirtualCatalystsEnabled(boolean enabled) {
        if (isClientSide()) {
            sendClientAction(VIRTUAL_CATALYSTS, enabled);
        } else {
            sftcore$virtualCatalysts = enabled;
        }
    }

    @Override
    @Unique
    public void sftcore$setVirtualCatalysts(List<ItemStack> catalysts) {
        if (isClientSide()) {
            sendClientAction(SET_VIRTUAL_CATALYSTS, VirtualCatalystsPack.pack(catalysts));
        } else {
            sftcore$virtualCatalystItems = List.copyOf(catalysts);
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
        if (pattern.isEmpty()) {
            return;
        }
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

    @Inject(method = "encode", at = @At("HEAD"), remap = false)
    private void sftcore$wrapGTCatalysts(CallbackInfo ci) {
        if (isClientSide()) {
            return;
        }

        if (!sftcore$virtualCatalysts || sftcore$curRecipe == null) {
            return;
        }

        for (var catalyst : sftcore$virtualCatalystItems) {
            var virtual = VirtualItemProviderBehaviorAccessor.sftcore$setVirtualItem(
                GTMMAEItems.VIRTUAL_ITEM_PROVIDER.asStack(), catalyst.copyWithCount(1)
            );
            for (int i = 0; i < encodedInputsInv.size(); i++) {
                if (encodedInputsInv.getStack(i) == null) {
                    encodedInputsInv.setStack(i, GenericStack.fromItemStack(virtual));
                    break;
                }
            }
        }
    }

    @Inject(method = "encode", at = @At("TAIL"), remap = false)
    private void autoTransferAfterEncoding(CallbackInfo ci) {
        if (isClientSide()) {
            return;
        }
        sftcore$gtContainerTargets = List.of(); // clear former target

        boolean checkGT = false;
        if (sftcore$checkIsAlreadyCraftable()) {
            getPlayer().sendSystemMessage(Component.translatable(MixinTooltips.ALREADY_CRAFTABLE));
        } else {
            checkGT = !sftcore$tryTransferToMatrix();
        }

        var packet = checkGT ? sftcore$checkAvailableGTMachine() : AvailableGTMachinesPacket.empty();
        sendPacketToClient(packet);
    }

    @Unique
    private boolean sftcore$checkIsAlreadyCraftable() {
        var pattern = encodedPatternSlot.getItem();
        if (pattern.isEmpty()) {
            return false;
        }
        var player = getPlayer();
        var detail = PatternDetailsHelper.decodePattern(pattern, player.level());
        if (detail == null) {
            return false;
        }
        var output = detail.getPrimaryOutput();
        return ((IGetCraftables) this).sftcore$getCraftables().contains(output.what());
    }

    @Unique
    private boolean sftcore$tryTransferToMatrix() {
        var node = getNetworkNode();
        if (node == null) {
            return false;
        }
        if (!sftcore$transferToMatrix) {
            return false;
        }
        var pattern = encodedPatternSlot.getItem();
        if (pattern.isEmpty()) {
            return false;
        }
        // check pattern type
        if (
            !AEItems.CRAFTING_PATTERN.isSameAs(pattern) && !AEItems.STONECUTTING_PATTERN.isSameAs(pattern) &&
                !AEItems.SMITHING_TABLE_PATTERN.isSameAs(pattern)
        ) {
            return false;
        }

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
        if (thisNode == null) {
            return AvailableGTMachinesPacket.empty();
        }
        var pattern = encodedPatternSlot.getItem();
        if (pattern.isEmpty() || !AEItems.PROCESSING_PATTERN.isSameAs(pattern)) {
            return AvailableGTMachinesPacket.empty();
        }
        SFTCore.LOGGER.info("Trying to check available GT machines to auto transfer");

        var rows = new ArrayList<AvailableGTRow>();
        var rowMap = new Object2ObjectArrayMap<AvailableGTRow, PatternContainer>();
        for (var clazz : thisNode.getGrid().getMachineClasses()) {
            if (PatternContainer.class.isAssignableFrom(clazz)) {
                @SuppressWarnings("unchecked")
                var machineClz = (Class<? extends PatternContainer>) clazz;

                for (var node : thisNode.getGrid().getMachineNodes(machineClz)) {
                    var owner = node.getOwner();
                    if (!machineClz.isInstance(owner)) {
                        continue;
                    }
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
    private void extractPatternFromStorage(ItemStack instance, int decrement) {
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
    private boolean transferStack$skipBlankAndWildcardPattern(RestrictedInputSlot instance, ItemStack stack) {
        return instance.mayPlace(stack) && !stack.is(AEItems.BLANK_PATTERN.asItem()) &&
            !WildcardPatternDecoder.INSTANCE.isEncodedPattern(stack);
    }

    @Unique
    private void sftcore$multiplySlotStack(ConfigInventory inv, int multiplier) {
        for (int i = 0; i < inv.size(); i++) {
            var stack = inv.getStack(i);
            if (stack == null) {
                continue;
            }
            var amount = stack.amount();
            if (multiplier >= 0) {
                amount *= multiplier;
            } else {
                amount /= -multiplier;
                if (amount == 0) {
                    amount = 1;
                }
            }
            if (amount > Integer.MAX_VALUE) {
                amount = Integer.MAX_VALUE;
            }
            inv.setStack(i, new GenericStack(stack.what(), amount));
        }
    }
}
