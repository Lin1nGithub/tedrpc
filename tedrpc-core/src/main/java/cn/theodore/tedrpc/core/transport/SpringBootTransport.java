package cn.theodore.tedrpc.core.transport;

import cn.theodore.tedrpc.core.api.RpcRequest;
import cn.theodore.tedrpc.core.api.RpcResponse;
import cn.theodore.tedrpc.core.provider.ProviderInvoker;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author linkuan
 */
@RestController
public class SpringBootTransport {

    @Resource
    private ProviderInvoker providerInvoker;

    @RequestMapping(value = "/tedprc", method = RequestMethod.POST)
    public RpcResponse<Object> invoke(@RequestBody RpcRequest request) {
        return providerInvoker.invoke(request);
    }
}
