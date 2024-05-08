package cn.theodore.tedrpc.core.registry.ted;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * thread pool function
 * @author linkuan
 */
@Slf4j
public class TedHealthChecker {
    private ScheduledExecutorService consumerExecutor = null;
    private ScheduledExecutorService providerExecutor = null;

    public void start() {
        log.info(" =====>>> [TedRegistry] : start with health checker");
        consumerExecutor = Executors.newScheduledThreadPool(1);
        providerExecutor = Executors.newScheduledThreadPool(1);
    }

    public void providerCheck(Callback callback) {
        providerExecutor.scheduleWithFixedDelay(() -> {
            try {
                callback.call();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public void stop() {
        log.info(" =====>>> [TedRegistry] : stop with health checker");
        gracefulShutdown(consumerExecutor);
        gracefulShutdown(providerExecutor);
    }

    public void consumerCheck(Callback callback) {
        try {
            callback.call();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

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

    public interface Callback {
        void call() throws Exception;
    }
}
