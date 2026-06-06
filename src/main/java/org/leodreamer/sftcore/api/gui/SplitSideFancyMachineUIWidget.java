package org.leodreamer.sftcore.api.gui;

import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;

import java.util.ArrayList;

public class SplitSideFancyMachineUIWidget extends FancyMachineUIWidget {

    private final RightSideTabsWidget rightSideTabsWidget;

    public SplitSideFancyMachineUIWidget(
        IFancyUIProvider mainPage,
        int width,
        int height
    ) {
        super(mainPage, width, height);

        this.rightSideTabsWidget = new RightSideTabsWidget(this::navigate, width, 0, 24, height);
        addWidget(rightSideTabsWidget);
    }

    @Override
    protected void setupSideTabs(IFancyUIProvider currentHomePage) {
        this.sideTabsWidget.clearSubTabs();

        var subTabs = new ArrayList<>(currentHomePage.getSubTabs());

        int total = subTabs.size() + 1;
        int rightCount = total / 2;
        int leftSubCount = Math.max(0, subTabs.size() - rightCount);

        this.sideTabsWidget.setMainTab(currentHomePage);

        for (int i = 0; i < leftSubCount; i++) {
            this.sideTabsWidget.attachSubTab(subTabs.get(i));
        }

        this.rightSideTabsWidget.setTabs(
            subTabs.subList(leftSubCount, subTabs.size())
        );
    }

    @Override
    protected void setupFancyUI(
        IFancyUIProvider fancyUI,
        boolean showInventory
    ) {
        super.setupFancyUI(fancyUI, showInventory);

        var pageSize = this.pageContainer.getSize();

        this.rightSideTabsWidget.setSelfPosition(
            new Position(pageSize.width - 4, 0)
        );
        this.rightSideTabsWidget.setSize(
            new Size(24, pageSize.height)
        );
        this.rightSideTabsWidget.selectTab(fancyUI);

        boolean visible = this.sideTabsWidget.isVisible()
            && !this.rightSideTabsWidget.isEmpty();

        this.rightSideTabsWidget.setVisible(visible);
        this.rightSideTabsWidget.setActive(visible);
    }

    @Override
    protected void openPageSwitcher(ClickData clickData) {
        super.openPageSwitcher(clickData);

        this.rightSideTabsWidget.setVisible(false);
        this.rightSideTabsWidget.setActive(false);
    }
}
