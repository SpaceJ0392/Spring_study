package com.springstudy.hello.repository;

import com.springstudy.hello.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryMemberRepositoryTest {

    MemoryMemberRepository repository = new MemoryMemberRepository();
    //afterEach()의 clear를 위해 인터페이스가 아닌 객체로 받음. (다른 repository를 사용하지 않으므로, 굳이 인터페이스를 받지 않음)

    @AfterEach //테스트 마다 끝나고 실행되는 callback method 개념
    public void afterEach(){
        repository.clearStore();
    }

    @Test
    public void save(){
        Member member = new Member();
        member.setName("Spring");

        repository.save(member);

        Member result = repository.findById(member.getId()).get();
        //방법 1
        Assertions.assertEquals(result ,member); //Assertion을 통해 검사 가능 (굳이 text 안찍어봐도 됨.)
        //방법 2
        assertThat(member).isEqualTo(result); // assertJ를 이용 (Assertions는 static import)
    }

    @Test
    public void findByName(){
        Member member1 = new Member();
        member1.setName("Spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("Spring2");
        repository.save(member2);

        Member result = repository.findByName("Spring1").get();
        assertThat(result).isEqualTo(member1);
    }

    @Test
    public void findAll(){
        Member member1 = new Member();
        member1.setName("Spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("Spring2");
        repository.save(member2);

        List<Member> result = repository.findAll();
        assertThat(result.size()).isEqualTo(2);
    }
}