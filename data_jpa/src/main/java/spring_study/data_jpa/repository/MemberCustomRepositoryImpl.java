package spring_study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import spring_study.data_jpa.entity.Member;

import java.util.List;

@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository {
//해당 리포지토리 이름은 JPA로 받은 MemberRepositoryImpl로 맞추어 주거나,
// 상속받은 MemberCustomRepositoryImpl로 이름을 맞추어 주어야 한다. (그렇지 않으면, 구현체를 가져다 사용하지 않음. - Spring data 2.x)


    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }
}
