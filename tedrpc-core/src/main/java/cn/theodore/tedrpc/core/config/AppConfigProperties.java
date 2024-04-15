package cn.theodore.tedrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author linkuan
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ted.app")
public class AppConfigProperties {

    // for app instance
    private String id = "app1";

    private String namespace = "public";

    private String env = "dev";
}
