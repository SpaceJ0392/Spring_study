package jpql;

import jakarta.persistence.*;
import jpql.domain.Member;
import jpql.domain.MemberDTO;
import jpql.domain.MemberType;
import jpql.domain.Team;

import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();
        try{
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member");
            member.setAge(10);
            member.setType(MemberType.ADMIN);
            member.changeTeam(team);
            em.persist(member);

            Member result = em.createQuery("select m from Member as m where m.username = :username", Member.class) //반환대상 확실
                    .setParameter("username", "member").getSingleResult();//파리미터 바인딩
            TypedQuery<Integer> query2 = em.createQuery("select m.age from Member as m", Integer.class); //반환대상 확실
            Query query3 = em.createQuery("select m.username, m.age from Member as m");//반환대상 불확실 1
            //Member result = query1.getSingleResult(); //결과가 무조건 1개일 때, 아니면 error
            //값이 없을 때도 error가 나는데, Spring Data JPA에서는 error시 null이나, Optional을 반환한다. (error 후 해당 값들을 반환하도록 만듬.)
            //List<Member> resultList = query1.getResultList(); //얘는 결과가 없어도 빈 리스트...

            System.out.println("result = " + result.getUsername());

            List<Object[]> resultList = em.createQuery("select m.username, m.age from Member as m").getResultList();// 반환 대상이 불확실할 때 2 (비권장)
            Object[] objects = resultList.get(0);
            System.out.println("objects = " + objects[0]);
            System.out.println("objects = " + objects[1]);

            List<MemberDTO> memberDTOList = em.createQuery("select new jpql.domain.MemberDTO(m.username, m.age) from Member as m", MemberDTO.class).getResultList();// 반환 대상이 불확실할 때 3 (권장)
            MemberDTO memberDTO = memberDTOList.get(0);
            System.out.println("memberDTO.username = " + memberDTO.getUsername());
            System.out.println("memberDTO.age = " + memberDTO.getAge());

            //paging
//            for(int i = 1; i <= 100; i++){
//                Member newMember = new Member();
//                newMember.setUsername("member" + i);
//                newMember.setAge(i);
//                em.persist(newMember);
//            }


            List<Member> resultList1 = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(0)
                    .setMaxResults(10)
                    .getResultList();

            System.out.println("resultList1.size() = " + resultList1.size());
            for (Member member1 : resultList1) {
                System.out.println("member = " + member1);
            }

            //조인
            List<Member> resultList2 = em.createQuery("select m from Member m join m.team t", Member.class)
                    .getResultList();

            List<Object[]> resultList3 = em.createQuery("select m.username, 'HELLO', " +
                    "true from Member m where m.type = :usertype").setParameter("usertype", MemberType.ADMIN)
                    .getResultList();
            for (Object[] objects1 : resultList3) {
                System.out.println("objects1 = " + objects1[0]);
                System.out.println("objects1 = " + objects1[1]);
                System.out.println("objects1 = " + objects1[2]);
            }

            //조건식
            List<String> resultList4 = em.createQuery("select " +
                    "case when m.age <= 10 then '학생요금'" +
                    " when m.age >= 60 then '경로요금'" +
                    " else '일반요금' end " +
                    "from Member m", String.class).getResultList();

            for (String s : resultList4) {
                System.out.println("s = " + s);
            }

            List<String> resultList5 = em.createQuery("select coalesce(m.username, '이름 없는 회원') " +
                    "from Member m", String.class).getResultList(); //coalesce 값이 null 이면 2번째 파라미터로 반환

            for (String s : resultList5) {
                System.out.println("s = " + s);
            }

            List<String> resultList6 = em.createQuery("select nullif(m.username, 'member') " +
                    "from Member m", String.class).getResultList(); //nullif 값이 둘이 같으면 null

            for (String s : resultList6) {
                System.out.println("s = " + s);
            }

            //사용자 정의 함수
            List<String> resultList7 = em.createQuery("select group_concat(m.username) " +
                    "from Member m", String.class).getResultList(); //hibernate 식 (PPT는 표준 문법)
            for (String s : resultList7) {
                System.out.println("s = " + s);
            }

            //fetch join
            Team team1 = new Team();
            team1.setName("팀A");
            em.persist(team1);

            Team team2 = new Team();
            team2.setName("팀B");
            em.persist(team2);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.changeTeam(team1);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.changeTeam(team1);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.changeTeam(team2);
            em.persist(member3);

            List<Member> fetchResult = em.createQuery("select m from Member m join fetch m.team", Member.class).getResultList();
            for (Member fetchMember : fetchResult) {
                System.out.println("fetchMember = " + fetchMember + fetchMember.getTeam().getName());
            }


            List<Team> resultList8 = em.createQuery("select t from Team t join fetch t.members", Team.class)
                    //컬랙션 패치 시, 데이터가 불어나는 문제가 있으므로 batch size를 지정하거나, 다 대 일의 형식으로 페이징을 가져와야 한다.
                    .setFirstResult(0)
                    .setMaxResults(10)
                    .getResultList();

            for (Team team3 : resultList8) {
                System.out.println("team3 = " + team3.getName());
                for (Member team3Member : team3.getMembers()) {
                    System.out.println("team3Member = " + team3Member);
                }
            }

            //Named Query
            List<Member> resultList9 = em.createNamedQuery("Member.findByUserName", Member.class)
                    .setParameter("username", "회원1")
                    .getResultList();
            for (Member member4 : resultList9) {
                System.out.println("member4 = " + member4);
            }

            //벌크 연산 - 여러개를 한번에 수정하는 연산 (update - 여러건을 한번에)
            int updateCnt = em.createQuery("update Member m set m.age = 20").executeUpdate();

            tx.commit();
        } catch (Exception e){
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();


    }
}
