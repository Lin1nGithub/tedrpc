package cn.theodore.tedrpc.core.cluster;

import cn.theodore.tedrpc.core.api.Router;
import cn.theodore.tedrpc.core.meta.InstanceMeta;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 灰度路由
 * @author linkuan
 */
@Slf4j
@Setter
public class GrayRouter implements Router<InstanceMeta> {

    private int grayRatio;

    private final Random random = new Random();

    public GrayRouter(int grayRatio) {
        this.grayRatio = grayRatio;
    }

    @Override
    public List<InstanceMeta> route(List<InstanceMeta> providers) {
        if (providers == null || providers.size() <= 1) {
            return providers;
        }

        // 对provider进行分类
        List<InstanceMeta> normalNodes = new ArrayList<>();
        List<InstanceMeta> grayNodes = new ArrayList<>();

        providers.forEach(p -> {
            if ("true".equals(p.getParameters().get("gray"))) {
                grayNodes.add(p);
            }else {
                normalNodes.add(p);
            }
        });

        if (normalNodes.isEmpty() || grayNodes.isEmpty()) {
            return providers;
        }
        if (grayRatio <= 0 ) {
            return normalNodes;
        }else if (grayRatio >= 100) {
            return grayNodes;
        }
        // List<100> 假设要求LB一定是线性的均匀分布
        //
        if (random.nextInt(100) < grayRatio) {
            log.debug(" grayRouter grayNodes ===> {}", grayNodes);
            return grayNodes;
        }else {
            log.debug(" grayRouter normalNodes ===> {}", normalNodes);
            return normalNodes;
        }

        // grayRatio = 10
        // graynodes    1 03
        // noramlnodes  2 01,02
        // all = 1*2=2, 2*9=18
    }
}
