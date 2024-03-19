package cn.theodore.tedrpc.core.util;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;

/**
 * @author linkuan
 */
public class TypeUtils {

    public static Object castType(Object origin, Class<?> type) {
        if (null == origin) {
            return null;
        }
        Class<?> aClass = origin.getClass();
        // 如果需要转换的类型是 需要的类型的子类 那么直接返回
        if (type.isAssignableFrom(aClass)) {
            return origin;
        }

        if (origin instanceof HashMap map) {
            JSONObject jsonObject = new JSONObject(map);
            return jsonObject.toJavaObject(type);
        }


        if (type.isArray()) {
            Object[] arr;
            if (origin instanceof List list) {
                origin = list.toArray();
            }
            int length = Array.getLength(origin);
            Class<?> componentType = type.getComponentType();
            Object resultArray = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                if (componentType.isPrimitive() ||
                        componentType.getPackageName().startsWith("java")) {
                    Array.set(resultArray, i, Array.get(origin, i));
                }
                // 基本类型或者是jdk自带的类型 其实就是我们自定义的类型
                else {
                    Object caseObject = castType(Array.get(origin, i), componentType);
                    Array.set(resultArray, i, caseObject);
                }
            }

            return resultArray;
        }

        // 7个 基本类型处理
        // 当返回值是Long或者long
        if (type.equals(Long.class) || type.equals(Long.TYPE)) {
            return Long.valueOf(origin.toString());
        }

        // 返回值是Integer 或者 int
        if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return Integer.valueOf(origin.toString());
        }

        // Float 或者 float
        if (type.equals(Float.class) || type.equals(Float.TYPE)) {
            return Integer.valueOf(origin.toString());
        }

        if (type.equals(Double.class) || type.equals(Double.TYPE)) {
            return Double.valueOf(origin.toString());
        }

        if (type.equals(Byte.class) || type.equals(Byte.TYPE)) {
            return Byte.valueOf(origin.toString());
        }

        if (type.equals(Short.class) || type.equals(Short.TYPE)) {
            return Short.valueOf(origin.toString());
        }

        if (type.equals(Character.class) || type.equals(Character.TYPE)) {
            return Character.valueOf(origin.toString().charAt(0));
        }

        return null;
    }
}
