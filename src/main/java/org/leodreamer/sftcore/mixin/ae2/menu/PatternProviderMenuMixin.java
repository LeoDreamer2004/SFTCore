package org.leodreamer.sftcore.mixin.ae2.menu;

import org.leodreamer.sftcore.integration.ae2.feature.IPromptProvider;
import org.leodreamer.sftcore.integration.ae2.feature.IPromptProviderMenu;
import org.leodreamer.sftcore.integration.ae2.sync.PromptSyncPacket;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.PatternProviderMenu;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternProviderMenu.class)
public class PatternProviderMenuMixin extends AEBaseMenu implements IPromptProviderMenu {

    @Unique
    private IPromptProvider sftcore$provider = IPromptProvider.EMPTY;

    @Unique
    private static final String SYNC_PROMPT = "syncPrompt";

    @Unique
    private static final String SET_PROMPT = "setPrompt";

    public PatternProviderMenuMixin(
        MenuType<?> menuType,
        int id,
        Inventory playerInventory,
        Object host
    ) {
        super(menuType, id, playerInventory, host);
    }

    @Inject(
        method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lappeng/helpers/patternprovider/PatternProviderLogicHost;)V",
        at = @At("TAIL"),
        remap = false
    )
    private void addProvider(
        MenuType<?> menuType,
        int id,
        Inventory playerInventory,
        PatternProviderLogicHost host,
        CallbackInfo ci
    ) {
        if (host instanceof IPromptProvider provider) {
            this.sftcore$provider = provider;
            registerClientAction(SET_PROMPT, String.class, this::sftcore$setPrompt);
            registerClientAction(SYNC_PROMPT, this::sftcore$getPrompt);
        }
    }

    @Unique
    @Override
    public @NotNull String sftcore$getPrompt() {
        var prompt = sftcore$provider.sftcore$getPrompt();
        if (isClientSide()) {
            sendClientAction(SYNC_PROMPT);
        } else {
            sendPacketToClient(new PromptSyncPacket(prompt));
        }
        return prompt;
    }

    @Unique
    @Override
    public void sftcore$setPrompt(String prompt) {
        if (isClientSide()) {
            sendClientAction(SET_PROMPT, prompt);
        } else {
            sftcore$provider.sftcore$setPrompt(prompt);
        }
    }
}
