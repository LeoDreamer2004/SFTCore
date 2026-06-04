package org.leodreamer.sftcore.common.item.terminal.api;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.LinkedHashMap;
import java.util.Map;

@DataGenScanned
public class BuildReport {

    public int placed;
    public int existing;
    public int blocked;
    public int missing;
    public int failed;

    public final Map<Item, Integer> missingItems = new LinkedHashMap<>();

    public void addMissing(Item item) {
        missing++;
        missingItems.merge(item, 1, Integer::sum);
    }

    @RegisterLanguage("Please sneak right click a valid Mekanism multiblock starting point")
    public static final String INVALID_START = "item.sftcore.mek_terminal.invalid_start";

    @RegisterLanguage("Build complete: Placed %s, Existing %s, Blocked %s，Missing %s，Failed %s")
    private static final String REPORT = "item.sftcore.mek_terminal.build_report";

    public Component toComponent() {
        return Component.translatable(
            REPORT,
            placed,
            existing,
            blocked,
            missing,
            failed
        );
    }
}
