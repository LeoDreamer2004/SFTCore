package org.leodreamer.sftcore.mixin.ae2;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.ITerminalHost;
import appeng.core.definitions.AEItems;
import appeng.helpers.IMenuCraftingPacket;
import appeng.helpers.IPatternTerminalMenuHost;
import appeng.menu.guisync.GuiSync;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.RestrictedInputSlot;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixPattern;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.leodreamer.sftcore.api.feature.ISendToAssemblyMatrix;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternEncodingTermMenu.class)
public abstract class PatternEncodingTermMenuMixin extends MEStorageMenu implements IMenuCraftingPacket, ISendToAssemblyMatrix {

    @Unique
    private static final AEItemKey sftcore$key = AEItemKey.of(AEItems.BLANK_PATTERN);

    @Shadow(remap = false)
    @Final
    private RestrictedInputSlot blankPatternSlot;

    @Unique
    @GuiSync(150)
    public boolean sftcore$transferToMatrix = true;

    @Shadow(remap = false)
    @Final
    private RestrictedInputSlot encodedPatternSlot;

    @Unique
    private static final String TRANSFER_TO_MATRIX = "transferToMatrix";

    public PatternEncodingTermMenuMixin(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lappeng/helpers/IPatternTerminalMenuHost;Z)V",
            at = @At("TAIL"),
            remap = false)
    private void initPattern(MenuType<?> menuType, int id, Inventory ip, IPatternTerminalMenuHost host, boolean bindInventory, CallbackInfo ci) {
        blankPatternSlot.setAllowEdit(false);
        blankPatternSlot.setStackLimit(Integer.MAX_VALUE);
        registerClientAction(TRANSFER_TO_MATRIX, Boolean.class, this::sftcore$setTransferToMatrix);
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
        if (left == 0) return;
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
    private void transferPatternToMatrixAfterEncoding(CallbackInfo ci) {
        if (!sftcore$transferToMatrix) {
            return;
        }

        var node = getNetworkNode();
        if (node == null) return;

        var pattern = encodedPatternSlot.getItem();
        // assembly matrix only accept crafting pattern
        if (!sftcore$acceptedByMatrix(pattern)) return;

        for (var mat : node.getGrid().getMachines(TileAssemblerMatrixPattern.class)) {
            var inv = mat.getPatternInventory();
            if (!pattern.isEmpty()) {
                pattern = inv.addItems(pattern); // overflow, try on
            }
            if (pattern.isEmpty()) {
                encodedPatternSlot.set(ItemStack.EMPTY);
                break;
            }
        }
    }

    @Unique
    private static boolean sftcore$acceptedByMatrix(ItemStack pattern) {
        return AEItems.CRAFTING_PATTERN.isSameAs(pattern)
                || AEItems.STONECUTTING_PATTERN.isSameAs(pattern)
                || AEItems.SMITHING_TABLE_PATTERN.isSameAs(pattern);
    }

    @Inject(method = "encode",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V",
                    remap = false),
            remap = false)
    private void extractPatternFromStorage(CallbackInfo ci, @Local(ordinal = 1) ItemStack blankPattern) {
        if (storage != null) {
            storage.extract(sftcore$key, 1, Actionable.MODULATE, getActionSource());
        }
    }

    @Redirect(method = "transferStackToMenu",
            at = @At(value = "INVOKE", target = "Lappeng/menu/slot/RestrictedInputSlot;mayPlace(Lnet/minecraft/world/item/ItemStack;)Z", remap = true),
            remap = false)
    private boolean transferStack$skipBlankPattern(RestrictedInputSlot instance, ItemStack itemStack) {
        return instance.mayPlace(itemStack) && itemStack.getItem() != AEItems.BLANK_PATTERN.asItem();
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
}
