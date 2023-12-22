package jpabook.jpashop.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Delivery extends BaseEntity{
    @Id @GeneratedValue
    private Long id;

    private String city;
    private String street;
    private String zipcode;

    private DelieveryStatus status;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;
}
