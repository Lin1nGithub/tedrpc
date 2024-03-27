package cn.theodore.tedrpc.core.util;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

/**
 * @author linkuan
 */
public class MockUtils {

    public static Object mock(Class type) {
        if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return 1;
        }else if (type.equals(Long.class) || type.equals(Long.TYPE)) {
            return 10000L;
        }
        if (Number.class.isAssignableFrom(type)) {
            return 1;
        }
        if (type.equals(String.class)) {
            return "this_is_mock_string";
        }
        return mockPojo(type);
    }

    @SneakyThrows
    private static Object mockPojo(Class type) {
        Object result = type.getDeclaredConstructor().newInstance();
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fType = field.getType();
            field.set(result, mock(fType));
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(mock(User.class));
    }

    @Getter
    @Setter
    public static class User {

        private String name;

        private int age;

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

}
