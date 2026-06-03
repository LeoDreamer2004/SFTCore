package org.leodreamer.sftcore.integration.ponder.api.annotation;

import org.leodreamer.sftcore.integration.ponder.misc.SFTPonderTag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Simple marker for tags in Ponder.
 * When annotated to classes, this means all the methods with {@link PonderScene}
 * are treated with this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface WithPonderTags {

    /**
     * @return The tags for the scene
     */
    SFTPonderTag[] value();
}
