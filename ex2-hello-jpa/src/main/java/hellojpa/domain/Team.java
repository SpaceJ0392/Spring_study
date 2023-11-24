package hellojpa.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team") //DB는 pk만으로 이미 양방향 이므로 바뀌는 게 없음
    // team과 member의 관계는 1 : 다 (mappedby는 반대편의 객체의 어떤 요소와 연결되는지 알림. - 여기서는 Member의 team)
    private List<Member> members = new ArrayList<>();

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
    public List<Member> getMembers() {
        return members;
    }
}
