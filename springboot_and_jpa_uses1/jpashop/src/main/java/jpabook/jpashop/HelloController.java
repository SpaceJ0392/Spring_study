package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello") //hello라는 url이 오면 해당 controller 호출
    public String hello(Model model){
        //Model을 통해 Controller에서 데이터를 실어서 view에 넘길 수 있음
        model.addAttribute("data", "hello!!!");
        return "hello"; //return은 화면 이름이다. (관례적으로 .html이 붙음)
        /*
        * hello 라는 이름 만으로 templates의 hello.html을 찾아가는 것은 thymeleaf가 자동으로 저 주소로
        * 매핑하기 때문이다. (원한다면, 기본 주소를 바꿀 수도 있다.)
        */
    }
}
