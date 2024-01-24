package spring_study.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import spring_study.querydsl.entity.Member;
import spring_study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static spring_study.querydsl.entity.QMember.member;
import static spring_study.querydsl.entity.QTeam.team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    private EntityManager em;

    JPAQueryFactory query;

    @BeforeEach //@Test 전에 실행 (각각)
    public void init() {
        query = new JPAQueryFactory(em);
        //em과 JPAQueryFactory 모두 다중 쓰레드에 대해서도, 각각의 쓰레드에 맞는 객체가 바인딩되게 처리되어 있어
        //위처럼 하나로 써도 동시성 이슈 X

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
    }

    @Test
    public void startJPQL() throws Exception {
        //mem1을 찾아라.
        Member resJPQL = em.createQuery("select m from Member m where m.membername = :memname", Member.class)
                .setParameter("memname", "mem1")
                .getSingleResult();

        assertThat(resJPQL.getMembername()).isEqualTo("mem1");
    }

    @Test
    public void startQueryDsl() throws Exception {
        Member res = query.select(member)
                .from(member)
                .where(member.membername.eq("mem1")) //내부적으로 jdbc 파라미터 바인딩 기술로 동작해 (SQLinjection에 안전)
                .fetchOne();
        //일반적으로 위에서 처럼 static import한 member를 쓰지만, 같은 테이블을 조인하는 경우, 추가로 new로 새로운 alias를 부여해서
        //조인할 수 있다.

        assertThat(res.getMembername()).isEqualTo("mem1");
    }

    @Test
    public void search() throws Exception {
        Member res = query.selectFrom(member).where(member.membername.eq("mem1").and(member.age.eq(10)))
                .fetchOne();

        assertThat(res.getMembername()).isEqualTo("mem1");
    }

    @Test
    public void searchAndParam() throws Exception {
        Member res = query.selectFrom(member)
                .where(
                        member.membername.eq("mem1"),
                        member.age.eq(10)
                ) //and 대신 ,도 사용가능 -- 이걸 이용하면 null은 중간에 있어도 무시해서 동적 쿼리시 유용
                .fetchOne();
    }

    @Test
    public void resultFetch() throws Exception {
        List<Member> fetch = query.selectFrom(member)
                .fetch();//목록 리스트 조회

        Member fetchOne = query.selectFrom(member).fetchOne(); //단건 조회

        Member fetchFirst = query.selectFrom(member).fetchFirst();//== limit(1).fetchOne();
    }

    /**
     * 회원 정렬 순서
     * 1. 나이 내림차순
     * 2. 이름 올림차순
     * 3. 2에서 이름이 없으면 마지막 (null last)
     */
    @Test
    public void sort() throws Exception {
        em.persist(new Member(null, 100));
        em.persist(new Member("mem5", 100));
        em.persist(new Member("mem6", 100));

        List<Member> result = query.selectFrom(member)
                .orderBy(member.age.desc(), member.membername.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        assertThat(member5.getMembername()).isEqualTo("mem5");
        assertThat(member6.getMembername()).isEqualTo("mem6");
        assertThat(memberNull.getMembername()).isNull();
    }

    @Test
    public void paging1() throws Exception {
        List<Member> res = query.selectFrom(member)
                .orderBy(member.membername.desc())
                .limit(2) //몇 개
                .offset(1) //시작 지점
                .fetch();

        assertThat(res.size()).isEqualTo(2);
    }

    @Test
    public void paging2() throws Exception {
        Pageable pageable = PageRequest.of(1,2);

        List<Member> res = query.selectFrom(member)
                .orderBy(member.membername.desc())
                .limit(pageable.getPageSize()) //몇 개
                .offset(pageable.getOffset()) //시작 지점
                .fetch();

        //count는 따로 가져와야 함
        Long tot = query.select(member.count())
                .from(member)
                .fetchOne();

        assertThat(res.size()).isEqualTo(2);
        assertThat(tot).isEqualTo(4);
    }

    @Test
    public void aggregation() throws Exception {
        List<Tuple> res = query.select(
                member.count(),
                member.age.sum(),
                member.age.avg(),
                member.age.max(),
                member.age.min()
        ).from(member).fetch();

        Tuple tuple = res.get(0);

        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    /**
     * 팀의 이름과 각 팀의 평균 연령을 구해라.
     */
    @Test
    public void group() throws Exception {
        List<Tuple> res = query.select(team.teamname, member.age.avg()).from(member)
                .join(member.team, team)
                .groupBy(team.teamname)
                .fetch();

        Tuple teamA = res.get(0);
        Tuple teamB = res.get(1);

        assertThat(teamA.get(team.teamname)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.teamname)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }
    
}
