package org.example.processor;

import org.example.annotations.ClassInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ShowClassInfo {


    private ShowClassInfo() {
        throw new RuntimeException("Can not create object");
    }

    public static void show(Object parent) {

        Class<?> cls = parent.getClass();

        if (cls.isAnnotationPresent(ClassInfo.class)) {

            ClassInfo classInfo = cls.getAnnotation(ClassInfo.class);

            System.out.println("Info about class "+cls.getSimpleName()+":\n");

            if (classInfo.fields()) {
                System.out.println("Fields:\n");
                for (Field field: cls.getDeclaredFields()) {
                    System.out.println(field.toString());
                }
                System.out.println();
            }
            if (classInfo.methods()) {
                System.out.println("Methods:\n");
                for (Method method: cls.getDeclaredMethods()) {
                    System.out.println(method.toString());
                }
            }
        } else {
            System.out.println("Can not show class info");
        }
    }
}