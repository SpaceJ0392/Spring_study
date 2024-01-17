package spring_study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import spring_study.data_jpa.dto.MemberDto;
import spring_study.data_jpa.entity.Member;
import spring_study.data_jpa.entity.Team;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    //같은 트랜잭션 안에 있으면, em을 호출할 때, 같은 em을 호출한다.
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();
        //원래 Optional로 가져와서 Null에 대한 처리가 필요한데, 여기서는 그냥 과정 상 생략
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();//Optional을 그냥 가져옴 (get())
        Member findMember2 = memberRepository.findById(member2.getId()).get();//Optional을 그냥 가져옴 (get())

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //전체 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCnt = memberRepository.count();
        assertThat(deletedCnt).isEqualTo(0);
    }

    @Test
    public void findByUserNameAndAgeGreaterThan(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUserNameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUserName()).isEqualTo(m2.getUserName());
        assertThat(result.get(0).getAge()).isEqualTo(m2.getAge());
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findMember = memberRepository.findByUsernameNamedQuery("AAA");

        assertThat(findMember.get(0)).isEqualTo(m1);
    }

    @Test
    public void testQuery() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findMember = memberRepository.findMember(10,"AAA");

        assertThat(findMember.get(0)).isEqualTo(m1);
    }

    @Test
    public void testFindByUserNameList() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> findMember = memberRepository.findByUsernameList();

        for (String s : findMember) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() throws Exception {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member m1 = new Member("AAA", 10, teamA);
        Member m2 = new Member("AAA", 20, teamB);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<MemberDto> findMember = memberRepository.findByMemberDto();

        for (MemberDto dto : findMember) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findMember = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for (Member member : findMember) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void paging(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getUserName(), m.getAge(), null));
        //페이징한 결과를 DTO로 바꿀 수 있음.

        ///Slice<Member> slice = memberRepository.findByAge(age, pageRequest);
        //slice는 전체 데이터 수 cnt X - 지정된 크기에 +1을 더 가져와서, 다음 페이지가 있는지 없는지 확인
        //여기서는 페이지를 3으로 했으므로, 4개를 가져와서 마지막이 있으면 다음 페이지가 존재함을 안다.

        //then
        //slice
//        List<Member> content = slice.getContent();
//
//        assertThat(slice.getContent().size()).isEqualTo(3); //가져온 데이터의 수
//        assertThat(slice.getNumber()).isEqualTo(0); //현재 페이지 번호
//        assertThat(slice.isFirst()).isTrue();
//        assertThat(slice.hasNext()).isTrue();


        //page
        List<Member> content = page.getContent();
        long totCnt = page.getTotalElements();

        assertThat(page.getContent().size()).isEqualTo(3); //가져온 데이터의 수
        assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); //현재 페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 수
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

//        for (Member member : content) {
//            System.out.println("member = " + member);
//        }
//        System.out.println("totCnt = " + totCnt);

    }

    @Test
    public void bulkUpdate() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        //JPQL 시작 시, 우선 flush가 한번 발생하므로, 이전 데이터는 반영되고, 이후 쿼리가 작동.
        int result = memberRepository.bulkAgePlus(20);
        //em.clear(); //벌크 연산시, 영속성을 무시하고, 바로 데이터를 DB에 반영하므로, 영속성을 위해 clear()필요
        // --> data jpa는 @Modifying에서 설정
        //같은 트랜잭션 안에 이후 다른 작업이 없으면 clear()없어도 지장 X

        //then
    }


    @Test
    public void testEntityGraph() throws Exception {
        //given
        Team teamA = teamRepository.save(new Team("teamA"));
        Team teamB = teamRepository.save(new Team("teamB"));

        Member member1 = memberRepository.save(new Member("member1", 10, teamA));
        Member member2 = memberRepository.save(new Member("member2", 10, teamB));

        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findAll();

        //then
        for (Member member : result) {
            System.out.println("member = " + member);
            System.out.println("TeamName = " + member.getTeam().getName());
        }
    }

    @Test
    public void testQueryHint() throws Exception {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));

        em.flush();
        em.clear();

        //when
        Member result = memberRepository.findReadOnlyByUserName(member1.getUserName()) ;
        result.setUserName("member2");

        em.flush();
        //flush를 하려면 결국 원본과 변경본 데이터 2개를 가져야 한다. (최적화되어 있겠지만, 용량을 먹는게 사실 + 변경확인하는 것도 성능 먹음)
        //따라서, 단순 조회만을 하거나, 하려면, hint를 통해 readonly임을 알리면, 가져올 때, 복제본을 만들지 않는다.
        //readonly 후 flush를 하면 스냅샷을 만들지 않아서 변경 감지가 안됨.

        //이건, 진짜 중요한 데서, 성능을 위해서나 사용하는 거다. 게다가, 진짜 성능이 문제가 될 정도면 이미 캐시등을 깔아놓고 씀...

    }


}