package spring_and_jpa_api_study.springboot_and_jpa2.domain.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.Category;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorColumn(name = "dtype")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter @Setter
public class Item {
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

}
