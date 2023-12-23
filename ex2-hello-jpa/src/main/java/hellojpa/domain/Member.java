package hellojpa.domain;

import hellojpa.domain.embadded.Address;
import hellojpa.domain.embadded.Period;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Member extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String name;

    //기간 Period
    @Embedded
    private Period workPeriod;

    //주소
    @Embedded
    private Address homeAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(name = "WORK_CITY")),
            @AttributeOverride(name = "street", column = @Column(name = "WORK_STREET")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "WORK_ZIPCODE"))
    })
    private Address workAddress; //동일한 타입으로 테이블에 다른 것을 만들려면 임베디드 타입일 시, 위와 같은 설정이 필요

    @ElementCollection //일대 다 형식이지만, 얘는 테이블이 아니라, 갑 타입 컬랙션 (갑 타입 컬랙션은 모든 데이터가 PK로 묶임.)
    @CollectionTable(name = "FAVORITE_FOOD",
            joinColumns = @JoinColumn(name = "MEMBER_ID")
    )
    @Column(name = "FOOD_NAME")
    private Set<String> favoriteFoods = new HashSet<>(); //복잡하고 별로 바람직하지는 않음... (대안은 35:49초의 갑 타입 컬렉션 참고)

//    @OneToOne
//    @JoinColumn(name = "LOCKER_ID")
//    private Locker locker;

    @ManyToMany //안 씀
    @JoinTable(name = "MEMBER_PRODUCT")
    private List<Product> products = new ArrayList<>();

//    @Column(name = "TEAM_ID")
//    private Long teamId;

    @ManyToOne(fetch = FetchType.LAZY) //팀이 1, 멤버가 다
    @JoinColumn(name = "TEAM_ID") //연결될 FK는 team_id
    private Team team;

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

    public Period getWorkPeriod() {
        return workPeriod;
    }

    public void setWorkPeriod(Period workPeriod) {
        this.workPeriod = workPeriod;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Address getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(Address workAddress) {
        this.workAddress = workAddress;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * 여기서 toString을 쓰면, 당연하게 team의 toString도 호출된다 (team이라는 객체를 가지고 있으므로)
     * 그러면 team객체에서도 member관견 객체를 양뱡향으로 매핑하고 있으면, 당연히 무한 루프 발생
     *
     *  이외에도 JSON 생성 라이브러리 등을 사용할 때, Entity를 그대로 반환하면, 위와 같은 무한 루프 문제가 발생할 수 있다.
     *  그러므로, DTO를 이용하여 쓰는 것이 맞다...
     */
}
