package spring_study.querydsl.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"team"})
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String membername;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String membername) {
        this.membername = membername;
    }

    public Member(String membername, int age) {
        this.membername = membername;
        this.age = age;
    }

    public Member(String membername, int age, Team team) {
        this.membername = membername;
        this.age = age;
        if (team != null) {
            createTeam(team);
        }
    }

    public void createTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

    public void changeTeam(Team before, Team after) {
        before.getMembers().remove(this);
        this.team = after;
        after.getMembers().add(this);
    }
}
