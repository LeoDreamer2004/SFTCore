package org.leodreamer.sftcore.integration.ae2.feature;

import org.jetbrains.annotations.NotNull;

public interface IPromptProviderMenu {

    @NotNull
    String sftcore$getPrompt();

    void sftcore$setPrompt(@NotNull String prompt);
}
