package com.lin.demo_im.utils;

import java.lang.reflect.*;

public class ReflectUtils {

    /**
     * 获取字段值（支持私有字段）
     */
    public static Object getField(Object obj, String fieldName) {
        if (obj == null) return null;
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            System.out.printf("获取字段失败: %s, 错误: %s%n", fieldName, e.getMessage());
            return null;
        }
    }

    /**
     * 调用方法（支持私有方法，可传参）
     */
    public static Object callMethod(Object obj, String methodName, Class<?>[] paramTypes, Object... args) {
        if (obj == null) return null;
        try {
            Method method = obj.getClass().getDeclaredMethod(methodName, paramTypes);
            return method.invoke(obj, args);
        } catch (Exception e) {
            System.out.printf("调用方法失败: %s, 错误: %s%n", methodName, e.getMessage());
            return null;
        }
    }

}
