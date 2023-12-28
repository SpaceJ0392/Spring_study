package spring_and_jpa_api_study.springboot_and_jpa2;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringbootAndJpa2Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootAndJpa2Application.class, args);
    }

    @Bean
    Hibernate5JakartaModule hibernate5Module(){
        Hibernate5JakartaModule hibernate5JakartaModule = new Hibernate5JakartaModule();
//        hibernate5JakartaModule.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, true);
        //Lazy 처리되어 있는걸 다 가져오게 함. (기본 전략은 lazy 값은 proxy 이므로 null을 반환)
        //당연히 좋지 않음... (필요하지 않은 것까지 다 가져옴.)
        return hibernate5JakartaModule;
    }
}
