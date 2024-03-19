package cn.theodore.tedrpc.core.registry;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author linkuan
 */
@Data
@AllArgsConstructor
public class Event {

    List<String> data;


}
