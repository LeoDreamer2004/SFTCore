package org.leodreamer.sftcore.integration.ae2.feature;

public interface IPromptProvider {

    String sftcore$getPrompt();

    void sftcore$setPrompt(String prompt);

    IPromptProvider EMPTY = new IPromptProvider() {

        @Override
        public String sftcore$getPrompt() {
            return "";
        }

        @Override
        public void sftcore$setPrompt(String prompt) {}
    };
}
