package cn.theodore.tedrpc.core.annotation;

import cn.theodore.tedrpc.core.config.ConsumerConfig;
import cn.theodore.tedrpc.core.config.ProviderConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author linkuan
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({ProviderConfig.class, ConsumerConfig.class})
public @interface EnableTedrpc {

}
