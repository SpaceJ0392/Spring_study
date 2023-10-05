package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") //이름을 지정해주지 않으면 orders가 되지 않으므로 error가 된다. query에 order by 명령어 존재...
@Getter
public class Order {
    @Id @GeneratedValue
    @Column(name = "orders_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id") // FK의 이름을 member_id로 지정.
    private Member member;

    @OneToMany(mappedBy = "order")
    private List<OrderTime> orderTimes = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문 상태 [ORDER, CANCEL]
}
