package cn.theodore.tedrpc.core.api;

import cn.theodore.tedrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * rpc上下文
 * @author linkuan
 */
@Data
public class RpcContext {

    /**
     * 过滤器
     */
    List<Filter> filters;

    /**
     * 路由
     */
    private Router<InstanceMeta> router;

    /**
     * 均衡负载
     */
    private LoadBalancer<InstanceMeta> loadBalancer;

    /**
     * 上下文参数
     */
    private Map<String, String> parameters = new HashMap<>();

    public static ThreadLocal<Map<String,String>> ContextParameters = new ThreadLocal<>() {
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<>();
        }
    };

    public static void setContextParameter(String key, String value) {
        ContextParameters.get().put(key, value);
    }

    public static String getContextParameter(String key) {
        return ContextParameters.get().get(key);
    }

    public static void removeContextParameter(String key) {
        ContextParameters.get().remove(key);
    }
}
