package spring_study.data_jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseTimeEntity implements Persistable<String> {

    @Id
    private String id; //기본적으로 data Jpa의 save는 idrk null or 0이여야 persist()하고, 아니면 merge()한다.

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override //data jpa에서 isNew를 true로 여겨야 persist()된다.
    // - createdDate도 JPA를 통해 persist()하면서 생성되기에 이를 기준으로 새로운 것인지 확인 가능
    public boolean isNew() {
        return getCreatedDate() == null;
    }
    //merge를 방지하려면, persistable를 가져올 필요가 있음.
}
