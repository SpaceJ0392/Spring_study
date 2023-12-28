package spring_and_jpa_api_study.springboot_and_jpa2.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.Order;
import spring_and_jpa_api_study.springboot_and_jpa2.repository.OrderRepository;
import spring_and_jpa_api_study.springboot_and_jpa2.repository.OrderSearch;

import java.util.List;


/**
 * X to One  (to Many 는 컬렉션을 받으므로 복잡해서 다음에) : 성능 최적화
 *
 * Order -> Member (Many to One)
 * Order -> Delivery (One to One)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        return all; //무한 루프 - Order에서 member 호출 후 member에서 orders 호출로 반복...
        //양방향 연관 관계 중 하나는 @JsonIgnore한다.
    }

}
