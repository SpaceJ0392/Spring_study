package spring_and_jpa_api_study.springboot_and_jpa2.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.Address;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.Order;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.OrderItem;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.OrderStatus;
import spring_and_jpa_api_study.springboot_and_jpa2.repository.OrderRepository;
import spring_and_jpa_api_study.springboot_and_jpa2.repository.OrderSearch;
import spring_and_jpa_api_study.springboot_and_jpa2.repository.order.query.OrderFlatDto;
import spring_and_jpa_api_study.springboot_and_jpa2.repository.order.query.OrderItemQueryDto;
import spring_and_jpa_api_study.springboot_and_jpa2.repository.order.query.OrderQueryDto;
import spring_and_jpa_api_study.springboot_and_jpa2.repository.order.query.OrderQueryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    public final OrderRepository orderRepository;
    public final OrderQueryRepository orderQueryRepository;
    @GetMapping("api/v1/orders")
    public List<Order> ordersV1(){
         List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기화

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); //Lazy 강제 초기화

        }

        return all;
    }

    @GetMapping("api/v2/orders")
    public List<OrderDto> ordersV2(){
        return orderRepository.findAllByString(new OrderSearch())
                .stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
    }

    @GetMapping("api/v3/orders")
    public List<OrderDto> ordersV3(){
        return orderRepository.findAllByFetch()
                .stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
    }

    @GetMapping("api/v3.1/orders")
    public List<OrderDto> ordersV3Page
            (@RequestParam(value = "offset", defaultValue = "0") int offset,
             @RequestParam(value = "limit", defaultValue = "100") int limit)
    {
        return orderRepository.findAllByToOneWithPaging(offset, limit)
                .stream().map(OrderDto::new).collect(Collectors.toList());
    }

    @GetMapping("api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        return orderQueryRepository.findAllByDtosOpti();
    }

    @GetMapping("api/v6/orders")
    public List<OrderQueryDto> ordersV6(){
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDtosFlat();

        return flats.stream()
                .collect(Collectors.groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()), //키
                        Collectors.mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), Collectors.toList()) //value
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Getter //@Data는 toString 등을 다 만들어서, 그냥 Getter를 쓰기도 한다... (안쓰면, type 에러 남. - properties를 못 가져 옴.)
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;
        //orderItem도 entity이므로 다 DTO로 변환한다. - 아니면, 그냥 출력시 null (proxy 이므로 - touch 필요)
        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(OrderItemDto::new).collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;
        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
