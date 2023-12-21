package hellojpa.domain.item;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) //부모 테이블에서 전략 설정 가능 (조인 전략, 싱글 테이블 전략, 구현 클래스 전략)
@DiscriminatorColumn //Dtype를 상속한 자식에 맞추어서 생성...
public class Item {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private int price;
}
