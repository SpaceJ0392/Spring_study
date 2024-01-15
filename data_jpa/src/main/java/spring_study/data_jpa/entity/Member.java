package spring_study.data_jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //jpa가 프록시 기술을 사용하므로 private은 안됨.
@ToString(of = {"id","userName","age"}) //연관관계 필드는 무한루프 유발 가능성이 있어서 team은 배제
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String userName;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String userName) {
        this.userName = userName;
    }

    public Member(String userName, int age, Team team) {
        this.userName = userName;
        this.age = age;
        if (team != null){
            changeTeam(team);
        }

    }

    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }
}
