package org.example.processor;

import org.example.annotations.RunWithDouble;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class WithDoubleRunner {


    private WithDoubleRunner() {
        throw new RuntimeException("Can not create object of the class");
    }

    public static void run(Object parent) {
        for (Method method : parent.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(RunWithDouble.class)) {
                RunWithDouble annotation = method.getAnnotation(RunWithDouble.class);
                method.setAccessible(true);
                if (method.getParameterCount() != 1) {
                    throw new RuntimeException("Annotation @RunWithDouble is applicable only to methods with one parameter!");
                }
                if (!Arrays.stream(method.getParameterTypes()).allMatch(t -> t.toString().equals("double"))) {
                    throw new RuntimeException("Annotation @RunWithDouble is applicable only to methods with one double type parameter!");
                }
                try {
                    method.invoke(parent, annotation.doubleValue());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }


}
