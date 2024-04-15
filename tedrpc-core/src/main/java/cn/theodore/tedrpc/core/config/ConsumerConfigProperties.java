package cn.theodore.tedrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author linkuan
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ted.consumer")
public class ConsumerConfigProperties {

    // for  ha and governance
    private int retries = 1;

    private int timeout = 1000;

    private int faultLimit = 10;

    private int halfOpenInitialDelay = 10_100;

    private int halfOpenDelay = 60_000;

    private int grayRatio = 0;
}
