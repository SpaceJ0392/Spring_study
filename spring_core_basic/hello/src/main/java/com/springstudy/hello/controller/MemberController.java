package com.springstudy.hello.controller;

import com.springstudy.hello.domain.Member;
import com.springstudy.hello.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MemberController {

    private MemberService memberService;
    //필드 주입 (권장 되지 않음)
/*
    @Autowired
    private MemberService memberService;
*/

    //생성자 주입
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

/*    //setter 주입
    @Autowired
    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }
*/
    @GetMapping("/members/new")
    public String createForm(){
        return "/members/createMemberForm";
    }

    @PostMapping("/members/new") //데이터는 매개변수에서 받음
    public String create(MemberForm form){
        Member member = new Member();
        member.setName(form.getName());

        memberService.join(member);

        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members",members);
        return "/members/memberList";
    }
}
