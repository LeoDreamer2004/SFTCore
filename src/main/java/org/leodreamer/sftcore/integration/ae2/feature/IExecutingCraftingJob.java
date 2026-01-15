package org.leodreamer.sftcore.integration.ae2.feature;

import org.leodreamer.sftcore.util.ReflectUtils;

import appeng.api.crafting.IPatternDetails;
import appeng.crafting.CraftingLink;
import appeng.crafting.execution.ExecutingCraftingJob;
import appeng.crafting.inv.ListCraftingInventory;

import java.util.Map;

public interface IExecutingCraftingJob {

    CraftingLink sftcore$getLink();

    // ? here is exactly the TASK_PROGESS_CLASS
    Map<IPatternDetails, ?> sftcore$getTasks();

    ListCraftingInventory sftcore$getWaitingFor();

    Class<?> TASK_PROGESS_CLASS = ReflectUtils.getInnerClassByName(ExecutingCraftingJob.class, "TaskProgress");

    static long getProgress(Object progress) {
        // assert TASK_PROGESS_CLASS.isInstance(progress);
        return ReflectUtils.getFieldValue(progress, "value", Long.class);
    }

    static void setProgress(Object progress, long value) {
        // assert TASK_PROGESS_CLASS.isInstance(progress);
        ReflectUtils.setFieldValue(progress, "value", value);
    }
}
