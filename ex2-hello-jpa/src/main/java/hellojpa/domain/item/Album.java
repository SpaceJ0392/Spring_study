package hellojpa.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("A") //ITEM에서의 DTYPE의 내용울 해당이름으로 축약가능 (기본값은 entity name)
public class Album extends Item{
    private String artist;
}
