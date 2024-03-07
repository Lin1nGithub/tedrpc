package cn.theodore.tedrpc.core.provider;

import cn.theodore.tedrpc.core.annotation.TedProvider;
import cn.theodore.tedrpc.core.api.RpcRequest;
import cn.theodore.tedrpc.core.api.RpcResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author linkuan
 */
@Getter
@Setter
public class ProviderBootstrap implements ApplicationContextAware {

    @Resource
    private ApplicationContext applicationContext;

    private Map<String, Object> skeleton = new HashMap<>();


    // bean的属性初始化装配前 todo 找出其他的装配办法
    // init-method
    @PostConstruct
    // @PreDestroy 进行销毁
    public void buildProviders() {
        // 拿到
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(TedProvider.class);
        providers.forEach((x,y) -> System.out.println(x));

        providers.values().forEach(x -> getInterface(x));
    }

    private void getInterface(Object x) {
        // 暂时拿第一个
        Class<?> xInterface = x.getClass().getInterfaces()[0];
        // key: 接口全限定名 value: 接口实现类
        skeleton.put(xInterface.getCanonicalName(), x);
    }

    public RpcResponse invoke(RpcRequest request) {
        Object bean = skeleton.get(request.getService());
        try {
            // 通过方法名拿到方法
            Method method = findMethod(bean.getClass(), request.getMethod());
            if (method == null) {
                throw new RuntimeException("不存在该方法名:" + request.getMethod());
            }
            // 执行方法
            Object result = method.invoke(bean, request.getArgs());
            return new RpcResponse(200, result,"调用成功");
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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
