package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") //이름을 지정해주지 않으면 orders가 되지 않으므로 error가 된다. query에 order by 명령어 존재...
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "orders_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // FK의 이름을 member_id로 지정.
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) //cascade는 해당 엔티티를 나 자신만 참조할 때 사용한다 (물론, 상대 대상이 다른 어떤 엔티티를 참조하는 것은 상관없음)
    private List<OrderItem> orderItems = new ArrayList<>();

    //cascade롤 통해서 각각의 orderTime에 대해서 각각 persist를 취하지 않고, 그냥 order에 대해서만 persist를 취해도 알아서 다 persist해준다.
    // + cascade ALL로 해서 삭제 시에도 다 같이 삭제된다.

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //기본적으로 모든 entity는 저장하고 싶으면 각각 persist해 주어야 한다. 그러나, cascade로 한 번에 처리한다.
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문 상태 [ORDER, CANCEL]

    //==연관 관계 편의 메서드==//
    //기본적으로 양방향 성을 가지는 엔티티에 대해서는 양 방향에서 서로의 값을 가져야 한다.
    //이를 원자적으로 묶어서 한번에 처리하도록 하는 메서드다.

    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==// 복잡한 생성관계를 기진 메서드는 생성 메서드를 만드는 것이 좋다.
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderitem : orderItems) {
            order.addOrderItem(orderitem);
        }

        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    //==비즈니스 로직==//

    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품입니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderitem : orderItems) {
            orderitem.cancel();
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderitem :orderItems) {
            totalPrice += orderitem.getTotalPrice();
        }
        return totalPrice;

        //return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum(); -- stream 사용 시...
    }
}
