package spring_and_jpa_api_study.springboot_and_jpa2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.Member;

import java.util.List;

public interface MemberDataJpaRepository extends JpaRepository<Member, Long> {

    // 메소드 이름의 시그니처로 인해 select m from Member m where m.name = ? 으로 그냥 코드를 짬.
    List<Member> findByName(String name);
}
