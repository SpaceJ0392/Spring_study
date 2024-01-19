package spring_study.data_jpa.repository;

public class UsernameOnlyDto {
    private final String username;

    public UsernameOnlyDto(String userName) {
        this.username = userName;
    }// 생성자가 중요! - 생성자의 파라미터 이름으로 매칭해서 projection 하는게 가능 + getter 이름도 중요

    public String getUserName() {
        return username;
    }
}
