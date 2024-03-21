package cn.theodore.tedrpc.core.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author linkuan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceMeta {


    private String scheme;

    /**
     * 地址
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 请求路径的上下文
     */
    private String context;


    /**
     * 服务状态 online or offline
     */
    private boolean status;

    /**
     * 参数信息 比如:某个机房
     */
    private Map<String, String> parameters;

    public String toPath() {
        return String.format("%s_%d", host, port);
    }

    public InstanceMeta(String scheme, String host, Integer port, String context) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    public static InstanceMeta http (String host, Integer port) {
        return new InstanceMeta("http", host, port,"");
    }

    @Override
    public String toString() {
        return String.format("%s://%s:%d/%s", scheme, host, port, context);
    }
}
