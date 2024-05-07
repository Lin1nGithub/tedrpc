package cn.theodore.tedrpc.core.registry.ted;

import cn.theodore.tedrpc.core.api.RegistryCenter;
import cn.theodore.tedrpc.core.consumer.HttpInvoker;
import cn.theodore.tedrpc.core.meta.InstanceMeta;
import cn.theodore.tedrpc.core.meta.ServiceMeta;
import cn.theodore.tedrpc.core.registry.ChangeListener;
import cn.theodore.tedrpc.core.registry.Event;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        log.info(" =====>>> [TedRegistry] : start with servers :{}", servers);
        executor = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void stop() {
        log.info(" =====>>> [TedRegistry] : start graceful shutdown");
        executor.shutdown();
        try {
            // 线程池优雅启停
            executor.awaitTermination(1, TimeUnit.SECONDS);
            // isShutdown isTerminated
            // 如果没有停下来
            if (!executor.isTerminated()) {
                // 执行强制停止
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" =====>>> [TedRegistry] : register instance :{} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/reg?service=" + service.toPath(), InstanceMeta.class);
        log.info(" =====>>> [TedRegistry] : registered {}", instance);
    }


    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" =====>>> [TedRegistry] : unregister instance :{} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/unreg?service=" + service.toPath(), InstanceMeta.class);
        log.info(" =====>>> [TedRegistry] : unregistered {}", instance);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" =====>>> [TedRegistry] : find all instances for {} ", service);
        List<InstanceMeta> instances = HttpInvoker.httpGet(servers + "/findAll?service=" + service.toPath(), new TypeReference<List<InstanceMeta>>() {
        });
        log.info(" =====>>> [TedRegistry] : findAll = {}", instances);
        return instances;
    }

    /**
     * 本地服务的版本缓存
     */
    private Map<String, Long> VERSIONS = new HashMap<>();

    private ScheduledExecutorService executor = null;

    @Override
    public void subscribe(ServiceMeta service, ChangeListener listener) {
        executor.scheduleWithFixedDelay(() -> {
            // 获取当前的版本
            Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
            // 远程获取服务的版本
            Long newVersion = HttpInvoker.httpGet(servers + "/version?service=" + service.toPath(), Long.class);
            log.info(" ====>>>> [TedRegistry] : version = {},newVersion = {}", version, newVersion);
            if (newVersion > version) {
                // 重新获取新的服务版本
                List<InstanceMeta> instances = fetchAll(service);
                listener.fire(new Event(instances));
                // 刷新版本号
                VERSIONS.put(service.toPath(), newVersion);
            }
        }, 1000, 5000, TimeUnit.MILLISECONDS);
    }
}
