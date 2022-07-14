package org.example.processor;


import org.example.annotations.SetDefaultString;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class DefaultStringInitializer {

    private DefaultStringInitializer() {
        throw new RuntimeException("Can not create object of this class");
    }

    public static void setDefaults(Object parent) {
        for (Field field: parent.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(SetDefaultString.class)) {

                SetDefaultString setDefaultString = field.getAnnotation(SetDefaultString.class);

                field.setAccessible(true);
                if (Modifier.isFinal(field.getModifiers())) {
                    throw new RuntimeException("Can not set value to the final field!");
                }
                try {
                    Object obj = field.get(parent);

                    if (obj instanceof String) {
                        field.set(parent, setDefaultString.string());
                    } else {
                        throw new RuntimeException("Annotation @SetDefaultString is applicable only to String fields!");
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }




}
