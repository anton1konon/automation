package org.example.processor;

import org.example.annotations.Regex;

import java.lang.reflect.Field;

public class RegexValidator {

    private RegexValidator() {
        throw new RuntimeException("Can not create object of this class");
    }

    public static boolean isValid(Object parent) {
        for (Field field : parent.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Regex.class)) {
                Regex annotation = field.getAnnotation(Regex.class);
                field.setAccessible(true);

                try {
                    Object obj = field.get(parent);
                    if (obj instanceof String) {
                        String str = (String) obj;
                        String regex = annotation.regex();
                        if (!str.matches(regex)) {
                            return false;
                        };
                    } else {
                        throw new RuntimeException("Annotation @PhoneFormat is applicable only to String fields!");
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }




}
