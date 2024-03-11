package cn.theodore.tedrpc.core.api;

import lombok.*;

/**
 * @author linkuan
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> {

    /**
     * 200: 成功
     */
    private Integer code;

    T data;

    private String message;

    private Exception ex;
}
