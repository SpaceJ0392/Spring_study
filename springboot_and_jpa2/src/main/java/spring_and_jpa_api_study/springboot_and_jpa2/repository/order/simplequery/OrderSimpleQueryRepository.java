package spring_and_jpa_api_study.springboot_and_jpa2.repository.order.simplequery;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    //API 스펙에 의존적이므로 분리한다. (데이터 조회가 최적화 되어야 되는 부분은 따로 만듬)
    public List<OrderQueryDto> findByDtos() {
        return em.createQuery( //dto 프로텍션 시, List 등으로 반환 X -> 기본 자료형으로만 데이터를 받을 수 있음...
                "select new spring_and_jpa_api_study.springboot_and_jpa2.repository.order.simplequery.OrderQueryDto(o.id, m.name, i.name, o.orderDate, o.status, d.address) " +
                        "from Order o " +
                        "join o.orderItems oi " +
                        "join o.member m " +
                        "join o.delivery d " +
                        "join oi.item i", OrderQueryDto.class).getResultList(); //dto 프로텍션 시, fetch join 불가 (객체 그래프 탐색이므로, 엔티티가 지정되지 않으면 X)
    }// 이 경우, 최적화는 되지만, 재사용성을 떨어짐... ( + DTO로 반환해서, 데이터 변경 불가)
}
