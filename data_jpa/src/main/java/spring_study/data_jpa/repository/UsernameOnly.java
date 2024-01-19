package spring_study.data_jpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
    //String getUserName(); //인터페이스 기반읜 closed projection
    /**
     * proxy 기술을 사용하여 Spring data jpa가 자동으로 해당하는 구현체를 만들어서 데이터를 넣어줌.
     *
     * 가장 큰 장점은 쿼리 메소드 형태로 find---by 형태와 함께 작성될 수 있다는 점이다.
     *
     * 이 기술을 통해 원하는 colmun만 가져온다.
     */
    @Value("#{target.userName + ' ' + target.age}")
    String getUserName(); //open projection closed projection과 달리, 다 가져와서 value를 통해 필요 데이터 뽑기
}
