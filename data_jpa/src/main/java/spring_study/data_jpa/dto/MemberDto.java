package spring_study.data_jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberDto {

    private String userName;
    private int age;

    private String teamName;
}
