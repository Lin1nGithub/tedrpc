package cn.theodore.tedrpc.core.registry;

import cn.theodore.tedrpc.core.api.ChangeListener;
import cn.theodore.tedrpc.core.api.RegistryCenter;
import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * 基于zk创建注册中心
 * 使用 curator连接zk客户端
 * @author linkuan
 */
public class ZkRegistryCenter implements RegistryCenter {

    private CuratorFramework client = null;

    @Override
    public void start() {
        // 重拾策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .namespace("tedrpc")
                .retryPolicy(retryPolicy)
                .build();
        System.out.println("====> zk client started.");
        client.start();
    }

    @Override
    public void stop() {
        System.out.println("====> zk client stopped.");
        client.close();
    }

    /**
     * 注册服务 注册某个服务的某个节点
     * @param service
     * @param instance
     */
    @Override
    public void register(String service, String instance) {
        String servicePath = "/" + service;
        try {
            // 创建服务的持久化节点
            if (client.checkExists().forPath(servicePath) == null) {
                // 持久化模式
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            // 创建实例的临时性节点
            String instancePath = servicePath + "/" + instance;
            System.out.println("====> register to zk:" + instancePath);
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 注销服务 注销服务的某个节点
     * @param service
     * @param instance
     */
    @Override
    public void unregister(String service, String instance) {
        String servicePath = "/" + service;
        try {
            // 判断服务节点是否存在
            // 如果是 那么返回
            if (client.checkExists().forPath(servicePath) == null) {
                return;
            }
            // 删除实例节点
            String instancePath = servicePath + "/" + instance;
            System.out.println("====> unregister to zk:" + instancePath);
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> fetchAll(String service) {
        String servicePath = "/" + service;
        try {
            List<String> nodes = client.getChildren().forPath(servicePath);
            System.out.println(" ===> fetchAll from zk: " + servicePath);
            nodes.forEach(System.out::println);
            return nodes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SneakyThrows
    public void subscribe(String service, ChangeListener listener) {
        final TreeCache cache = TreeCache.newBuilder(client, "/" + service)
                .setCacheData(true)
                .setMaxDepth(2)
                .build();
        cache.getListenable().addListener((curator, event) -> {
            // 有任何节点变动 这里会执行
            System.out.println("zk subscribe event: "+ event);
            List<String> nodes = fetchAll(service);
            listener.fire(new Event(nodes));
        });
        cache.start();
    }
}
