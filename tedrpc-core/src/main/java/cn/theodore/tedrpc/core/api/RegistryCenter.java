package cn.theodore.tedrpc.core.api;

import java.util.List;

/**
 * 注册中心
 *
 * @author linkuan
 */
public interface RegistryCenter {

    void start();

    void stop();


    // provider侧 注册/注销节点

    /**
     * 注册功能 注册某个服务的某个节点
     *
     * @param service
     * @param instance
     */
    void register(String service, String instance);

    void unregister(String service, String instance);

    // consumer侧

    /**
     * 获取某个服务的所有节点
     *
     * @param service
     * @return
     */
    List<String> fetchAll(String service);

    /**
     * 监听到服务的变化 然后需要做的操作
     * provider挂掉的时候 需要通知到consumer注销节点
     */
    void subscribe(String service, ChangeListener changeListener);

    /**
     * 静态注册中心
     */
    class StaticRegistryCenter implements RegistryCenter {

        List<String> providers;

        public StaticRegistryCenter(List<String> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, String instance) {

        }

        @Override
        public void unregister(String service, String instance) {

        }

        @Override
        public List<String> fetchAll(String service) {
            return providers;
        }

        @Override
        public void subscribe(String service, ChangeListener changeListener) {

        }

    }
}
