package be.vsol.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Vector;

public class Reflect {

    public static Vector<Field> getFields(Object object, Class<? extends Annotation> annotationClass) {
        Vector<Field> result = new Vector<>();

        Class<?> currentClass = object.getClass();

        while (!currentClass.getName().equals("java.lang.Object")) {
            Vector<Field> current = new Vector<>();

            for (Field field : currentClass.getDeclaredFields()) {
                if (field.getAnnotation(annotationClass) != null) {
                    current.add(field);
                }
            }

            result.addAll(0, current);

            currentClass = currentClass.getSuperclass();
        }

        return result;
    }

}
