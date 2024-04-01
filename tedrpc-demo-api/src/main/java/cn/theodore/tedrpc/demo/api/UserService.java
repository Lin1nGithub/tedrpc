package cn.theodore.tedrpc.demo.api;

/**
 * @author linkuan
 */

public interface UserService {

    User findById(int id);
    User findById(int id, String name);

    String getName();

    String getName(int id);

    long findById(long id);

    User getId(User user);

    int[] getIds();

    int[] getIds(int[] ids);

    User ex(boolean flag);

    void setTimeoutPorts(String timeoutPorts);
}
