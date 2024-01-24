package spring_study.querydsl.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberTest {

    @Autowired
    private EntityManager em;
    @Test
    public void testEntity() throws Exception {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("mem1", 12, teamA);
        Member member2 = new Member("mem2", 12, teamA);
        Member member3 = new Member("mem3", 12, teamB);
        Member member4 = new Member("mem4", 12, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        //초기화
        em.flush();
        em.clear();

        //when
        List<Member> res = em.createQuery("select m from Member m join fetch m.team t", Member.class).getResultList();

        //then
        for (Member member : res) {
            System.out.println("member = " + member);
            System.out.println("member.team = " + member.getTeam());
        }
    }
    
    @Test
    public void changeTeamTest() throws Exception {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member = new Member("mem1", 12, teamA);
        em.persist(member);

        em.flush();
        em.clear();

        //when
        Member res = em.find(Member.class, 1);
        res.changeTeam(res.getTeam(), teamB);
        em.flush();

        //then
        assertThat(res.getTeam()).isEqualTo(teamB);
    }
}