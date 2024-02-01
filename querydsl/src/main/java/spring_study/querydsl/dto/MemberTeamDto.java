package spring_study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class MemberTeamDto {

    private Long memberId;
    private String membername;
    private int age;
    private Long teamId;
    private String teamname;

    @QueryProjection //쓰려면 만들고, compile.java로 q파일 만들어야 함 - dto의 순수성이 떨어지는 단점이 있음.
    public MemberTeamDto(Long memberId, String membername, int age, Long teamId, String teamname) {
        this.memberId = memberId;
        this.membername = membername;
        this.age = age;
        this.teamId = teamId;
        this.teamname = teamname;
    }
}
