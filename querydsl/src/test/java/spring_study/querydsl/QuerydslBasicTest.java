package spring_study.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import spring_study.querydsl.entity.Member;
import spring_study.querydsl.entity.QMember;
import spring_study.querydsl.entity.Team;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.select;
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

    /**
     * 팀 A에 소속된 모든 회원
     */
    @Test
    public void join() throws Exception {
        //given
        List<Member> res = query.selectFrom(member)
                .join(member.team, team)
                .where(member.team.teamname.eq("teamA"))
                .fetch();

        assertThat(res).extracting("membername")
                .containsExactly("mem1", "mem2");
    }

    /**
     * 세타 조인
     * 회원의 이름이 팀 이름과 같은 회원 조회 (연관관계가 없어도 세타 조인으로 하면 조인 가능...)
     */
    @Test
    public void thetaJoinTest() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Member> res = query.select(member)
                .from(member, team)
                .where(member.membername.eq(team.teamname))
                .fetch();

        //세타 조인에서는 모든 회원을 가져오고, 모든 팀을 가져온 다음, 다 그냥 조인함 (이후, where 절에서 필터링) -- 물론 DB가 최적화 함.
        //일종의 막 조인

        assertThat(res).extracting("membername")
                .containsExactly("teamA", "teamB");
    }

    /**
     * 회원과 팀을 조인하는데, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * JPQL : select m, t from Member m left join m.team t on t.name = "teamA"
     */
    @Test
    public void joinOnFiltering() throws Exception {
        List<Tuple> res = query.select(member, team).from(member)
                .leftJoin(member.team, team).on(team.teamname.eq("teamA"))
                .fetch();
        // 사실 inner join이면 굳이 on을 쓰지 않고, where로 필터링해도 같은 결과가 나온다.
        // 그러나, outer join이면, null값 등을 채워야 하기에 where가 아니라 on 절을 사용한다.

        for (Tuple t : res) {
            System.out.println("t = " + t);
        }
    }

    /**
     * 연관관계가 없는 엔티티 외부 조인
     * 회원의 이름이 팀 이름과 같은 회원을 외부 조인
     */
    @Test
    public void joinOnNoRelation() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Tuple> res = query.select(member, team)
                .from(member)
                .leftJoin(team).on(team.teamname.eq(member.membername))
                .fetch();

        //조인 시 member.team으로 조인하지 않아, id값으로 on절 생성 X - 그냥 on절을 작성한 내용을 바탕으로만 조인이 된다.

        for (Tuple t : res) {
            System.out.println("t = " + t);
        }
    }


    @PersistenceUnit
    EntityManagerFactory emf;
    @Test
    public void noFetchJoin() throws Exception {
        em.flush();
        em.clear();

        Member res = query.selectFrom(member).where(member.membername.eq("mem1")).fetchOne();
        //team이 Lazy라 당연히 team은 조회되지 않을 것.

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(res.getTeam()); //이미 로딩된 데이터인지 확인
        assertThat(loaded).as("fetch join 미적용").isFalse();
    }


    @Test
    public void fetchJoin() throws Exception {
        em.flush();
        em.clear();

        Member res = query.selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.membername.eq("mem1")).fetchOne();
        //team이 Lazy라 당연히 team은 조회되지 않을 것.

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(res.getTeam()); //이미 로딩된 데이터인지 확인
        assertThat(loaded).as("fetch join 적용").isTrue();
    }

    /**
     * 나이가 가장 많은 회원 조회
     *
     */
    @Test
    public void subQuery() throws Exception {
        //서브 쿼리는 외부 쿼리와 alias가 겹치면 안됨.
        QMember subMember = new QMember("sub_member");
        //서브 쿼리는 JPAExpression 필요 -- static import 함.
        Member res = query.selectFrom(member)
                .where(member.age.eq(
                        select(subMember.age.max())
                                .from(subMember)
                )).fetchOne();

        assertThat(res.getAge()).isEqualTo(40);
    }

    /**
     * 나이가 평균 이상인 회원 조회
     *
     */
    @Test
    public void subQueryGoe() throws Exception {
        //서브 쿼리는 외부 쿼리와 alias가 겹치면 안됨.
        QMember subMember = new QMember("sub_member");

        List<Member> res = query.selectFrom(member)
                .where(member.age.goe(
                        select(subMember.age.avg())
                                .from(subMember)
                )).fetch();

        assertThat(res).extracting("age")
                .containsExactly(30, 40);
    }

    /**
     * 나이가 10살 이상인 회원 조회 (IN절 위한 억지 예제)
     */
    @Test
    public void subQueryIn() throws Exception {
        //서브 쿼리는 외부 쿼리와 alias가 겹치면 안됨.
        QMember subMember = new QMember("sub_member");

        List<Member> res = query.selectFrom(member)
                .where(member.age.in(
                        select(subMember.age)
                                .from(subMember)
                                .where(subMember.age.gt(10))
                )).fetch();

        assertThat(res).extracting("age")
                .containsExactly(20, 30, 40);
    }

    @Test
    public void selectSubQuery() throws Exception {
        //select절에서도 서브쿼리 사용 가능
        QMember subMember = new QMember("sub_member");

        List<Tuple> res = query.select(member.membername, select(subMember.age.avg()).from(subMember)).from(member).fetch();

        for (Tuple tuple : res) {
            System.out.println("tuple = " + tuple);
        }

        /*
        select 절 서브 쿼리는 hibernate 가 구현해 놓음 -- 구현체로 hibernate를 쓰면, 사용가능
        (JPQL 자체는 select 서브 쿼리 지원 X)

        JPQL은 from절 서브 쿼리 지원 X -- queryDSL도 결국 지원 X (hibernate6부터는 from절 서브쿼리 지원 - querydsl은 ???)
        where절은 서브 쿼리 0
         */
    }

    @Test
    public void basicCase() throws Exception {
        List<String> res = query.select(member.age
                .when(10).then("열살")
                .when(20).then("스무살")
                .otherwise("기타")
        ).from(member).fetch();

        for (String s : res) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void complexCase() throws Exception {
        List<String> res = query.select(new CaseBuilder()
                .when(member.age.between(10,20)).then("0 ~ 20")
                .when(member.age.between(21,30)).then("21 ~ 30")
                .otherwise("기타")
        ).from(member).fetch();

        for (String s : res) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void constant() throws Exception {
        List<Tuple> res = query.select(member.membername, Expressions.constant("A")).from(member).fetch();
        //쿼리는 상수에 대한 것이 나가지 않는다. (그냥 일반 쿼리가 나감) 이후, 상수를 붙이는 형식

        for (Tuple t : res) {
            System.out.println("t = " + t);
        }
    }

    @Test
    public void concat() throws Exception {

        //membername_age를 원함.
        List<String> res = query.select(member.membername.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.membername.eq("mem1"))
                .fetch();

        //enum 타입을 가져오는 경우, stringvalue()를 이용해서 가져오면 된다.

        for (String s : res) {
            System.out.println("s = " + s);
        }
    }
    
    
}
