package com.springstudy.hello.controller;

public class MemberForm {
    private String name;
    //<form> 의 옵션 name에 따라 여기에 post 데이터 입력
    //입력되는 데이터는 set 방식을 통해 spring이 알아서 넣는다.
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
