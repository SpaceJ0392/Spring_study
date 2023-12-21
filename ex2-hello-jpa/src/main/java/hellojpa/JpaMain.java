package hellojpa;

import hellojpa.domain.Member;
import hellojpa.domain.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {

            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Team newteam = new Team();
            newteam.setName("TeamB");
            em.persist(newteam);

            Member member = new Member();
            member.setName("memberA");
            member.changeTeam(team); //연관관계 편의 메소드 (양방향으로 객체가 매핑되게 구성 - JPA 자체는 주인만 알면되기는 함.)
            //양 방향 매핑 시, 일반적으로 주인이 되는 객체에만 연관관계에 대해 변경 가능. (mapped by로 연결된 객체는 그냥 읽기용)
            em.persist(member);

            em.flush();
            em.clear();

            Member findMember = em.find(Member.class, member.getId());
            List<Member> members = findMember.getTeam().getMembers();
            for (Member mem : members) {
                System.out.println("member = " + mem.getName());
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();

    }
}