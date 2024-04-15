package cn.theodore.tedrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linkuan
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ted.providers")
public class ProviderConfigProperties {

    // for provider
    private Map<String, String> metas = new HashMap<>();
}
