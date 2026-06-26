package org.leodreamer.sftcore.common.item.wildcard;

import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;

import com.gregtechceu.gtceu.common.mui.GTGuiTextures;
import com.gregtechceu.gtceu.integration.ae2.gui.AEStackDisplayWidget;

import net.minecraft.world.level.Level;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.GenericStack;
import brachy.modularui.api.drawable.Text;
import brachy.modularui.api.widget.IWidget;
import brachy.modularui.drawable.progress.ProgressDrawable;
import brachy.modularui.utils.Alignment;
import brachy.modularui.utils.Color;
import brachy.modularui.widget.SingleChildWidget;
import brachy.modularui.widgets.ProgressWidget;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.layout.Grid;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@DataGenScanned
public class WildcardIndexPage {

    private static final int PATTERN_CYCLE = 20;
    private static final int SLOT_COUNT = 6;

    private final List<IPatternDetails> patterns;
    private final List<GenericStack> inputStacks = new ArrayList<>(SLOT_COUNT);
    private final List<GenericStack> outputStacks = new ArrayList<>(SLOT_COUNT);

    private SingleChildWidget<?> content;
    private int tick;
    private int patternIndex;

    @RegisterLanguage("Wildcard Pattern Preview")
    public static final String TITLE = "item.sftcore.wildcard_pattern.ui.index.title";

    @RegisterLanguage("%d Patterns Available")
    public static final String PATTERNS_AVAILABLE = "item.sftcore.wildcard_pattern.patterns_available";

    public WildcardIndexPage(WildcardPatternLogic logic, Level level) {
        patterns = logic.generateAllPatterns(level).toList();
        displayPattern(patterns.isEmpty() ? null : patterns.get(0));
    }

    public IWidget createWidget() {
        content = new SingleChildWidget<>()
            .width(WildcardPatternUIProvider.PAGE_WIDTH)
            .height(WildcardPatternUIProvider.EDITOR_HEIGHT);
        refreshContent();
        return content.onUpdateListener(widget -> update());
    }

    private void update() {
        if (patterns.isEmpty() || ++tick % PATTERN_CYCLE != 0) {
            return;
        }

        patternIndex = (patternIndex + 1) % patterns.size();
        displayPattern(patterns.get(patternIndex));
        refreshContent();
    }

    private void refreshContent() {
        if (content == null) {
            return;
        }

        content.child(
            Flow.column()
                .width(WildcardPatternUIProvider.PAGE_WIDTH)
                .height(WildcardPatternUIProvider.EDITOR_HEIGHT / 2)
                .background(GTGuiTextures.DISPLAY)
                .padding(7)
                .childPadding(12)
                .crossAxisAlignment(Alignment.CrossAxis.CENTER)
                .child(
                    Text.lang(PATTERNS_AVAILABLE, patterns.size())
                        .asWidget()
                        .height(16)
                        .color(Color.WHITE.main)
                )
                .child(createPatternPreview())
        );
    }

    private IWidget createPatternPreview() {
        return Flow.row()
            .coverChildren()
            .childPadding(11)
            .crossAxisAlignment(Alignment.CrossAxis.CENTER)
            .child(createStackGrid(inputStacks))
            .child(
                new ProgressWidget()
                    .texture(GTGuiTextures.PROGRESS_ARROW.main(), ProgressDrawable.Direction.RIGHT)
                    .clientValue(() -> 0.5)
                    .size(20, 15)
            )
            .child(createStackGrid(outputStacks));
    }

    private static IWidget createStackGrid(List<GenericStack> stacks) {
        return new Grid()
            .coverChildren()
            .minElementMargin(0)
            .minColWidth(18)
            .minRowHeight(18)
            .gridOfSizeWidth(SLOT_COUNT, 3, (x, y, index) -> new AEStackDisplayWidget(stacks, index));
    }

    private void displayPattern(@Nullable IPatternDetails pattern) {
        inputStacks.clear();
        outputStacks.clear();

        if (pattern == null) {
            return;
        }

        Arrays.stream(pattern.getInputs())
            .map(input -> {
                var possibleInputs = input.getPossibleInputs();
                if (possibleInputs.length == 0) {
                    return null;
                }
                return possibleInputs[0];
            })
            .filter(Objects::nonNull)
            .limit(SLOT_COUNT)
            .forEach(inputStacks::add);

        Arrays.stream(pattern.getOutputs())
            .filter(Objects::nonNull)
            .limit(SLOT_COUNT)
            .forEach(outputStacks::add);
    }
}
