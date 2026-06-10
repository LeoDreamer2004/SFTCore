package org.leodreamer.sftcore.api.kinetics;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.leodreamer.sftcore.api.gui.SFTGuiTextures;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TooltipsPanel;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@DataGenScanned
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WorkableKineticMultiblockMachine extends WorkableMultiblockMachine
    implements IFancyUIMachine, IDisplayUIMachine {

    public WorkableKineticMultiblockMachine(BlockEntityCreationInfo info) {
        super(info);
    }

    @Override
    public WorkableKineticMultiblockMachine self() {
        return this;
    }

    @RegisterLanguage("Input RPM: %s RPM")
    static final String INPUT_RPM = "sftcore.multiblock.kinetic.input_rpm";

    @RegisterLanguage("Available Stress: %ssu")
    static final String AVAILABLE_STRESS = "sftcore.multiblock.kinetic.available_stress";

    @Override
    public void addDisplayText(List<Component> textList) {
        int numParallels;
        int subtickParallels;
        int totalRuns;
        boolean exact = false;

        if (recipeLogic.isActive() && recipeLogic.getLastRecipe() != null) {
            numParallels = recipeLogic.getLastRecipe().parallels;
            subtickParallels = recipeLogic.getLastRecipe().subtickParallels;
            totalRuns = recipeLogic.getLastRecipe().getTotalRuns();
            exact = true;
        } else {
            numParallels = 0;
            subtickParallels = 0;
            totalRuns = 0;
        }

        var builder = MultiblockDisplayText.builder(textList, isFormed())
            .setWorkingStatus(recipeLogic.isWorkingEnabled(), recipeLogic.isActive())
            .addMachineModeLine(getRecipeType(), getRecipeTypes().length > 1)
            .addCustom(this::addKineticLines)
            .addTotalRunsLine(totalRuns)
            .addParallelsLine(numParallels, exact)
            .addSubtickParallelsLine(subtickParallels)
            .addWorkingStatusLine()
            .addProgressLine(recipeLogic)
            .addRecipeFailReasonLine(recipeLogic);

        if (recipeLogic.getLastRecipe() != null) {
            builder.addOutputLines(recipeLogic.getLastRecipe());
        }

        getDefinition().getAdditionalDisplay().accept(this, textList);
        IDisplayUIMachine.super.addDisplayText(textList);
    }

    private void addKineticLines(List<Component> textList) {
        if (!isFormed()) {
            return;
        }

        textList.add(
            Component.translatable(
                INPUT_RPM,
                KineticRecipeHelper.format(KineticRecipeHelper.getMaxInputRPM(recipeLogic))
            )
                .withStyle(ChatFormatting.GRAY)
        );
        textList.add(
            Component.translatable(
                AVAILABLE_STRESS,
                KineticRecipeHelper.format(KineticRecipeHelper.getTotalAvailableStress(recipeLogic))
            )
                .withStyle(ChatFormatting.GRAY)
        );
    }

    @Override
    public IGuiTexture getScreenTexture() {
        return SFTGuiTextures.DISPLAY_CREATE;
    }

    @Override
    public Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, 182 + 8, 117 + 8);
        group.addWidget(
            new DraggableScrollableWidgetGroup(4, 4, 182, 117).setBackground(getScreenTexture())
                .addWidget(new LabelWidget(4, 5, self().getBlockState().getBlock().getDescriptionId()))
                .addWidget(
                    new ComponentPanelWidget(4, 17, this::addDisplayText)
                        .textSupplier(this.getLevel().isClientSide ? null : this::addDisplayText)
                        .setMaxWidthLimit(200)
                        .clickHandler(this::handleDisplayClick)
                )
        );
        return group;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        return new ModularUI(198, 208, this, entityPlayer).widget(new FancyMachineUIWidget(this, 198, 208));
    }

    @Override
    public List<IFancyUIProvider> getSubTabs() {
        return getParts().stream().map(IFancyUIProvider.class::cast).toList();
    }

    @Override
    public void attachTooltips(TooltipsPanel tooltipsPanel) {
        for (var part : getParts()) {
            part.attachFancyTooltipsToController(this, tooltipsPanel);
        }
    }
}
