package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;

@Entity
@Getter
public class OrderTime {

    @Id @GeneratedValue
    @Column(name = "order_time_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문 당시 가격
    private int count; // 주문 당시 수량
}
