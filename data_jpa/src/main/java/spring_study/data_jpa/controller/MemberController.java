package spring_study.data_jpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import spring_study.data_jpa.entity.Member;
import spring_study.data_jpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("members/{id}")
    public String findMember(@PathVariable("id") Long id){
        return memberRepository.findById(id).get().getUserName();
    }

    @GetMapping("members2/{id}")
    public String findMember2(@PathVariable("id") Member member){ //도메인 클래스 컨버터로 Springboot 상에서는 자동 반환
        return member.getUserName();
    }

    @PostConstruct
    public void init(){
        memberRepository.save(new Member("userA"));
    }
}
