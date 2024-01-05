package spring_and_jpa_api_study.springboot_and_jpa2.repository.order.simplequery;

import lombok.Data;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.Address;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.OrderStatus;

import java.time.LocalDateTime;

@Data
public class OrderQueryDto {
    private Long orderId;
    private String userName;
    private String itemName;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Address address;

    public OrderQueryDto(Long id, String name, String itemNames,
                         LocalDateTime orderDate, OrderStatus status, Address address) {
        //JPQL에서 직접 DTO를 매핑하면, 엔티티 객체 형식으로 파리미터 주입 X -> 직접 다 작성해야 함.
        this.orderId = id;
        this.userName = name;
        this.itemName = itemNames;
        this.orderDate = orderDate;
        this.status = status;
        this.address = address;
    }

}
