package org.leodreamer.sftcore.common.save;

import org.leodreamer.sftcore.SFTCore;
import org.leodreamer.sftcore.integration.ae2.logic.WirelessGrid;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod.EventBusSubscriber(modid = SFTCore.MOD_ID)
public class WirelessSavedData extends SavedData {

    private List<WirelessGrid> grids = new ArrayList<>();
    private static final String NBT_KEY = "gt_wireless_data";
    public static WirelessSavedData INSTANCE = null;

    private WirelessSavedData() {}

    public WirelessGrid createOrGetGrid(ResourceKey<Level> level, BlockPos center) {
        for (var grid : grids) {
            if (grid.level() == level && grid.center().equals(center)) {
                return grid;
            }
        }

        var grid = new WirelessGrid(level, center);
        grids.add(grid);
        setDirty();
        return grid;
    }

    public void removeGrid(WirelessGrid grid) {
        grids.remove(grid);
        setDirty();
    }

    public static WirelessSavedData load(CompoundTag tag) {
        var data = new WirelessSavedData();
        var list = tag.getList(NBT_KEY, Tag.TAG_COMPOUND);
        data.grids = list.stream()
            .map(CompoundTag.class::cast)
            .map(WirelessGrid::decode)
            .collect(Collectors.toList());
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        var list = new ListTag();
        for (var grid : grids) {
            var nbt = grid.encode();
            list.add(nbt);
        }
        tag.put(NBT_KEY, list);
        return tag;
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            INSTANCE = serverLevel.getDataStorage()
                .computeIfAbsent(WirelessSavedData::load, WirelessSavedData::new, "sftcore_wireless_data");
        }
    }
}
