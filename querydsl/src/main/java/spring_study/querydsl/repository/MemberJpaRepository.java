package spring_study.querydsl.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import spring_study.querydsl.dto.MemberSearchCondition;
import spring_study.querydsl.dto.MemberTeamDto;
import spring_study.querydsl.dto.QMemberTeamDto;
import spring_study.querydsl.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;
import static spring_study.querydsl.entity.QMember.member;
import static spring_study.querydsl.entity.QTeam.team;

@Repository
public class MemberJpaRepository {

    private final EntityManager em;
    //동시성 이슈 X - spring에서 주입받는 em은 싱글톤이지만, proxy 객체라서, tranactional 영역마다, 각각의 객체로 proxy가 바인딩해준다.
    private final JPAQueryFactory query; //queryDSL용

    public MemberJpaRepository(EntityManager em, JPAQueryFactory query) {
        this.em = em;
        //this.query = new JPAQueryFactory(em); //--방법 1

        //스프링 빈으로 등록했으므로 그냥 autowired --방법 2 (Lombok 사용 가능)
        this.query = query;
    }


    public void save(Member member){
        em.persist(member);
    }

    public Optional<Member> findById(Long id){
        return Optional.ofNullable(em.find(Member.class, id));
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public List<Member> findAllQueryDsl(){
        return query.selectFrom(member).fetch();
    }

    public List<Member> findByMembername(String membername){
        return em.createQuery("select m from Member m where m.membername = :membername", Member.class)
                .setParameter("membername", membername)
                .getResultList();
    }

    public List<Member> findByMembernameQueryDsl(String membername){
        return query.selectFrom(member).where(member.membername.eq(membername)).fetch();
    }


    public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition){

        // 동적 쿼리
        BooleanBuilder builder = new BooleanBuilder();
        if (hasText(condition.getMembername())){
            builder.and(member.membername.eq(condition.getMembername()));
        }

        if (hasText(condition.getTeamname())) {
            builder.and(team.teamname.eq(condition.getTeamname()));
        }

        if (condition.getAgeGoe() != null) {
            builder.and(member.age.goe(condition.getAgeGoe()));
        }

        if (condition.getAgeLoe() != null) {
            builder.and(member.age.loe(condition.getAgeLoe()));
        }

        return query
                .select(new QMemberTeamDto(
                            member.id.as("memberId"),
                            member.membername,
                            member.age,
                            team.id.as("teamId"),
                            team.teamname
                        )).from(member)
                .leftJoin(member.team, team)
                .where(builder)
                .fetch();
    }

    public List<MemberTeamDto> search(MemberSearchCondition condition){
        return query
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.membername,
                        member.age,
                        team.id.as("teamId"),
                        team.teamname
                )).from(member)
                .leftJoin(member.team, team)
                .where(
                        membernameEq(condition.getMembername()),
                        teamnameEq(condition.getTeamname()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                        //ageBetween(condition.getAgeLoe(), condition.getAgeGoe())
                )
                .fetch();
    }

    private BooleanExpression membernameEq(String membername) {
        return hasText(membername) ? member.membername.eq(membername) : null;
    }

    private BooleanExpression teamnameEq(String teamname) {
        return hasText(teamname) ? team.teamname.eq(teamname) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }

    private BooleanExpression ageBetween(Integer ageLoe, Integer ageGoe) {
        //조립 및 재사용 가능
        //해당 코드는 교육용으로 Null 체크 등이 더 필요 (둘다 null일 때는 문제 X but 둘 중 하나만 null 이거나 하면 문제 가능성 up)
        return ageGoe(ageLoe).and(ageLoe(ageLoe));
    }
}
