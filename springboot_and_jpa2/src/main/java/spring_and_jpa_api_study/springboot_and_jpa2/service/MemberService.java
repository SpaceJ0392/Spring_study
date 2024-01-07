package spring_and_jpa_api_study.springboot_and_jpa2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring_and_jpa_api_study.springboot_and_jpa2.domain.Member;
import spring_and_jpa_api_study.springboot_and_jpa2.repository.MemberDataJpaRepository;
import spring_and_jpa_api_study.springboot_and_jpa2.repository.MemberRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberDataJpaRepository memberDataJpaRepository;

    @Transactional
    public Long join(Member member){

        validateDuplicateMember(member);

        memberDataJpaRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        if (!memberDataJpaRepository.findByName(member.getName()).isEmpty()){
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }

    public List<Member> findAll(){
        return memberDataJpaRepository.findAll();
    }

    public Member findOne(Long id){
        return memberDataJpaRepository.findById(id).get();
    } //findById는 Optional로 반환해서 사실은 Null 등에 대한 처리를 해주어야 하나, 여기서는 그냥 get()으로 처리 (원래는 orElseGet() 등으로 처리)

    @Transactional
    public void update(Long id, String name) {
        Member member = memberDataJpaRepository.findById(id).get();
        member.setName(name);
    }
}
