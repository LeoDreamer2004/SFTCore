package org.leodreamer.sftcore.api.machine;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;

import net.minecraft.resources.ResourceLocation;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class KineticMachineDefinition extends MachineDefinition {

    @Getter
    public final boolean isSource;
    @Getter
    public final float torque;

    @Getter
    @Setter
    public boolean frontRotation;

    public KineticMachineDefinition(ResourceLocation id, boolean isSource, float torque) {
        super(id);
        this.isSource = isSource;
        this.torque = torque;
    }
}
