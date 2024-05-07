package cn.theodore.tedrpc.core.meta;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 描述服务元数据.
 * @author linkuan
 */
@Data
@Builder
@EqualsAndHashCode(of = {"app", "namespace", "env", "name"})
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

    public String toPath() {
        return String.format("%s_%s_%s_%s", app, namespace,env, name);
    }

}
