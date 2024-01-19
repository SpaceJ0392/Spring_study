package spring_study.data_jpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import spring_study.data_jpa.dto.MemberDto;
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

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable){ //yml 파일이 아니라, 여기서 해당 부분만 제약
        //return memberRepository.findAll(pageable).map(member -> new MemberDto(member.getUserName(), member.getAge()));
        return memberRepository.findAll(pageable).map(MemberDto::new); //MemberDto::new == member -> new MemberDto(member)
    }

    //@PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
