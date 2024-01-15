package spring_study.data_jpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spring_study.data_jpa.entity.Member;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // test에서의 transactional은 실행 후 데이터를 rollback 한다. - 데이터를 남겨서 보고 싶으면, 아래의 설정 필요
// @Rollback(value = false)
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
        assertThat(findMember).isEqualTo(member); //JPA로 인해 영속성 보장 (저장한 겻과 가져온 것이 같은 것으로 처리)
    }

}