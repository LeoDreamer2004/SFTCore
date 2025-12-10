package org.leodreamer.sftcore.api.machine;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;

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