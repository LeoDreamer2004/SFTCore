package org.leodreamer.sftcore.util;

import org.spongepowered.asm.mixin.Unique;

import java.lang.reflect.Field;

public class ReflectUtils {
    @Unique
    public static <T> T getFieldValue(Object obj, String fieldName, Class<T> fieldType) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return fieldType.cast(field.get(obj));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get field value: " + fieldName, e);
        }
    }
}
