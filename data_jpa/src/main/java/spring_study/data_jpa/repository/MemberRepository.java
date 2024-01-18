package spring_study.data_jpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring_study.data_jpa.dto.MemberDto;
import spring_study.data_jpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository //커스텀한 리포지토리를 상속하여 사용해도 됨 - 이때는, Spring data jpa가 자동으로 구현체를 가져다 쓴다.
public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository {

    //쿼리 메소드 - 간단한 메소드의 시그니처를 통해 쿼리 생성
    List<Member> findByUserNameAndAgeGreaterThan(String username, int age);

    List<Member> findHelloBy(); //find ... by 사이의 값은 그냥 식별자라 아무거나 넣어도 됨... (by 뒤에 없으면, 조건 없이 다 가져온다.)

    //NamedQuery - 사용할 수 있도록 만듬
    //@Query(name = "Member.findByUsername") //NamedQuery 이름을 관습에 맞게 작성하면 @Query 없어도 됨.
    //메소드를 실행할 때, (클래스명.메소드명)로 되어 있는 namedQuery가 있는지 먼저 찾고, 있으면 실행.
    //없으면, 쿼리 메소드로 실행.
    List<Member> findByUsernameNamedQuery(@Param("username") String username);

    //일종의 이름 없는 NamedQuery 처럼 작동 (정적 쿼리에 대해서, 애플리케이션 시작 시, 파싱하여 오류를 잡을 수 있음)
    @Query("select m from Member m where m.age = :age and m.userName = :username")
    List<Member> findMember(@Param("age") int age, @Param("username") String username);

    @Query("select m.userName from Member m")
    List<String> findByUsernameList();

    @Query("select new spring_study.data_jpa.dto.MemberDto(m.userName, m.age, t.name) from Member m join m.team t")
    List<MemberDto> findByMemberDto();

    @Query("select m from Member m where m.userName in :names") //in절로 컬랙션에 대해 호출가능.
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUserName(String username);
    //컬랙션 조회 - 데이터가 없어도 빈 리스트가 반환 (not null)
    Member findMemberByUserName(String username);
    //단건 조회 - 데이터가 없으면, JPA상으로는 error 그러나, data-jpa에서는 감싸서 null 반환
    //        - 데이터가 많으면, 그냥 error JPA상의 에러를 Spring 에러로 transform해서 다른 에러로 만듬 (다른 ORM 기술을 사용할 수도 있으므로)
    Optional<Member> findOptionalByUserName(String username);
    //단건 조회 - null인 경우도 허용, 문제 없음 (단, 여러건 조회는 error)

    //paging
    @Query(value = "select m from Member m left join m.team t",
            countQuery ="select count(m) from Member m")
    //page 시 tot count가 조인 없이 읽어도 되는 것을, 조인한 결과로 읽어서 문제가 될 때가 있음
    // (데이터가 많으면 cnt 시 성능 문제가 있을 수 있음) - 이렇때는 쿼리를 위처럼 분리가능.
    Page<Member> findByAge(int age, Pageable pageable);

    //Slice<Member> findByAge(int age, Pageable pageable);

    //Bulk update -- 단체 업데이트

    //벌크 연산의 문제는 영속성을 무시하고, 그냥 바로 DB에 데이터를 넣는다. 즉, 영속성을 위해서는 DB에서 다시 불러와야 함.
    @Modifying(clearAutomatically = true) //이게없으면 업데이트가 호출 안됨.
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    // Entity Graph (fetch join)
    @Override
    @EntityGraph(attributePaths = {"team"}) //fetch join을 해줌...
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"}) //fetch join을 해줌...
    @Query("select m from Member m") //이런식으로 쿼리에 fetch join 을 해도 됨.
    List<Member> findMemberEntityGraph();

    //@EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all") //JPA표준인 NamedEntityGraph를 지원.
    List<Member> findEntityGraphByUserName(String username);

    //Jpa Hint
    //데이터를 호출 시, 변경 감지가 필요없는 읽기용 데이터는 굳이 복제본을 만들 필요가 없으나, readonly로 하지 않으면, 변경 감지를 위해
    //데이터를 복제함. 이를 hint를 통해 방지할 수 있음. (다만, JPA 표준이 아니라 Hibernate가 제공) - hint자체늕 JPA 기능, readonly가 hibernate
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUserName(String name);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE) //쿼리 중 접근하지 못하게 하는 Lock를 JPA도 제공한다 정도만 알아라
    List<Member> findLockByUserName(String name); //깊이있는 내용이라, 이렇게 쓸 수 있다 정도로 넘어간다.

}
