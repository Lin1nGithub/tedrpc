package cn.theodore.tedrpc.core.config;

import cn.theodore.tedrpc.core.api.RegistryCenter;
import cn.theodore.tedrpc.core.provider.ProviderBootstrap;
import cn.theodore.tedrpc.core.provider.ProviderInvoker;
import cn.theodore.tedrpc.core.registry.zk.ZkRegistryCenter;
import cn.theodore.tedrpc.core.transport.SpringBootTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

/**
 * @author linkuan
 */
@Slf4j
@Configuration
@Import({AppConfigProperties.class,ProviderConfigProperties.class, SpringBootTransport.class})
public class ProviderConfig {

    @Value("${server.port:8080}")
    private String port;

    @Autowired
    private AppConfigProperties appConfigProperties;

    @Autowired
    private ProviderConfigProperties providerConfigProperties;

    @Bean
    public ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap(port, appConfigProperties, providerConfigProperties);
    }

    @Bean
    public ProviderInvoker providerInvoker(@Autowired ProviderBootstrap providerBootstrap) {
        return new ProviderInvoker(providerBootstrap);
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerBoostStrap_runner(@Autowired ProviderBootstrap providerBootstrap) {
        return x -> {
            log.info("providerBootstrap starting ....");
            providerBootstrap.start();
            log.info("providerBootstrap started ....");
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public RegistryCenter provider_rc() {
        return new ZkRegistryCenter();
    }

}
