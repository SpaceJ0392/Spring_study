package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id") //그냥 DB에서는 컬럼명이 id가 아니라 member_id가 되게 하기 위함.
    private Long id;

    private String name;

    @Embedded //내장 타입 사용을 알림 (사실은 @Embedable 과 둘 중 하나만 있어도 되나 보통 둘 다 씀)
    private Address address;

    @OneToMany(mappedBy = "member") // mappedBy는 orders의 member에 의해 매핑된 거라고 알림 (즉, 연관관계의 주인이 아님.)
    //즉, 읽기 전용이 됨. (여기서 값을 변경시켜도 FK의 값은 변화하지 않음.)
    private List<Order> orders = new ArrayList<>();
}
