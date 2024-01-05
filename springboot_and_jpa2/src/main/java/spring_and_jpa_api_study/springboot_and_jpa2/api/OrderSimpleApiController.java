package spring_and_jpa_api_study.springboot_and_jpa2.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.Address;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.Order;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.OrderItem;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.OrderStatus;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.item.Item;
import spring_and_jpa_api_study.springboot_and_jpa2.repository.OrderRepository;
import spring_and_jpa_api_study.springboot_and_jpa2.repository.OrderSearch;
import spring_and_jpa_api_study.springboot_and_jpa2.repository.order.simplequery.OrderSimpleQueryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


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
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;
    @GetMapping("api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //강제로 Lazy 호출
            order.getDelivery().getAddress(); //강제로 Lazy 호출
        }
        return all; //무한 루프 - Order에서 member 호출 후 member에서 orders 호출로 반복...
        //양방향 연관 관계 중 하나는 @JsonIgnore한다.
    }

    @GetMapping("api/v2/simple-orders")
    public Result ordersV2(){
        List<SimpleOrderDto> simpleOrderDtos = orderRepository.findAllByString(new OrderSearch()).stream()
                .map(SimpleOrderDto::new).collect(Collectors.toList());

        return new Result<>(simpleOrderDtos);
    }

    @GetMapping("api/v3/simple-orders")
    public Result ordersV3() {
         List<SimpleOrderItemDto> simpleOrderItemDtos = orderRepository.findAllByFetch()
                 .stream().map(SimpleOrderItemDto::new)
                .collect(Collectors.toList());

        return new Result<>(simpleOrderItemDtos);
    }

    @GetMapping("api/v4/simple-orders")
    public Result ordersV4() {
        return new Result<>(orderSimpleQueryRepository.findByDtos());
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        private T data;
    }

    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String userName;
        private LocalDateTime orderDate;
        private OrderStatus status;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            userName = order.getMember().getName();
            orderDate = order.getOrderDate();
            status = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }

    @Data
    static class SimpleOrderItemDto {
        private Long orderId;
        private String userName;
        private List<String> item;
        private LocalDateTime orderDate;
        private OrderStatus status;
        private Address address;

        public SimpleOrderItemDto(Order order) {
            orderId = order.getId();
            userName = order.getMember().getName();
            item = order.getOrderItems().stream().map(OrderItem::getItem).toList()
                    .stream().map(Item::getName).collect(Collectors.toList());
            orderDate = order.getOrderDate();
            status = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
