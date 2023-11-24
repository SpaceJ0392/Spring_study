package hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        //엔티티 매니저 팩토리는 하나만 생성해서 애플리케이션 전체에서 공유 - DB 당 하나만 웹서버 시작 시 생성

        EntityManager em = emf.createEntityManager();
        EntityTransaction etx = em.getTransaction(); //transaction 없이는 JPA는 commit 되지 않는다.
        etx.begin();

        try{
            // 회원 생성
//            Member member = new Member();
//            member.setId(1L);
//            member.setName("HelloA");
//            em.persist(member);
//            // ID 전략이 Identitiy일 때, DB에 데이터가 들어가야 ID를 알수 있으므로, 예외적으로 persist 시, 바로 insert 하고 영속화
//            // ID 전략이 Sequence는 ID를 알기 위해 persist 시, DB에서 시퀀스 값을 얻어와서 persist하며, 영속화

            // 회원 수정
//            Member findMember = em.find(Member.class, 1L);
//            findMember.setName("HelloJPA");

            //회원 조회
//            Member findMember = em.find(Member.class, 1L);
//            System.out.println("findMember_id = " + findMember.getId());
//            System.out.println("findMember_Name = " + findMember.getName());
//            List<Member> result = em.createQuery("select m from Member as m", Member.class)
//                    .getResultList();
//            for (Member ret :result) {
//                System.out.println("result = " + ret.getName());
//            }

            //회원 삭제
//            Member findMember = em.find(Member.class, 1L);
//            em.remove(findMember);

            etx.commit(); //JPA는 변경점이 발견되면, tranaction이 commit 될 때, 자동적으로 update 한다.
        } catch (Exception e){
            etx.rollback(); //과정 중 실패시 rollback
        } finally {
            em.close(); // 엔티티 매니저가 transaction을 물고 다녀서, 다 하면 닫아줘야 함.
            //엔티티 매니저는 쓰레드 간에 공유X (사용하고 버려야 한다). - 데이터 일관성 문제
        }
        //JPA의 모든 데이터 변경은 트랜잭션 안에서 실행 (조회는 좀 예외 - 변경이 없음)
        //기본적으로 모든 RDB는 데이터 변경에 대해서는 트랜잭션 하에 실행된다.
        emf.close();
    }

}
