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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private final String REG_PATH = "/reg";
    private final String UNREG_PATH = "/unreg";
    private final String FINDALL_PATH = "/findAll";
    private final String VERSION_PATH = "/version";
    private final String RENEWS_PATH = "/renews";

    TedHealthChecker healthChecker = new TedHealthChecker();

    /**
     * 服务保活的map
     * 每次注册的时候防入
     */
    MultiValueMap<InstanceMeta, ServiceMeta> RENEWS = new LinkedMultiValueMap<>();

    @Override
    public void start() {
        log.info(" =====>>> [TedRegistry] : start with servers :{}", servers);
        healthChecker.start();
        // 探活
        providerCheck();
    }

    private void providerCheck() {
        healthChecker.providerCheck(() -> {
            for (InstanceMeta instance : RENEWS.keySet()) {
                Long timestamp = HttpInvoker.httpPost(JSON.toJSONString(instance), renewsPath(RENEWS.get(instance)), Long.class);
                log.info(" =====>>> [TedRegistry] : renew instance {} at {}", instance, timestamp);
            }
        });
    }

    @Override
    public void stop() {
        log.info(" =====>>> [TedRegistry] : start graceful shutdown");
        healthChecker.stop();
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
        HttpInvoker.httpPost(JSON.toJSONString(instance), regPath(service), InstanceMeta.class);
        log.info(" =====>>> [TedRegistry] : registered {}", instance);
        RENEWS.add(instance, service);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" =====>>> [TedRegistry] : unregister instance :{} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), unregPath(service), InstanceMeta.class);
        log.info(" =====>>> [TedRegistry] : unregistered {}", instance);
        RENEWS.remove(instance, service);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" =====>>> [TedRegistry] : find all instances for {} ", service);
        List<InstanceMeta> instances = HttpInvoker.httpGet(findAllPath(service), new TypeReference<List<InstanceMeta>>() {
        });
        log.info(" =====>>> [TedRegistry] : findAll = {}", instances);
        return instances;
    }

    /**
     * 本地服务的版本缓存
     */
    @Override
    public void subscribe(ServiceMeta service, ChangeListener listener) {
        healthChecker.consumerCheck(() -> {
            // 获取当前的版本
            Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
            // 远程获取服务的版本
            Long newVersion = HttpInvoker.httpGet(versionPath(service), Long.class);
            log.info(" ====>>>> [TedRegistry] : version = {},newVersion = {}", version, newVersion);
            if (newVersion > version) {
                // 重新获取新的服务版本
                List<InstanceMeta> instances = fetchAll(service);
                listener.fire(new Event(instances));
                // 刷新版本号
                VERSIONS.put(service.toPath(), newVersion);
            }
        });
    }

    private String regPath(ServiceMeta service) {
        return path(REG_PATH, service);
    }

    private String unregPath(ServiceMeta service) {
        return path(UNREG_PATH, service);
    }

    private String findAllPath(ServiceMeta service) {
        return path(FINDALL_PATH, service);
    }

    private String versionPath(ServiceMeta service) {
        return path(VERSION_PATH, service);
    }

    private String path(String context, ServiceMeta service) {
        return servers + context + "?service=" + service.toPath();
    }

    private String renewsPath(List<ServiceMeta> serviceList) {
        return path(RENEWS_PATH, serviceList);
    }

    private String path(String context, List<ServiceMeta> serviceList) {
        StringBuilder sb = new StringBuilder();
        for (ServiceMeta service : serviceList) {
            sb.append(service.toPath()).append(",");
        }
        String services = sb.toString();
        if (services.endsWith(",")) services = services.substring(0, services.length() - 1);
        log.info(" =====>>> [TedRegistry] : renew instance for {}", services);
        return servers + context + "?services=" + services;
    }

}
