package org.leodreamer.sftcore.integration.ae2.feature;

import org.jetbrains.annotations.NotNull;

public interface IPromptProvider {

    @NotNull
    String sftcore$getPrompt();

    void sftcore$setPrompt(@NotNull String prompt);

    IPromptProvider EMPTY = new IPromptProvider() {

        @Override
        public @NotNull String sftcore$getPrompt() {
            return "";
        }

        @Override
        public void sftcore$setPrompt(@NotNull String prompt) {}
    };
}
