package com.springstudy.hello.repository;

import com.springstudy.hello.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository {
    //springdatajpa가 자동으로 구현체를 만들어서 proxy 방식으로 bean으로 자동 등록한다.
    //기본적으로 JpaRepository에서는 공통적으로 사용되는 대부분의 DB 접근 방식을 제공한다 ex. CRUD, SortAndPaging...

    //JPQL select m from Member m where member.name = ?
    @Override
    Optional<Member> findByName(String name);
    //이름 짓는 규칙에 따라 우리가 원하는 비즈니스 로직에 따른 호출을 할 수 있다.(reflection 기술로 처리)

    @Override
    Optional<Member> findById(Long aLong);
}
