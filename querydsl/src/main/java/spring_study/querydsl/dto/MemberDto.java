package spring_study.querydsl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor //있어야 QueryDSL에서 Projection 가능
@AllArgsConstructor
public class MemberDto {

    private String membername;
    private int age;
}
