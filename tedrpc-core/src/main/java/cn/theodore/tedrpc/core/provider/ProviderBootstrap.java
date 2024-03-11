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

        // 拿到全限定名 并且进行放置
        providers.values().forEach(this::getInterface);
    }

    private void getInterface(Object x) {
        // 暂时拿第一个
        Class<?> xInterface = x.getClass().getInterfaces()[0];
        // key: 接口全限定名 value: 接口实现类
        skeleton.put(xInterface.getCanonicalName(), x);
    }

    public RpcResponse invoke(RpcRequest request) {
        Object bean = skeleton.get(request.getService());

        RpcResponse rpcResponse = new RpcResponse();
        try {
            // 通过方法名拿到方法
            Method method = findMethod(bean.getClass(), request.getMethod());
            if (method == null) {
                throw new RuntimeException("不存在该方法名:" + request.getMethod());
            }
            // 执行方法
            Object result = method.invoke(bean, request.getArgs());
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

    private Method findMethod(Class<?> aClass, String methodName) {
        for (Method method : aClass.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

}
