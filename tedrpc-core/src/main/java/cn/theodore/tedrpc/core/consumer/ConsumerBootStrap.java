package cn.theodore.tedrpc.core.consumer;

import cn.theodore.tedrpc.core.annotation.TedConsumer;
import cn.theodore.tedrpc.core.api.*;
import cn.theodore.tedrpc.core.util.MethodUtils;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消费端实现类.
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

        // 获取注册中心
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);

        // 初始化rpc上下文
        RpcContext context = new RpcContext();
        // 设置路由
        context.setRouter(router);
        // 设置均衡负载
        context.setLoadBalancer(loadBalancer);

        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);

            List<Field> fields = MethodUtils.findAnnotationField(bean.getClass(), TedConsumer.class);

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
                        consumer = createFromRegistry(service, context, rc);
                        stub.put(serviceName, consumer);
//                        consumer = createConsumer(service, context, List.of(providers));
                    }
                    field.setAccessible(true);
                    field.set(bean, consumer);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    /**
     * 通过 Registry创建consumer
     *
     * @param service
     * @param context
     * @param rc
     * @return
     */
    private Object createFromRegistry(Class<?> service, RpcContext context, RegistryCenter rc) {
        String canonicalName = service.getCanonicalName();
        List<String> providers = mapUrl(rc.fetchAll(canonicalName));
        System.out.println(" ===> map to providers: ");

        rc.subscribe(canonicalName, event -> {
            providers.clear();
            providers.addAll(mapUrl(event.getData()));
        });

        return createConsumer(service, context, providers);
    }

    private List<String> mapUrl(List<String> nodes) {
        return nodes.stream().map(x -> "http://" + x.replace("_", ":")).collect(Collectors.toList());
    }

    /**
     * 创建 consumer(设置代理类)
     *
     * @param service
     * @param context
     * @param providers
     * @return
     */
    private Object createConsumer(Class<?> service, RpcContext context, List<String> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service},
                new TedInvocationHandler(service, context, providers));
    }
}
