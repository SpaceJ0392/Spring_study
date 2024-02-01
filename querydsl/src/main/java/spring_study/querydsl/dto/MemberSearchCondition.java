package spring_study.querydsl.dto;

import lombok.Data;

@Data
public class MemberSearchCondition {
    //회원명, 팀명, 나이 (ageGoe, ageLoe)

    private String membername;
    private String teamname;

    private Integer ageGoe; //나이 이상
    private Integer ageLoe; //나이 이하
}
