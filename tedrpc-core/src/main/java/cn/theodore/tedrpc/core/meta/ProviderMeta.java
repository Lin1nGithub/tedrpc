package cn.theodore.tedrpc.core.meta;

import lombok.*;

import java.lang.reflect.Method;

/**
 * 描述provider映射关系
 * @author linkuan
 */
@Data
@Builder
public class ProviderMeta {

    private Method method;

    private String methodSign;

    private Object ServiceImpl;
}
