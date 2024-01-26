package spring_study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor //있어야 QueryDSL에서 Projection 가능
public class MemberDto {

    private String membername;
    private int age;

    @QueryProjection
    public MemberDto(String membername, int age) {
        this.membername = membername;
        this.age = age;
    }
}
