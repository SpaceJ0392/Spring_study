package spring_study.data_jpa.repository;

import spring_study.data_jpa.entity.Member;

import java.util.List;

public interface MemberCustomRepository {
    List<Member> findMemberCustom();
}
