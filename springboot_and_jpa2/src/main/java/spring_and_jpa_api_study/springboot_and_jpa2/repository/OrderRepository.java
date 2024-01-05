package spring_and_jpa_api_study.springboot_and_jpa2.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.Order;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondtion = true;

        //동적 쿼리 - 이게 힘들어서 Mybatis 같은 것을 쓰는 거다.
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null){
            if(isFirstCondtion){
                jpql += " where";
                isFirstCondtion = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = : status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())){
            if(isFirstCondtion){
                jpql += " where";
                isFirstCondtion = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                //.setFirstResult(100) //페이징시, 시작 위치 지정 가능
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null){
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())){
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList(); //결과 1000개 까지만

    }

    public List<Order> findAllByFetch() {
        return em.createQuery(
                //원래는 orderitems 갯수에 맞추어 order 가 늘어나서 나와야 하나 hibernate 6.0 부터는 distinct가 기본 적용
                //여기서의 distinct는 DB의 데이터의 전체가 같을 때의 중복 제거 + 가져와서 JPA 상의 ID가 같은 데이터에 대한 중복 제거
                "select o from Order o " +
                "join fetch o.member m " +
                "join fetch o.delivery d " +
                "join fetch o.orderItems oi " +
                "join fetch oi.item i", Order.class)
                .setFirstResult(1) //distinct가 자동 적용되며, paging도 가능하나, 메모리에 가져와서 해야함... (즉, DB 레벨에서의 paging이 안됨.)
                .setMaxResults(100) //사실상 페이징 안됨.... - 일 대 다 관계(컬랙션 페치 조인)가 있을 때, 안되는 거다... (데이터가 뻥튀기 되므로)
                .getResultList();
    }
}
