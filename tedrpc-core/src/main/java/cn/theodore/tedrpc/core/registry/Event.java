package cn.theodore.tedrpc.core.registry;

import cn.theodore.tedrpc.core.meta.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author linkuan
 */
@Data
@AllArgsConstructor
public class Event {

    List<InstanceMeta> data;


}
