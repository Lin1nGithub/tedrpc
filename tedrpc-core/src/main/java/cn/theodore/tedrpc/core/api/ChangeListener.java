package cn.theodore.tedrpc.core.api;

import cn.theodore.tedrpc.core.registry.Event;

/**
 * @author linkuan
 */
public interface ChangeListener {

    void fire(Event event);
}
