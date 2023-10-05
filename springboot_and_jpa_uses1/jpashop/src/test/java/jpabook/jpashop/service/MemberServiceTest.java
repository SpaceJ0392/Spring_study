package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

//JPA가 돌아가는 것을 봐야해서 Spring을 가지고 쓴다. (순수 단위 테스트가 아님.)
@SpringBootTest
@Transactional //테스트에서는 있어야 롤백이 된다.
class MemberServiceTest {

    //테스트임으로 따로 참조 X.
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    //@Rollback(value = false) // 롤백 방지
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);
        //결과적으로 join 내부의 persist는 commit이 발생해야 insert 문이 발생하는데, @transactional로 인해 rollback이 발생하여
        //select 문만 작동... (Rollback 하기 싫으면, 어노테이션 사용)

        //then
        //혹은 그냥 entity manager 로 flush 하면 롤백이 되지만, 그 이전에 commit 됨.
        //em.flush();
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        //try {
        //    memberService.join(member2);
        //} catch (IllegalStateException e){
        //    return;
        //}
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        });


        //then
        //fail("예외가 발생해야 한다."); //여기까지 코드가 넘어오면 실패시킴.

    }


}