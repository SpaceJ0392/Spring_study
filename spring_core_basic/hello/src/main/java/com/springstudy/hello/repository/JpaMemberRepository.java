package com.springstudy.hello.repository;

import com.springstudy.hello.domain.Member;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class JpaMemberRepository implements MemberRepository {

    private EntityManager em;
    /*
     *   우리가 Jpa 라이브러리를 받으면, application.properties의 DB 정보 및 Jpa setting 정보 등을 혼합해서
     *   스프링 부트가 알아서 EntityManager라는 객체를 만들고 bean으로 등록한다.
     *
     *   Jpa는 EntityManager로 모든 것이 관리된다... (EntityManager가 내부적으로 쿼리 생성, Db 연결 등의 기능을 수행)
     *   JPA를 사용하려면 결국 EntityManager를 주입 받아야 한다.
     *
     */
    public JpaMemberRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Member save(Member member) {
        em.persist(member); //알아서 내부에서 insert 해줌 (테이블 찾고, 거기에 insert까지  + id 생성)
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id); //조회 끝
        return Optional.ofNullable(member); //값이 없을 수도 있으므로 Nullable로 반환.
    }

    @Override
    public Optional<Member> findByName(String name) {
        List<Member> result = em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();

        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
        //id로는 간단하게 select 가 가능하나, 전체 탐색 및 이름으로 탐색은 sql 사용
        //여기서 사용하는 sql은 jpql 이라는 특정한 sql 구문이다. (테이블이 아닌 엔티티 객체를 이용하여 쿼리 생성 -> SQL 구문으로 바뀜)
        //구문의 m은 Member 객체의 줄임말 표현 (as m)이라고 보면 된다.
    }
}
