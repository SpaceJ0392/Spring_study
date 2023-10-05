package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //가급적 JPA 관련 로직들은 모두 트랜잭션 내에서 작동해야 한다. (그래야, Lazy 로딩 등이 다 된다.)
//사실 class 위에 트랜잭션이 존재하면, 아래의 public 메소드에는 다 먹힌다. 그런데, 하위에 메소드에 따로 처리해도 됨...
// (하위 메서드에 적용한 것은 더 우선권을 가짐.)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    //회원 가입
    @Transactional
    public Long join(Member member){
        //validation (중복 회원 검증)
        validateDuplicateMember(member);
        //save
        memberRepository.save(member);
        return member.getId(); // 영속성으로 보장하므로 id값이 보장된다.
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (! findMembers.isEmpty()) {
            //Exception
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    //@Transactional(readOnly = true) //읽기 전용으로 처리되서 보다 가볍게 처리 (더티 체킹 등 절차들을 간소화)
    public List<Member> findAll(){
        return memberRepository.findAll();
    }

    //@Transactional(readOnly = true)
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
