package cn.theodore.tedrpc.core.provider;

import cn.theodore.tedrpc.core.annotation.TedProvider;
import cn.theodore.tedrpc.core.api.RegistryCenter;
import cn.theodore.tedrpc.core.api.RpcRequest;
import cn.theodore.tedrpc.core.api.RpcResponse;
import cn.theodore.tedrpc.core.meta.ProviderMeta;
import cn.theodore.tedrpc.core.util.MethodUtils;
import cn.theodore.tedrpc.core.util.TypeUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 服务提供者的启动类
 * @author linkuan
 */
@Getter
@Setter
public class ProviderBootstrap implements ApplicationContextAware {

    @Resource
    private ApplicationContext applicationContext;

    private LinkedMultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    @Value("${server.port}")
    private String port;

    private String instance;

    // bean的属性初始化装配前
    // init-method
    @PostConstruct
    // @PreDestroy 进行销毁
    public void init() {
        // 拿到
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(TedProvider.class);
        providers.forEach((x, y) -> System.out.println(x));

        // 拿到全限定名 并且进行放置
        providers.values().forEach(this::getInterface);

//        String ip = InetAddress.getLocalHost().getHostAddress();
//        this.instance = ip + "_" + port;
        // 每个服务进行注册
        // skeleton.keySet().forEach(this::registerService); // zk就有了 spring还未完成
    }


    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        this.instance = ip + "_" + port;
        skeleton.keySet().forEach(this::registerService); // zk就有了 spring还未完成
    }

    private void registerService(String service) {
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        rc.register(service, instance);
    }

    /**
     * 进行销毁
     */
    @PreDestroy
    public void stop() {
        skeleton.keySet().forEach(this::unRegisterService);
    }

    private void unRegisterService(String service) {
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        rc.unregister(service, instance);
    }

    private void getInterface(Object x) {
        // 暂时拿第一个
        Class<?> xInterface = x.getClass().getInterfaces()[0];
        Method[] methods = xInterface.getMethods();
        for (Method method : methods) {
            if (MethodUtils.checkLocalMethod(method)) {
                continue;
            }
            createProvider(xInterface, x, method);
        }
        // key: 接口全限定名 value: 接口实现类
//        skeleton.put(xInterface.getCanonicalName(), x);
    }

    private void createProvider(Class<?> xInterface, Object x, Method method) {
        ProviderMeta meta = new ProviderMeta();
        meta.setServiceImpl(x);
        meta.setMethod(method);
        meta.setMethodSign(MethodUtils.methodSign(method));
        System.out.println("create a provider: " + meta);
        skeleton.add(xInterface.getCanonicalName(), meta);
    }

    public RpcResponse invoke(RpcRequest request) {
        System.out.println("request ==>" + request);
        String methodSign = request.getMethodSign();

        RpcResponse rpcResponse = new RpcResponse();
        List<ProviderMeta> providerMetas = skeleton.get(request.getService());
        ProviderMeta meta = findProviderMeta(providerMetas, methodSign);

        try {
            // 通过方法名拿到方法
            Method method = meta.getMethod();
            // 入参参数转换
            Object[] args = processArgs(request.getArgs(), method.getParameterTypes());
            // 执行方法
            Object result = method.invoke(meta.getServiceImpl(), args);
            rpcResponse.setCode(200);
            rpcResponse.setMessage("调用成功");
            rpcResponse.setData(result);
        } catch (InvocationTargetException ex) {
            rpcResponse.setMessage("调用失败");
            rpcResponse.setEx(new RuntimeException(ex.getTargetException().getMessage()));
        } catch (IllegalAccessException ex) {
            rpcResponse.setMessage("调用失败");
            rpcResponse.setEx(new RuntimeException(ex.getMessage()));
        }
        return rpcResponse;
    }

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) {
        if (args == null || args.length == 0) {
            return args;
        }
        Object[] actuals = new Object[args.length];
        for (int i = 0; i < actuals.length; i++) {
            actuals[i] = TypeUtils.castType(args[i], parameterTypes[i]);
        }
        return actuals; 
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        Optional<ProviderMeta> optional = providerMetas.stream().filter(x-> x.getMethodSign().equals(methodSign)).findFirst();
        return optional.orElse(null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T convertObject(Object obj) {
        if (obj == null) {
            return null;
        }

        // 对基本类型的包装类进行判断和转换
        if (obj instanceof Integer || obj instanceof Double || obj instanceof Float ||
                obj instanceof Long || obj instanceof Short || obj instanceof Byte ||
                obj instanceof Boolean || obj instanceof Character) {
            return (T) obj; // 直接返回，利用自动装箱
        }

        // 如果有其他类型需要转换处理，可以在这里继续添加逻辑
        // 例如，如果你需要特殊处理String类型
//        if (obj instanceof String) {
//            return (T) obj; // 假设直接返回String对象
//        }

        // 如果不符合以上任何条件，可以返回null或抛出异常
        return null;
    }

    private Method findMethod(Class<?> aClass, String methodName) {
        for (Method method : aClass.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

}
