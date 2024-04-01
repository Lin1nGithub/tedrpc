package cn.theodore.tedrpc.core.provider;

import cn.theodore.tedrpc.core.api.RpcRequest;
import cn.theodore.tedrpc.core.api.RpcResponse;
import cn.theodore.tedrpc.core.api.TedrpcException;
import cn.theodore.tedrpc.core.meta.ProviderMeta;
import cn.theodore.tedrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * @author linkuan
 */
@Slf4j
public class ProviderInvoker {

    private LinkedMultiValueMap<String, ProviderMeta> skeleton;

    public ProviderInvoker(ProviderBootstrap providerBootstrap) {
        this.skeleton = providerBootstrap.getSkeleton();
    }

    public RpcResponse invoke(RpcRequest request) {
        log.info("request ==>" + request);
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
            rpcResponse.setEx(new TedrpcException(ex.getTargetException().getMessage()));
        } catch (IllegalAccessException ex) {
            rpcResponse.setMessage("调用失败");
            rpcResponse.setEx(new TedrpcException(ex.getMessage()));
        }
        return rpcResponse;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        Optional<ProviderMeta> optional = providerMetas.stream().filter(x-> x.getMethodSign().equals(methodSign)).findFirst();
        return optional.orElse(null);
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


}
