package hellojpa;

import hellojpa.domain.Member;
import hellojpa.domain.embadded.Address;
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

//            Team team = new Team();
//            team.setName("TeamA");
//            em.persist(team);
//
//            Team newteam = new Team();
//            newteam.setName("TeamB");
//            em.persist(newteam);
//
//            Member member = new Member();
//            member.setName("memberA");
//            member.changeTeam(team); //연관관계 편의 메소드 (양방향으로 객체가 매핑되게 구성 - JPA 자체는 주인만 알면되기는 함.)
//            //양 방향 매핑 시, 일반적으로 주인이 되는 객체에만 연관관계에 대해 변경 가능. (mapped by로 연결된 객체는 그냥 읽기용)
//            em.persist(member);
//
//            em.flush();
//            em.clear();
//
//            Member findMember = em.find(Member.class, member.getId());
//            List<Member> members = findMember.getTeam().getMembers();
//            for (Member mem : members) {
//                System.out.println("member = " + mem.getName());
//            }

//            Member member = new Member();
//            member.setName("a");
//
//            em.persist(member);
//
//            em.flush();
//            em.clear();
//
//            Member findMember = em.find(Member.class, member.getId()); //이러면 연관관계까지 매핑되서 join된 값이 나옴...
//            System.out.println("findMember.id = " + findMember.getId());
//            System.out.println("findMember.name = " + findMember.getName());
//
//            Member reference = em.getReference(Member.class, member.getId()); //이건 퀴리 생성 X (나중에 실제 사용 시 지연 로딩)
//            System.out.println("reference = " + reference.getClass()); //getReference시, proxy를 생성함을 알 수 있음
//            System.out.println("reference 초기화 확인 메소드 = " + emf.getPersistenceUnitUtil().isLoaded(reference));
//            Hibernate.initialize(reference); //강제 초기화 (Hibernate 설정 - not JPA 옵션)

//            Address address = new Address("city", "street", "10000");
//            Member member1 = new Member();
//            member1.setName("a");
//            member1.setHomeAddress(address);
//            em.persist(member1);
//
//            Address copyAddress = new Address(address.getCity(), address.getStreet(), address.getZipcode());
//
//            Member member2 = new Member();
//            member2.setName("b");
//            member2.setHomeAddress(copyAddress);
//            em.persist(member2);
//
//            member1.getHomeAddress().setCity("new_city");
//            //만약 member2에서 그냥 address를 쓰고 있었으면, member1의 내용을 바꿀 때 같이 바뀌었을 것 (주의!!!) - 그래서 불변 객체로 setter 등을 없앤다.

            //JPQL
            List<Member> resultList = em.createQuery("select m FROM Member as m where m.name like '%kim%'", Member.class)
                    .getResultList();

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();

    }
}