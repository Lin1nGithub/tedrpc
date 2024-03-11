package cn.theodore.tedrpc.core.consumer;

import cn.theodore.tedrpc.core.annotation.TedConsumer;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linkuan
 */
@Getter
@Setter
public class ConsumerBootStrap implements ApplicationContextAware {

    @Resource
    private ApplicationContext applicationContext;

    private Map<String, Object> stub = new HashMap<>();

    // 设置接口的代理类
    // 此时已初始化完成
    public void start() {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);

            List<Field> fields = findAnnotationField(bean.getClass());

            if (fields.isEmpty()) {
                continue;
            }

            fields.forEach(field -> {
                try {
                    System.out.println("====>" + field.getName());
                    // 对每个field生成代理
                    Class<?> service = field.getType();
                    // 全限定名称
                    String serviceName = service.getCanonicalName();

                    Object consumer = stub.get(serviceName);
                    if (consumer == null) {
                        // 设置代理类
                        consumer = createConsumer(service);
                    }
                    field.setAccessible(true);
                    field.set(bean, consumer);
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private Object createConsumer(Class<?> service) {
        return Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service},
                new TedInvocationHandler(service));
    }


    private List<Field> findAnnotationField(Class<?> aClass) {
        List<Field> result = new ArrayList<>();

        while (aClass != null) {
            // TedrpcDemoConsumerApplication 启动类是被代理过的 无法直接拿到里面的fields
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(TedConsumer.class)) {
                    result.add(field);
                }
            }

            aClass = aClass.getSuperclass();
        }

        return result;
    }

}
