package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
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

    //==비즈니스 로직==//
    //특정 비즈니스 로직의 경우, 엔티티 주도 개발을 할 경우, 엔티티안에 존재하는 것이 보다 응집도가 있다.

    /*
        재고 증가.
     */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    /**
     *
     * stock 감소
     */
    public void removeStock(int quantity){
        if (this.stockQuantity - quantity < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity -= quantity;
    }
}
