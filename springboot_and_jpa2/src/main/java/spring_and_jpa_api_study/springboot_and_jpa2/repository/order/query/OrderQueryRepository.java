package spring_and_jpa_api_study.springboot_and_jpa2.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;


    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> orders = findOrders();

        orders.forEach(o -> {
            o.setOrderItems(findOrderItems(o.getOrderId()));
        });

        return orders;
    }

    public List<OrderQueryDto> findAllByDtosOpti() {
        List<OrderQueryDto> result = findOrders();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    public List<OrderFlatDto> findAllByDtosFlat() {
        return em.createQuery("select distinct new spring_and_jpa_api_study.springboot_and_jpa2.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d" +
                " join o.orderItems oi" +
                " join oi.item i", OrderFlatDto.class).getResultList();
        //얘도 쿼리 한번에 가져올 수 있다는 장점은 있으나, 페이징이 안됨 (데이터가 뻥튀기 되서... - 얘는 DTO 프로텍션이라, distinct 자동 적용 X)
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery("select new spring_and_jpa_api_study.springboot_and_jpa2.repository.order.query.OrderItemQueryDto(oi.order.id ,i.name, oi.orderPrice, oi.count) " +
                        "from OrderItem oi " +
                        "join oi.item i " +
                        "where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds).getResultList();

        //그냥 orderId 기준으로 데이터 정리
        return orderItems.stream().collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
    }

    public List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream().map(OrderQueryDto::getOrderId).toList();
    }

    public List<OrderItemQueryDto> findOrderItems(Long orderId) {

        return em.createQuery("select new spring_and_jpa_api_study.springboot_and_jpa2.repository.order.query.OrderItemQueryDto(oi.order.id ,i.name, oi.orderPrice, oi.count) " +
                "from OrderItem oi " +
                "join oi.item i " +
                "where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId).getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery("select new spring_and_jpa_api_study.springboot_and_jpa2.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
                "from Order o " +
                "join o.member m " +
                "join o.delivery d", OrderQueryDto.class).getResultList();
    }
}
