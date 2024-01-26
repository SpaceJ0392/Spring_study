package spring_study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
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
import spring_study.querydsl.dto.MemberDto;
import spring_study.querydsl.dto.QMemberDto;
import spring_study.querydsl.dto.UserDto;
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
    
    //Projection -- select 절에 나열되는 가져올 목록을 SQL에서는 projection이라고 한다.
    @Test
    public void tupleProjection() throws Exception {
        List<Tuple> res = query.select(member.membername, member.age).from(member).fetch();
        //여러개의 타입을 projection 받을 때는 Tuple로 받아짐.

        for (Tuple tuple : res) {
            String membername = tuple.get(member.membername);
            Integer age = tuple.get(member.age);

            System.out.println("membername = " + membername);
            System.out.println("age = " + age);
        }
    }
    
    @Test
    public void findDtoByJPQL() throws Exception {
        List<MemberDto> result = em.createQuery("select new spring_study.querydsl.dto.MemberDto(m.membername, m.age) " +
                "from Member m", MemberDto.class).getResultList();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void findDtoBySetter() throws Exception { //property 접근 방법 -- setter 이용 (getter, setter 없으면 작동 X)
        List<MemberDto> res = query.select(Projections.bean(MemberDto.class,
                        member.membername, member.age))
                .from(member).fetch(); //bean이 자바 bean 즉, getter, setter로 작동

        for (MemberDto memberDto : res) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void findDtoByField() throws Exception { //getter, setter 없어도 바로 필드에 값을 넣음.
        List<MemberDto> res = query.select(Projections.fields(MemberDto.class,
                        member.membername, member.age))
                .from(member).fetch(); //자바 리플렉션등을 이용하여 private에서도 그냥 필드에 값을 넣는다.

        for (MemberDto memberDto : res) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void findUserDtoByField() throws Exception {
        List<UserDto> res = query.select(Projections.fields(UserDto.class,
                        member.membername.as("name"), member.age))
                //projection의 인자가 setter의 프로퍼티명이나, field명과 일치해야 작동 (아니면 as를 주면 된다.)
                .from(member).fetch();

        for (UserDto userDto : res) {
            System.out.println("userDto = " + userDto);
        }
    }

    @Test
    public void findUserDtoComplex() throws Exception {
        QMember subMember = new QMember("sub_member");

        List<UserDto> res = query.select(Projections.fields(UserDto.class,
                        member.membername.as("name"),
                        ExpressionUtils.as(JPAExpressions.select(subMember.age.max()).from(subMember), "age")))
                .from(member).fetch();

        for (UserDto userDto : res) {
            System.out.println("userDto = " + userDto);
        }
    }

    @Test
    public void findDtoByConstructor() throws Exception { //생성자 이용하여 값을 넣음
        List<MemberDto> res = query.select(Projections.constructor(MemberDto.class,
                        member.membername, member.age))
                //생성자는 타입을 보므로, 인자의 이름은 크게 중요하지 않음. (필드나, setter projection은 다름)
                .from(member).fetch();

        for (MemberDto memberDto : res) {
            System.out.println("memberDto = " + memberDto);
        }
    }
    
    @Test
    public void findDtoByQueryProjection() throws Exception {
        List<MemberDto> res = query.select(new QMemberDto(member.membername, member.age))
                .from(member).fetch();
        //얘와 projections.constructor의 차이는 projections.constructor는 컴파일 오류가 안남
        // (생성자에 다른 param을 추가로 넣어도 에러 X)


        //단, 얘는 컴파일 체킹이 되서 굉장히 좋은데, 큐파일을 생성해야 하고, DTO가 queryDSL에 대한 의존성을 가지게 된다는 문제가 있음.
        for (MemberDto dto : res) {
            System.out.println("dto = " + dto);
        }
    }
    
    @Test
    public void dynamicQueryBooleanBuilder() throws Exception {
        String memberNameParam = "mem1";
        Integer ageParam = 10;

        List<Member> result = searchMember1(memberNameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember1(String memberNameCond, Integer ageCond) {

        BooleanBuilder builder = new BooleanBuilder(); //파리미터로 초기 조건 setting 가능
        if (memberNameCond != null){
            builder.and(member.membername.eq(memberNameCond));
        }

        if (ageCond != null){
            builder.and(member.age.eq(ageCond));
        }

        return query.selectFrom(member)
                .where(builder)
                .fetch();
    }


    @Test
    public void dynamicQueryWhereParam() throws Exception {
        String memberNameParam = "mem1";
        Integer ageParam = null;

        List<Member> result = searchMember2(memberNameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember2(String memberNameCond, Integer ageCond) {
        return query.selectFrom(member)
                //.where(allEq(memberNameCond, ageCond))
                .where(membernameEq(memberNameCond), ageEq(ageCond)) //연속해서 나열시 and로 연결됨
                // where에 들어간 null 값은 조건 무시
                .fetch();
    }

    private BooleanExpression membernameEq(String memberNameCond) {
        //Predicate 대신 BooleanExpression 사용 가능 -- 인터페이스에서 알아서 받음.
        return memberNameCond == null ? null : member.membername.eq(memberNameCond);
    }

    private BooleanExpression ageEq(Integer ageCond) {
        if (ageCond == null) {
            return null;
        }
        return member.age.eq(ageCond);
    }

    private BooleanExpression allEq(String memberNameCond, Integer ageCond){
        //Predicate로 반환하면, .and() 사용 불가.
        return membernameEq(memberNameCond).and(ageEq(ageCond)); //이런 식으로 합칠 수도 있음
    }

    @Test
    public void bulkUpdate() throws Exception {

        //mem1, 10 -> 비회원
        //mem2, 20 -> 비회원
        //mem3, 30 -> 유지
        //mem4, 40 -> 유지

        long count = query.update(member)
                .set(member.membername, "비회원")
                .where(member.age.lt(28))
                .execute(); //return은 영향을 받은 목록 수

        //얘도 JPQL를 이용한 벌크 연산과 마찬가지로, 영속성을 무시하고 바로 쿼리 나가서, 영속성을 깨짐
        //DB에는 반영되고, 영속성 컨택스트는 안바뀜.

        //즉, 쿼리를 호출하여 DB의 내용을 가져오는데, 문제는 영속성 컨텍스트에 내용이 있으면,
        // 그냥 영속성 컨텍스트의 데이터 사용

        em.flush();
        em.clear(); //그래서 영속성 컨텍스트 날림

        List<Member> res = query.selectFrom(member).fetch();

        for (Member mem : res) {
            System.out.println("mem = " + mem);
        }
    }
    
    @Test
    public void bulkAdd() throws Exception {
        query.update(member)
                .set(member.age, member.age.add(1))
                .execute();
    }
    
    @Test
    public void bulkDelete() throws Exception {
        query.delete(member)
                .where(member.age.goe(18))
                .execute();
    }

    @Test
    public void sqlFunction() throws Exception {
        List<String> res = query.select(
                Expressions.stringTemplate("function('replace', {0}, {1}, {2})",
                        member.membername, "mem", "M")
        ).from(member).fetch();

        for (String s : res) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void sqlFunction2() throws Exception {
        List<String> res = query.select(member.membername)
                .from(member)
                .where(member.membername.eq(
                        //안시 표준에 등록되는 매우 자주 사용되는 함수는 queryDSL이 내장하고 있음.
                        //Expressions.stringTemplate("function('lower', {0})", member.membername)))
                        member.membername.lower()
                ))
                .fetch();

        for (String s : res) {
            System.out.println("s = " + s);
        }
    }
    
    
}
