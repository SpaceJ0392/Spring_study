package com.springstudy.hello.service;

import com.springstudy.hello.domain.Member;
import com.springstudy.hello.repository.MemoryMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberServiceTest {

    MemberService memberService;
    MemoryMemberRepository memberRepository;
/*
    MemoryMemberRepository memberRepository = new MemoryMemberRepository();
    이 방식은 repository를 service에서 사용하고 있는 repository 와 다른 repository를 만든 것이다.
    여기서는 데이터가 static 으로 선언된 hashMap에 들어가기에 상관 없지만, 만악 static 이 아니라면 바로 error

    그래서 DI 방식으로 service 단의 외부에서 repository를 지정해서 넣어주도록 변경
*/
    @BeforeEach // test method 가 돌아가기 전에 항상 실행
    void beforeEach(){
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository);
    }

    @AfterEach //테스트 마다 끝나고 실행되는 callback method 개념
    public void afterEach(){
        memberRepository.clearStore();
    }

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
    void 중복_회원_예외(){
        //given
        Member member1 = new Member();
        member1.setName("Spring");

        Member member2 = new Member();
        member2.setName("Spring");

        //when & then
        memberService.join(member1);
        //방법 1
/*
        try{
            memberService.join(member2);
            fail(); //오류가 발생하지 않으면 실패하게 함.
        }catch (IllegalArgumentException e){
            assertThat(e.getMessage()).isEqualTo("이미 존재하는 사람입니다");
        }
*/
        //방법 2
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> memberService.join(member2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 사람입니다");

    }

    @Test
    void findMembers() {
    }

    @Test
    void findOne() {
    }
}