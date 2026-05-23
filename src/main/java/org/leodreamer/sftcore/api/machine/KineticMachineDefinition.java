package org.leodreamer.sftcore.api.machine;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;

import net.minecraft.resources.ResourceLocation;

import lombok.Getter;
import lombok.Setter;

@Getter
public class KineticMachineDefinition extends MachineDefinition {

    private final boolean source;
    private final float torque;
    /**
     * false: rotation axis is front-facing clockwise axis.
     * true: rotation axis is front-facing axis.
     */
    @Setter
    private boolean frontRotation;

    public KineticMachineDefinition(ResourceLocation id, boolean source, float torque) {
        super(id);
        this.source = source;
        this.torque = torque;
        this.frontRotation = false;
    }
}
