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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * implementation for ted registry center.
 *
 * @author linkuan
 */
@Slf4j

public class TedRegistryCenter implements RegistryCenter {

    @Value("${tedregistry.servers}")
    private String servers;
    private Map<String, Long> VERSIONS = new HashMap<>();

    private ScheduledExecutorService consumerExecutor = null;
    private ScheduledExecutorService producerExecutor = null;

    /**
     * 服务保活的map
     * 每次注册的时候防入
     */
    MultiValueMap<InstanceMeta, ServiceMeta> RENEWS = new LinkedMultiValueMap<>();

    @Override
    public void start() {
        log.info(" =====>>> [TedRegistry] : start with servers :{}", servers);
        consumerExecutor = Executors.newScheduledThreadPool(1);
        producerExecutor = Executors.newScheduledThreadPool(1);
        // 探活
        producerExecutor.scheduleWithFixedDelay(() -> {
            for (InstanceMeta instance : RENEWS.keySet()) {
                StringBuffer sb = new StringBuffer();
                for (ServiceMeta service : RENEWS.get(instance)) {
                    sb.append(service.toPath()).append(",");
                }
                String services = sb.toString();
                if (services.endsWith(",")) services = services.substring(0, services.length() - 1);
                log.info(" =====>>> [TedRegistry] : renew instance {} for {}", instance, services);
                Long timestamp = HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/renews?services=" + services, Long.class);
                log.info(" =====>>> [TedRegistry] : renew instance {} at {}", instance, timestamp);
            }

        }, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        log.info(" =====>>> [TedRegistry] : start graceful shutdown");
        gracefulShutdown(consumerExecutor);
        gracefulShutdown(producerExecutor);
    }

    /**
     * 优雅启停方法
     *
     * @param executorService
     */
    private void gracefulShutdown(ScheduledExecutorService executorService) {
        executorService.shutdown();
        try {
            // 线程池优雅启停
            executorService.awaitTermination(1, TimeUnit.SECONDS);
            // isShutdown isTerminated
            // 如果没有停下来
            if (!executorService.isTerminated()) {
                // 执行强制停止
                executorService.shutdownNow();
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
        RENEWS.add(instance, service);
    }


    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" =====>>> [TedRegistry] : unregister instance :{} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/unreg?service=" + service.toPath(), InstanceMeta.class);
        log.info(" =====>>> [TedRegistry] : unregistered {}", instance);
        RENEWS.remove(instance, service);
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
    @Override
    public void subscribe(ServiceMeta service, ChangeListener listener) {
        consumerExecutor.scheduleWithFixedDelay(() -> {
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
