package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    //@PersistenceContext // Spring data JPA가 @Autowired로 사용하는 것을 지원하여, 일반 로직에서 DI 받듯이 쓸 수 있다.
    private final EntityManager em;

    //EntityManagerFactory를 직접 주입받고 싶으면, 아래와 같이 주입 받는다.
    /*@PersistenceUnit
    private EntityManagerFactory enf;*/

    public void save(Member member) {
        em.persist(member); //persist 하면 영속성 컨텍스트에 일단 객체를 넣고, 트랜잭선이 commit 되는 시점에 쿼리로 반영.
        //영속성 컨텍스트에 넣으므로, DB에 들어가기 전이라도, PK를 배정하고, Entity에도 해당 값을 넣어준 상태가 된다.
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
        //Jpql은 대상이 테이블이 아니라, 엔티티라는 것이 차이점일 뿐 SQL과 크게 다르지 않다.
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name).getResultList();
    }
}
