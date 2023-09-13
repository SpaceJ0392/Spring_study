package com.springstudy.hello.domain;

import jakarta.persistence.*;

@Entity //이제부터 얘는 JPA가 관리하는 entity가 된다.
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //DB가 자동으로 ID 생성 - 그 전략을 IDENTITY 라 한다.
    private Long id; //여기서의 ID는 시스템이 알아서 지정한 ID
    //@Column(name = "username") //col의 이름이 username인 것과 mapping - 지금은 그냥 name이므로 필요 X
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
