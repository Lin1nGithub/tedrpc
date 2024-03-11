package cn.theodore.tedrpc.demo.api;

import lombok.*;

/**
 * @author linkuan
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {

    private Integer id;

    private Long amount;
}
