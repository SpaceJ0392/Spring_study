package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable //jpa에 의해 내장 될 수 있는 타입임을 알림.
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address() {
        //기본 생성자를 생성 했지만, public은 너무 접근성이 좋으므로 pass, JPA는 protected 부터 허용
    }

    public Address(String city, String street, String zipcode) { 
        //그냥 이 생성자만 사용하면, JPA가 기본 생성자를 호출해서 proxy 등의 처리를 못해서 안됨.
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
