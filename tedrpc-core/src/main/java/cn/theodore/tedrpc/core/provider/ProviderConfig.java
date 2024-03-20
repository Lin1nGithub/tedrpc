package cn.theodore.tedrpc.core.provider;

import cn.theodore.tedrpc.core.api.RegistryCenter;
import cn.theodore.tedrpc.core.registry.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author linkuan
 */
@Configuration
public class ProviderConfig {

    @Bean
    public ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }


    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerBootStrap_runner(@Autowired ProviderBootstrap providerBootstrap) {
        return x -> {
            System.out.println("providerBootstrap start....");
            providerBootstrap.start();
            System.out.println("providerBootstrap start....");
        };
    }

//    @Bean(initMethod = "start", destroyMethod = "stop")
    @Bean
    public RegistryCenter provider_rc() {
        return new ZkRegistryCenter();
    }

}
