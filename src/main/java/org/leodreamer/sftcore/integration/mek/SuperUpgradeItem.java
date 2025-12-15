package org.leodreamer.sftcore.integration.mek;

import mekanism.api.Upgrade;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.tile.interfaces.IUpgradeTile;
import mekanism.common.util.WorldUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@DataGenScanned
public class SuperUpgradeItem extends Item {
    private final Upgrade upgrade;

    public SuperUpgradeItem(Properties properties, Upgrade upgrade) {
        super(properties.rarity(Rarity.RARE).stacksTo(1));
        this.upgrade = upgrade;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        Level world = context.getLevel();
        BlockEntity tile = WorldUtils.getTileEntity(world, context.getClickedPos());
        if (tile instanceof IUpgradeTile upgradeTile && upgradeTile.supportsUpgrades()) {
            TileComponentUpgrade component = upgradeTile.getComponent();
            if (component.supports(this.upgrade)) {
                if (!world.isClientSide) {
                    component.addUpgrades(this.upgrade, Integer.MAX_VALUE);
                }

                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @RegisterLanguage("Can be used infinitely. Press shift and right click the machine to install the update")
    static final String TOOLTIP = "sftcore.item.super_upgrade.tooltip";

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, components, isAdvanced);
        components.add(Component.translatable(TOOLTIP).withStyle(ChatFormatting.YELLOW));
    }
}
