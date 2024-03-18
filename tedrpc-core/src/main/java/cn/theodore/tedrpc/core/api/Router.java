package cn.theodore.tedrpc.core.api;

import java.util.List;

/**
 * @author linkuan
 */
public interface Router<T> {

    List<T> route(List<T> providers);

    Router Default = providers -> providers;
}
