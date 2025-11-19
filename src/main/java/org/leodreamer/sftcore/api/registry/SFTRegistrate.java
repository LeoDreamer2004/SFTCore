package org.leodreamer.sftcore.api.registry;

import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import org.jetbrains.annotations.NotNull;

public class SFTRegistrate extends GTRegistrate {
    protected SFTRegistrate(String modId) {
        super(modId);
    }

    @NotNull
    public static SFTRegistrate create(@NotNull String modId) {
        return new SFTRegistrate(modId);
    }
}
