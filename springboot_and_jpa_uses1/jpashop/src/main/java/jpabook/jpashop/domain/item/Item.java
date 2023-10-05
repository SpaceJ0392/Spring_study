package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //상속관계 테이블을 사용하면, 부모에게 지정해 주어야 한다. (우리의 전략은 싱글 테이블 전략)
@DiscriminatorColumn(name = "dtype")
@Getter
public abstract class Item { //구현체가 있으므로 추상 클래스로 만듬.

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();
}
