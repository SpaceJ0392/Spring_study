package hellojpa.domain.cascade;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Parent {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    //cascade는 한쪽에 완전히 종속적이거나(단일 소유자), 라이프 사이클이 같을 때 사용한다.
    //orphanRemoval을 True로 하면 부모에서 자식의 연관관계를 지우면 자동으로 table에서도 remove된다. (List에서 remove하면, query로 지워짐.)
    private List<Child> childList = new ArrayList<>();

    public void addChild(Child child){
        childList.add(child);
        child.setParent(this);
    }

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

    public List<Child> getChildList() {
        return childList;
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList;
    }
}
