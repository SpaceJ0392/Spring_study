package com.springstudy.hello.service;

import com.springstudy.hello.domain.Member;
import com.springstudy.hello.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest //Spring boot와 관련된 기능이 필요한 test 시 사용
@Transactional
class MemberServiceIntegrationTest {

    @Autowired //TEST는 마지막에 진행되므로 이걸 받아서 따로 사용하거나 하지 않으므로 그냥 필드 inject 받아 편하게 쓴다.
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository; //구현체는 우리가 config한 구현체를 가져올 것.
    //이전까지의 코드는 직접 서비스나 리포지토리를 주입했지만 지금부터는 Spring의 도움을 받아 그냥 DI할 것.
    //따라서 기존의 @BeforeEach는 필요 없음.

    //@AfterEach는 리포지토리를 메모리에서 초기화하기 위해 사용했었다. 당연히 DB 사용으로 필요 없음 + Transactional로 대체
    //이렇게 DB에 있는 내용을 테스트 케이스 마다 초기화하므로 당연히 운영 DB가 아닌 Test용 DB를 따로 구축한다. (혹은 로컬 PC의 DB 사용)
    @Test
    void 회원가입() {
        //given
        Member member = new Member();
        member.setName("Hello");

        //when
        Long saveId = memberService.join(member);

        //then
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    @Test
    void 중복_회원_예외() {
        //given
        Member member1 = new Member();
        member1.setName("Spring");

        Member member2 = new Member();
        member2.setName("Spring");

        //when & then
        memberService.join(member1);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> memberService.join(member2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 사람입니다");

    }
}