package cn.theodore.tedrpc.core.util;

import cn.theodore.tedrpc.core.annotation.TedConsumer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * method方法处理类
 * @author linkuan
 */
public class MethodUtils {

    /**
     * 判断方法是否为本地方法
     * @param method
     * @return
     */
    public static boolean checkLocalMethod(final String method) {
        // 本地方法不执行
        if ("toString".equals(method) ||
                "hashCode".equals(method) ||
                "notifyAll".equals(method) ||
                "equals".equals(method) ||
                "wait".equals(method) ||
                "getClass".equals(method) ||
                "notify".equals(method)) {
            return true;
        }
        return false;
    }

    /**
     * 判断方法是否为本地方法的优雅写法
     * @param method
     * @return
     */
    public static boolean checkLocalMethod(final Method method) {
        return method.getDeclaringClass().equals(Object.class);
    }

    /**
     * UserService 存在
     * cn.theodore.tedrpc.demo.api.UserService#findById(java.lang.Integer)
     * cn.theodore.tedrpc.demo.api.UserService#findById(java.lang.Integer, java.lang.String)
     * 同方法名 但入参不一样的方法
     * 需要通过方法签名进行区分 不然会有冲突
     *
     * 使用方法签名优于每次接口调用时用反射解析方法的类型和入参
     * @param method
     * @return
     */
    public static String methodSign(final Method method) {
        StringBuilder sb = new StringBuilder(method.getName());
        sb.append("@").append(method.getParameterCount());
        Arrays.stream(method.getParameterTypes()).forEach(c -> {
            sb.append("_").append(c.getCanonicalName());
        });
        return sb.toString();
    }

    public static List<Field> findAnnotationField(Class<?> aClass, Class<? extends Annotation> annotationClass) {
        List<Field> result = new ArrayList<>();

        while (aClass != null) {
            // TedrpcDemoConsumerApplication 启动类是被代理过的 无法直接拿到里面的fields
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(annotationClass)) {
                    result.add(field);
                }
            }

            aClass = aClass.getSuperclass();
        }

        return result;
    }
}
