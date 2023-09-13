package com.springstudy.hello.controller;

import com.springstudy.hello.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final MemberService memberService;

    public HomeController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/") //default 도메인
    public String home() {
        return "home";
    }
}
