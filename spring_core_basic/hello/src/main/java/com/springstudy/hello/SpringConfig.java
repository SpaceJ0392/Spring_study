package com.springstudy.hello;

import com.springstudy.hello.repository.MemberRepository;
import com.springstudy.hello.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    //SpringDataJPA
    private final MemberRepository memberRepository; //자동으로 SpringDataJPA의 구현체 등록
    //bean으로 등록된 memberRepository를 찾는데, 등록된 것이 SpringDataJpa로 되있는 거 밖에 없어서 그냥 가져옴.
    public SpringConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    //JPA
/*
    private EntityManager em;

    public SpringConfig(EntityManager em) {
        this.em = em;
    }
*/

    //JPA 이전
/*
    private final DataSource dataSource;
    //Datasource는 Spring이 우리가 application.properties에서 제공한 접근 정보로 알아서 bean을 만드어두어
    //Autowired 가 가능 (물론 configuration도 bean으로 자동 등록)
    @Autowired
    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
*/

    @Bean
    public MemberService memberService(){
        return new MemberService(memberRepository);
    }

/*
    @Bean
    public MemberRepository memberRepository(){
        //return new MemoryMemberRepository(); //기존의 메모리에 저장하던 방식 (DB없을 때)

        //return new JdbcMemberRepository(dataSource); //서버 킬 때, 당연히 h2를 켜놓아야 한다.
        //여기서 놀라운 점은 우리는 코드를 jdbc 리포지토리로 확장만 하고, 기존 코드는 사용하지 않고,
        //DB를 바꾸어서 연결함.

        //return new JdbcTempleteMemberRepository(dataSource);

        //return new JpaMemberRepository(em);

        //SpringDataJPA는 인터페이스가 자동으로 구현체를 만들고, bean으로 등록하므로 config단에서 등록할 필요가 없음.
        //(따라서 해당 메소드 자체가 필요 없음)
    }
*/
}
