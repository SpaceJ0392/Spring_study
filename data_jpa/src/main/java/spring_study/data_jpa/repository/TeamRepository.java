package spring_study.data_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring_study.data_jpa.entity.Team;
//@Repository 없어도 JpaRepository를 상속받아서 자동으로 repository 로 처리됨...
public interface TeamRepository extends JpaRepository<Team, Long> {
}
