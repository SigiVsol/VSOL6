package be.vsol.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Vector;

public class Reflect {

    /**
     * Recursively get all the fields that have the specified annotation
     */
    public static Vector<Field> getFields(Object object, Class<? extends Annotation> annotationClass) {
        Vector<Field> result = new Vector<>();

        Class<?> currentClass = object.getClass();

        while (!currentClass.getName().equals("java.lang.Object")) {
            Vector<Field> current = new Vector<>();

            for (Field field : currentClass.getDeclaredFields()) {
                if (annotationClass == null || field.getAnnotation(annotationClass) != null) {
                    current.add(field);
                }
            }

            result.addAll(0, current);

            currentClass = currentClass.getSuperclass();
        }

        return result;
    }

    /**
     * Recursively get all the fields of this class type
     */
    public static Vector<Field> getFields(Object object, String simpleClassName) {
        Vector<Field> result = new Vector<>();

        Class<?> currentClass = object.getClass();

        while (!currentClass.getName().equals("java.lang.Object")) {
            Vector<Field> current = new Vector<>();

            for (Field field : currentClass.getDeclaredFields()) {
                if (field.getType().getSimpleName().equals(simpleClassName)) {
                    current.add(field);
                }
            }

            result.addAll(0, current);

            currentClass = currentClass.getSuperclass();
        }

        return result;
    }

}
