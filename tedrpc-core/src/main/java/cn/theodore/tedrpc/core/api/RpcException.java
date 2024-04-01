package cn.theodore.tedrpc.core.api;

import lombok.Data;

/**
 * @author linkuan
 */
@Data
public class RpcException extends RuntimeException {

    private String errorCode;


    public RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(Throwable cause, String errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public RpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


    // X => 技术类异常
    // Y => 业务类异常
    // Z => unknown, 搞不清楚,再归类到X或Y
    public static final String SOCKET_TIME_OUT_EX = "X001" + "-" + "http_invoke_timeout";
    public static final String NO_SUCH_METHOD_EX = "X002" + "-" + "method_not_exists";

    public static final String UNKNOWN_EX = "Z001" + "-" + "unknown";
}
