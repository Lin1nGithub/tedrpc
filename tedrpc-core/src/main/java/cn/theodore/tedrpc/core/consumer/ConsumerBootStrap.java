package cn.theodore.tedrpc.core.consumer;

import cn.theodore.tedrpc.core.annotation.TedConsumer;
import cn.theodore.tedrpc.core.api.LoadBalancer;
import cn.theodore.tedrpc.core.api.Router;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

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
public class ConsumerBootStrap implements ApplicationContextAware, EnvironmentAware {

    @Resource
    private ApplicationContext applicationContext;
    Environment environment;

    private Map<String, Object> stub = new HashMap<>();

    // 设置接口的代理类
    // 此时已初始化完成
    public void start() {

        Router router = applicationContext.getBean(Router.class);
        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);

        String urls = environment.getProperty("tedrpc.providers");
        if (Strings.isBlank(urls)) {
            System.out.println("tedrpc.providers is empty.");
        }
        String[] providers = urls.split(",");

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
                        consumer = createConsumer(service, router, loadBalancer, providers);
                    }
                    field.setAccessible(true);
                    field.set(bean, consumer);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private Object createConsumer(Class<?> service, Router router, LoadBalancer loadBalancer, String[] providers) {
        return Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service},
                new TedInvocationHandler(service, router,  loadBalancer, providers));
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
