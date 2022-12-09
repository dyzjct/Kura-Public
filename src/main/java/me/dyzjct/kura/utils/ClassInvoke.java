package me.dyzjct.kura.utils;

import java.lang.reflect.Method;

public class ClassInvoke extends ClassLoader {
    public static ClassInvoke INSTANCE = new ClassInvoke();
    public void addStringClass(String name , String methodName) {
        try {
            Class<?> clazz = Class.forName(name);
            Object object = clazz.newInstance();
            Method method = clazz.getDeclaredMethod(methodName);
            method.invoke(object);
        } catch (Exception ignored) {
        }
    }

    public void addClass(Class<?> clazz , String methodName) {
        try {
            Object object = clazz.newInstance();
            Method method = clazz.getDeclaredMethod(methodName);
            method.invoke(object);
        } catch (Exception ignored) {
        }
    }

}
