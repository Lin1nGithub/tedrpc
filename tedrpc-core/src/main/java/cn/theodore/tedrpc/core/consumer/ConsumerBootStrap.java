package cn.theodore.tedrpc.core.consumer;

import cn.theodore.tedrpc.core.annotation.TedConsumer;
import cn.theodore.tedrpc.core.api.*;
import cn.theodore.tedrpc.core.meta.InstanceMeta;
import cn.theodore.tedrpc.core.meta.ServiceMeta;
import cn.theodore.tedrpc.core.util.MethodUtils;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消费端实现类.
 *
 * @author linkuan
 */
@Getter
@Setter
@Slf4j
public class ConsumerBootStrap implements ApplicationContextAware, EnvironmentAware {

    @Resource
    private ApplicationContext applicationContext;
    Environment environment;

    private Map<String, Object> stub = new HashMap<>();

    @Value("${app.id}")
    private String app;

    @Value("${app.namespace}")
    private String namespace;

    @Value("${app.env}")
    private String env;

    @Value("${app.retries}")
    private Integer retries;

    @Value("${app.timeout}")
    private int timeout;

    // 设置接口的代理类
    // 此时已初始化完成
    public void start() {

        Router<InstanceMeta> router = applicationContext.getBean(Router.class);
        LoadBalancer<InstanceMeta> loadBalancer = applicationContext.getBean(LoadBalancer.class);
        List<Filter> filters = applicationContext.getBeansOfType(Filter.class).values().stream().toList();

        // 获取注册中心
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);

        // 初始化rpc上下文
        RpcContext context = new RpcContext();
        // 设置路由
        context.setRouter(router);
        // 设置均衡负载
        context.setLoadBalancer(loadBalancer);
        // 设置过滤器
        context.setFilters(filters);
        context.getParameters().put("app.retries", retries + "");
        context.getParameters().put("app.timeout", timeout + "");
       // context.getParameters().put("app.grayRatio", grayRatio + "");

        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);

            List<Field> fields = MethodUtils.findAnnotationField(bean.getClass(), TedConsumer.class);

            if (fields.isEmpty()) {
                continue;
            }

            fields.forEach(field -> {
                try {
                    log.info("====>" + field.getName());
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
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service.getCanonicalName())
                .app(app)
                .namespace(namespace)
                .env(env)
                .build();
        List<InstanceMeta> providers = rc.fetchAll(serviceMeta);
        log.info(" ===> map to providers: ");

        rc.subscribe(serviceMeta, event -> {
            providers.clear();
            providers.addAll(event.getData());
        });

        return createConsumer(service, context, providers);
    }

    /**
     * 创建 consumer(设置代理类)
     *
     * @param service
     * @param context
     * @param providers
     * @return
     */
    private Object createConsumer(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(),
                new Class[]{service},
                new TedInvocationHandler(service, context, providers));
    }
}
