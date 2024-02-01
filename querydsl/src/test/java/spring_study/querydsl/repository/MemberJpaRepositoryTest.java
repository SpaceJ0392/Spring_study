package spring_study.querydsl.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spring_study.querydsl.dto.MemberSearchCondition;
import spring_study.querydsl.dto.MemberTeamDto;
import spring_study.querydsl.entity.Member;
import spring_study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {
    
    @Autowired EntityManager em;
    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    public void basicTest() throws Exception {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member result1 = memberJpaRepository.findById(member.getId()).get();
        assertThat(result1).isEqualTo(member);

        List<Member> result2 = memberJpaRepository.findAll();
        assertThat(result2).containsExactly(member);

        List<Member> result3 = memberJpaRepository.findByMembername("member1");
        assertThat(result3).containsExactly(member);
    }

    @Test
    public void basicQueryDslTest() throws Exception {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        List<Member> result2 = memberJpaRepository.findAllQueryDsl();
        assertThat(result2).containsExactly(member);

        List<Member> result3 = memberJpaRepository.findByMembernameQueryDsl("member1");
        assertThat(result3).containsExactly(member);
    }
    
    @Test
    public void searchTest() throws Exception {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("mem1", 10, teamA);
        Member member2 = new Member("mem2", 20, teamA);
        Member member3 = new Member("mem3", 30, teamB);
        Member member4 = new Member("mem4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition cond = new MemberSearchCondition();
        cond.setAgeGoe(35);
        cond.setAgeLoe(45);
        cond.setTeamname("teamB");
        // 동적쿼리는 조건이 없으면, 모든 데이터를 가져올 수 있다. 그러므로 데이터 제한을 위한 기본 조건이나, paging을 통해 제약 등이 필요하다.

        List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(cond);
        assertThat(result).extracting("membername").containsExactly("mem4");
    }

    @Test
    public void searchWhereTest() throws Exception {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("mem1", 10, teamA);
        Member member2 = new Member("mem2", 20, teamA);
        Member member3 = new Member("mem3", 30, teamB);
        Member member4 = new Member("mem4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition cond = new MemberSearchCondition();
        cond.setAgeGoe(35);
        cond.setAgeLoe(45);
        cond.setTeamname("teamB");
        // 동적쿼리는 조건이 없으면, 모든 데이터를 가져올 수 있다. 그러므로 데이터 제한을 위한 기본 조건이나, paging을 통해 제약 등이 필요하다.

        List<MemberTeamDto> result = memberJpaRepository.search(cond);
        assertThat(result).extracting("membername").containsExactly("mem4");
    }
    
}