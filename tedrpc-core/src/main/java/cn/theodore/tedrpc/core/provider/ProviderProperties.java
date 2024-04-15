package cn.theodore.tedrpc.core.provider;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author linkuan
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ted.provider")
public class ProviderProperties {
}
