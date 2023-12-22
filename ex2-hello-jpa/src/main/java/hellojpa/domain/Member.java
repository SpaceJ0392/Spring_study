package hellojpa.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String name;

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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Team getTeam() {
        return team;
    }
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

    /**
     * 여기서 toString을 쓰면, 당연하게 team의 toString도 호출된다 (team이라는 객체를 가지고 있으므로)
     * 그러면 team객체에서도 member관견 객체를 양뱡향으로 매핑하고 있으면, 당연히 무한 루프 발생
     *
     *  이외에도 JSON 생성 라이브러리 등을 사용할 때, Entity를 그대로 반환하면, 위와 같은 무한 루프 문제가 발생할 수 있다.
     *  그러므로, DTO를 이용하여 쓰는 것이 맞다...
     */
}
