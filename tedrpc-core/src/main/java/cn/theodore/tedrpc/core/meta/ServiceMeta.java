package cn.theodore.tedrpc.core.meta;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述服务元数据.
 * @author linkuan
 */
@Data
@Builder
public class ServiceMeta {

    /**
     * 应用名
     */
    private String app;

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * 环境名
     */
    private String env;

    /**
     * 服务名
     */
    private String name;

    private Map<String, String> parameters = new HashMap<>();

    public String toPath() {
        return String.format("%s_%s_%s_%s", app, namespace,env, name);
    }

    public String toMetas() {
        return JSON.toJSONString(this.getParameters());
    }
}
