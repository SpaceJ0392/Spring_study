package jpabook.jpashop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class) //스프링 관련 테스트임을 알림
@SpringBootTest
class TestTestMemberRepositoryTest {

    @Autowired
    TestMemberRepository testMemberRepository;

    @Test
    @Transactional // 스프링 거가 제공하는 옵션이 많아서 권장 됨.
    //스프링의 @Transactional은 테스트 케이스에 있는 경우, 테스트가 끝나면, DB를 롤백함.
    @Rollback(value = false) //이 어노테이션을 사용하면 rollback 없이 commit 한다.
    public void testMember() throws Exception {
        //given
        TestMember testMember = new TestMember();
        testMember.setUserName("memberA");

        // 엔티티 매니저를 이용한 데이터 변경은 항상 트랜잭션 내에서 이루어져야 한다.
        //when
        Long saveId = testMemberRepository.save(testMember);
        TestMember findTestMember = testMemberRepository.find(saveId);

        //then
        assertThat(findTestMember.getId()).isEqualTo(testMember.getId());
        assertThat(findTestMember.getUserName()).isEqualTo(testMember.getUserName());
        assertThat(findTestMember).isEqualTo(testMember);
        System.out.println("findMember == member : " + (findTestMember == testMember));
        //트랜잭션 안에서 같은 id를 갖는 컨택스트를 저장하고 조회하면, 영속성이 같으므로 같은 엔티티로 인식하여 True가 나온다.
        //1차 캐시에서 이미 관리되고 있는 같은 영속성 컨텍스트가 존재하기에 기존에 관리하던게 그냥 나온다.

    }
    

}