package spring_and_jpa_api_study.springboot_and_jpa2.repository;

import lombok.Getter;
import lombok.Setter;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.OrderStatus;

@Getter
@Setter
public class OrderSearch {

    private String memberName; //회원 이름
    private OrderStatus orderStatus; //주문 상태 [CANCEL, ORDER]
}
