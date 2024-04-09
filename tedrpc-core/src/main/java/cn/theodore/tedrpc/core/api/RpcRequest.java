package cn.theodore.tedrpc.core.api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linkuan
 */
@Getter
@Setter
@ToString
public class RpcRequest {

    /**
     * 接口  cn.theodore.tedrpc.demo.api.UserService
     */
    private String service;

    /**
     * 方法 findById
     */
    private String methodSign;

    /**
     * 参数 100
     */
    private Object[] args;

    // 增加map传递上下文
    // 需要考虑使用ThreadLocal绑定线程

    // 跨调用方需要传递的参数
    private Map<String, String> params = new HashMap<>();
}
