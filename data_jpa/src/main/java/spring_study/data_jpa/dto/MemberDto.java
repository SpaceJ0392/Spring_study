package spring_study.data_jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import spring_study.data_jpa.entity.Member;

@Data
@AllArgsConstructor
public class MemberDto {

    private String userName;
    private int age;

    private String teamName;

    public MemberDto(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }

    public MemberDto(Member member) { //entity는 dto에 접근하지 않는게 맞지만, dto가 entity의 데이터를 가져오는 건 됨.
        this.userName = member.getUserName();
        this.age = member.getAge();
    }
}
