package cn.theodore.tedrpc.core.provider;

import cn.theodore.tedrpc.core.annotation.TedProvider;
import cn.theodore.tedrpc.core.api.RegistryCenter;
import cn.theodore.tedrpc.core.config.AppConfigProperties;
import cn.theodore.tedrpc.core.config.ProviderConfigProperties;
import cn.theodore.tedrpc.core.meta.InstanceMeta;
import cn.theodore.tedrpc.core.meta.ProviderMeta;
import cn.theodore.tedrpc.core.meta.ServiceMeta;
import cn.theodore.tedrpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Map;

/**
 * 服务提供者的启动类
 *
 * @author linkuan
 */
@Getter
@Setter
@Slf4j
public class ProviderBootstrap implements ApplicationContextAware {

    @Resource
    private ApplicationContext applicationContext;
    private LinkedMultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();
    private String port;
    private InstanceMeta instance;
    private RegistryCenter rc;
    private AppConfigProperties appProperties;
    private ProviderConfigProperties providerProperties;

    public ProviderBootstrap(String port, AppConfigProperties appConfigProperties,
                             ProviderConfigProperties providerProperties) {
        this.port = port;
        this.appProperties = appConfigProperties;
        this.providerProperties = providerProperties;
    }


    // bean的属性初始化装配前
    // init-method
    @PostConstruct
    // @PreDestroy 进行销毁
    public void init() {
        // 拿到
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(TedProvider.class);
        providers.forEach((x, y) -> log.info(x));

        // 拿到全限定名 并且进行放置
        providers.values().forEach(this::getInterface);

        rc = applicationContext.getBean(RegistryCenter.class);

//        String ip = InetAddress.getLocalHost().getHostAddress();
//        this.instance = ip + "_" + port;
        // 每个服务进行注册
        // skeleton.keySet().forEach(this::registerService); // zk就有了 spring还未完成
    }


    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        this.instance = InstanceMeta.http(ip, Integer.valueOf(port));
        this.instance.getParameters().putAll(providerProperties.getMetas());
        rc.start();
        skeleton.keySet().forEach(this::registerService); // zk就有了 spring还未完成
    }

    private void registerService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service).app(appProperties.getId())
                .namespace(appProperties.getNamespace()).env(appProperties.getEnv())
                .build();
        rc.register(serviceMeta, instance);
    }

    /**
     * 进行销毁
     */
    @PreDestroy
    public void stop() {
        log.info(" ====> unreg all services.");
        skeleton.keySet().forEach(this::unRegisterService);
        rc.stop();
    }

    private void unRegisterService(String service) {
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service).app(appProperties.getId())
                .namespace(appProperties.getNamespace()).env(appProperties.getEnv())
                .build();
        rc.unregister(serviceMeta, instance);
    }

    private void getInterface(Object impl) {
        // 暂时拿第一个
        Class<?> xInterface = impl.getClass().getInterfaces()[0];
        Method[] methods = xInterface.getMethods();
        for (Method method : methods) {
            if (MethodUtils.checkLocalMethod(method)) {
                continue;
            }
            createProvider(xInterface, impl, method);
        }
        // key: 接口全限定名 value: 接口实现类
//        skeleton.put(xInterface.getCanonicalName(), x);
    }

    private void createProvider(Class<?> service, Object impl, Method method) {
        ProviderMeta meta = ProviderMeta.builder()
                .method(method)
                .methodSign(MethodUtils.methodSign(method))
                .ServiceImpl(impl)
                .build();
        log.info("create a provider: " + meta);
        skeleton.add(service.getCanonicalName(), meta);
    }
}
