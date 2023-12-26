package spring_and_jpa_api_study.springboot_and_jpa2.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Address {
    public String city;
    public String street;
    public int zipcode;

    protected Address() {
    } //JPA는 기본 생성자가 기본적으로 필요.

    public Address(String city, String street, int zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
