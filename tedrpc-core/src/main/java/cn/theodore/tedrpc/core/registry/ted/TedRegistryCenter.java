package cn.theodore.tedrpc.core.registry.ted;

import cn.theodore.tedrpc.core.api.RegistryCenter;
import cn.theodore.tedrpc.core.meta.InstanceMeta;
import cn.theodore.tedrpc.core.meta.ServiceMeta;
import cn.theodore.tedrpc.core.registry.ChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * implementation for ted registry center.
 * @author linkuan
 */
@Slf4j

public class TedRegistryCenter implements RegistryCenter {

    @Value("${kkregistry.servers}")
    private String servers;

    @Override
    public void start() {
        log.info("=====>>> [TedRegistry] : start with servers :{}", servers);
    }

    @Override
    public void stop() {
        log.info("=====>>> [TedRegistry] : stop with servers :{}", servers);
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info("=====>>> [TedRegistry] : registry instance :{} for {}", instance, service);
    }


    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {

    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        return List.of();
    }

    @Override
    public void subscribe(ServiceMeta service, ChangeListener changeListener) {

    }
}
