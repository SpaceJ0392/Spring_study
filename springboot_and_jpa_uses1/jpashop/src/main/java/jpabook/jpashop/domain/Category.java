package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    //객체는 다 대 다가 가능하지만, 테이블인 불가능해서 일 대 다, 다 대 일 의 중간 테이블이 필요하다.
    //그래서 JoinTable 이 필요... (실무에서는 거의 안씀. 딱 이 그림 밖에 안됨 - 필드 추가 불가...)
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY) //레이지로 해놓지 않으면, 해당 목록이 100개일 때, join한다고 쿼리가 100번 날아감...
    //XtoOne에는 기본 fetch가 LAZY가 아니므로 설정 필요.
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    //==연관 관계 메서드==//
    public void addChildCategory(Category child){
        this.child.add(child);
        child.setParent(this);
    }
}
