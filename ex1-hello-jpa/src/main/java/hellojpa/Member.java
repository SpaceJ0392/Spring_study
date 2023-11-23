package hellojpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
//@Table("USER") -- 엔티티와 테이블 이름이 다르면 이렇게 매핑할 수 있다.
public class Member {
    @Id
    private Long id;
    //@Column("user_name") -- 원하는 컬럼 이름에 값을 매핑하도록 할 수 있다.
    private String name;


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
}
