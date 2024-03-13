package cn.theodore.tedrpc.core.meta;

import lombok.*;

import java.lang.reflect.Method;

/**
 * @author linkuan
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProviderMeta {

    private Method method;

    private String methodSign;

    private Object ServiceImpl;
}
