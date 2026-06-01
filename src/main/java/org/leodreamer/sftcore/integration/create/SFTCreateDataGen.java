package org.leodreamer.sftcore.integration.create;

import net.minecraftforge.data.event.GatherDataEvent;

import static org.leodreamer.sftcore.integration.create.SFTCreateRecipeGen.*;

public class SFTCreateDataGen {

    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();

        boolean run = event.includeServer();

        generator.addProvider(run, new Pressing(output));
        generator.addProvider(run, new Deploying(output));
        generator.addProvider(run, new Milling(output));
        generator.addProvider(run, new Crushing(output));
        generator.addProvider(run, new SequencedAssembly(output));
    }
}
