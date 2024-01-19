package spring_study.data_jpa.repository;

public interface NestedClosedProjections {

    String getUserName();
    TeamInfo getTeam(); //중첩 구조 Projection (team까지 가져옴)
    //처음 구조인 user는 최적화 되나, 중첩 구조인 team은 최적화가 안됨.
    interface TeamInfo{
        String getName();
    }
}
