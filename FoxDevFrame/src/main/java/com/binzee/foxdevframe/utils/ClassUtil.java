package com.binzee.foxdevframe.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具类
 *
 * @author 狐彻
 * 2020/10/25 15:31
 */
public class ClassUtil {
    private static final String TAG = "ClassUtil";

    //目标Class
    @NonNull
    private final Class<?> cls;

    private ClassUtil(@NonNull Class<?> cls) {
        this.cls = cls;
    }

    @NonNull
    public static ClassUtil with(@NonNull Class<?> cls) {
        return new ClassUtil(cls);
    }

    @Nullable
    public static ClassUtil with(@NonNull String clsName) throws ClassNotFoundException {
        Class<?> cls = Class.forName(clsName);
        return new ClassUtil(cls);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 工具方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 调用方法
     *
     * @param target     调用对象
     * @param methodName 方法名
     * @param args       方法参数
     * @return 方法返回值
     * @author 狐彻 2020/10/25 15:43
     */
    public Object invokeMethod(Object target, String methodName, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?>[] argsClasses = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++)
            argsClasses[i] = args[i].getClass();
        Method method = cls.getMethod(methodName, argsClasses);
        return method.invoke(target, args);
    }

    /**
     * 获取字段值
     *
     * @return 获取失败则返回null
     * @author 狐彻 2020/10/25 15:47
     */
    public Object getFieldValue(Object target, String fieldName) {
        try {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LogUtil.e(TAG, "getFieldValue: 获取字段值失败", e);
            return null;
        }
    }

    /**
     * 设置字段值
     *
     * @author 狐彻 2020/10/25 15:50
     */
    public void setFieldValue(Object target, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = cls.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
