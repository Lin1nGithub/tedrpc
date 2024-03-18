package cn.theodore.tedrpc.core.api;

import java.util.List;

/**
 * @author linkuan
 */
public interface Router {

    List<String> route(List<String> providers);

    Router Default = providers -> providers;
}
