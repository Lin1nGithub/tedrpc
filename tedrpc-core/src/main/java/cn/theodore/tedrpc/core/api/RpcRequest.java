package cn.theodore.tedrpc.core.api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
}
