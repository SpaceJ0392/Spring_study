package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.embaddable.Address;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Delivery extends BaseEntity{
    @Id @GeneratedValue
    private Long id;

    @Embedded
    private Address address;
    private DelieveryStatus status;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public DelieveryStatus getStatus() {
        return status;
    }

    public void setStatus(DelieveryStatus status) {
        this.status = status;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
