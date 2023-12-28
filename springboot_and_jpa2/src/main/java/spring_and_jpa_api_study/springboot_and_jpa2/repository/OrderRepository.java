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
}
