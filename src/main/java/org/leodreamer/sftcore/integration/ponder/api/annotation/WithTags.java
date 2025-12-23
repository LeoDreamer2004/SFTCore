package org.leodreamer.sftcore.integration.ponder.api.annotation;

import org.leodreamer.sftcore.integration.ponder.misc.SFTPonderTag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface WithTags {

    /**
     * @return The tags for the scene
     */
    SFTPonderTag[] value();
}
