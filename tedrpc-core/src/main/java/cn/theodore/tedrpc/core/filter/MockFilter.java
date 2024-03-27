package cn.theodore.tedrpc.core.filter;

import cn.theodore.tedrpc.core.api.Filter;
import cn.theodore.tedrpc.core.api.RpcRequest;
import cn.theodore.tedrpc.core.api.RpcResponse;
import cn.theodore.tedrpc.core.util.MethodUtils;
import cn.theodore.tedrpc.core.util.MockUtils;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 一个链路 A B C 依次调用 当链路有问题的时候
 * A ==> B ==> C
 * ===> ===>  D 先在C上加个挡板 发现 链路还是有问题
 * ===> D 那么在B上加个挡板 发现链路还是有问题 那么只可能是A出问题了
 * Mock
 *
 * @author linkuan
 */
public class MockFilter implements Filter {
    @SneakyThrows
    @Override
    public Object preFilter(RpcRequest request) {
        Class service = Class.forName(request.getService());
        // 在service里面找方法
        Method method = findMethod(service, request.getMethodSign());
        // 找方法的类型
        Class<?> clazz = method.getReturnType();
        return MockUtils.mock(clazz);
    }

    private Method findMethod(Class service, String methodSign) {
        return Arrays.stream(service.getMethods())
                .filter(method -> !MethodUtils.checkLocalMethod(method))
                .filter(method -> methodSign.equals(MethodUtils.methodSign(method)))
                .findFirst().orElse(null);
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
        return null;
    }
}
