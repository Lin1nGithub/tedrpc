package cn.theodore.tedrpc.core.api;

import lombok.Data;

/**
 * @author linkuan
 */
@Data
public class TedrpcException extends RuntimeException {

    private String errorCode;


    public TedrpcException() {
    }

    public TedrpcException(String message) {
        super(message);
    }

    public TedrpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public TedrpcException(Throwable cause) {
        super(cause);
    }

    public TedrpcException(Throwable cause, String errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public TedrpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


    // X => 技术类异常
    // Y => 业务类异常
    // Z => unknown, 搞不清楚,再归类到X或Y
    public static final String SOCKET_TIME_OUT_EX = "X001" + "-" + "http_invoke_timeout";
    public static final String NO_SUCH_METHOD_EX = "X002" + "-" + "method_not_exists";

    public static final String UNKNOWN_EX = "Z001" + "-" + "unknown";
}
