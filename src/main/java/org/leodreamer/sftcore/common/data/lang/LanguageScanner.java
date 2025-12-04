package org.leodreamer.sftcore.common.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraftforge.fml.ModList;
import org.leodreamer.sftcore.api.annotation.DataGenScanned;
import org.leodreamer.sftcore.api.annotation.RegisterLanguage;
import org.objectweb.asm.Type;

import java.util.Objects;

public class LanguageScanner {
    public static void scan(RegistrateLangProvider provider) {
        Type annoType = Type.getType(DataGenScanned.class);
        for (var data : ModList.get().getAllScanData()) {
            for (var a : data.getAnnotations()) {
                Type annotation = a.annotationType();
                if (Objects.equals(annotation, annoType)) {
                    try {
                        scanIn(Class.forName(a.memberName()), provider);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    static void scanIn(Class<?> clazz, RegistrateLangProvider provider) {
        for (var field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(RegisterLanguage.class)) {
                var anno = field.getAnnotation(RegisterLanguage.class);
                String translation = anno.value();
                field.setAccessible(true);
                try {
                    String key = (String) field.get(null);
                    assert key != null;
                    provider.add(key, translation);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
