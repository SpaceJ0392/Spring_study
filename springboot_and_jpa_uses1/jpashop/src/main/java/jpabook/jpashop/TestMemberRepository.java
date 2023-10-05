package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class TestMemberRepository { //리포지토리는 엔티티같은 얘들을 찾아주는 역할 (DAO라고 봐도 된다.)

    @PersistenceContext //엔티티매니저 알아서 주입
    private EntityManager em; //Spring data jpa를 주입하면서, 자동으로 그냥 EntityManager가 생성 (설정값 활용해서 펙토리 처럼 생성)

    public Long save(TestMember testMember){ //저장의 경우, 사이드 effect를 일으킬 수 있는 커맨드성이므로 리턴값을 거의 만들지 않는다.
        em.persist(testMember);
        return testMember.getId(); //커맨드랑 쿼리를 분리하기 위해 member가 아닌 id만 반환...
    }

    public TestMember find(Long id){
        return em.find(TestMember.class, id);
    }
}
