package org.leodreamer.sftcore.common.advancement.trigger;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SimpleGTMachineTrigger<M extends MetaMachine> extends SimpleAnyTrigger {

    public SimpleGTMachineTrigger(ResourceLocation id) {
        super(id);
    }

    public SimpleGTMachineTrigger(String id) {
        super(id);
    }

    /**
     * Helper function to trigger the advancement for the player of the GTM machine. See {@link TriggerUtils#findOwnerOrNearestPlayer(MetaMachine)}.
     *
     * @param machine The GT machine
     */
    public void trigger(M machine) {
        var player = TriggerUtils.findOwnerOrNearestPlayer(machine);
        if (player != null) {
            trigger(player);
        }
    }
}
