package org.leodreamer.sftcore.integration.ponder.api.annotation;

import org.leodreamer.sftcore.integration.ponder.misc.SFTPonderGroup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PonderScene {
    /**
     * @return The path to the schematic file
     */
    String file();

    /**
     * @return The components this scene is for
     */
    SFTPonderGroup[] groups();
}
