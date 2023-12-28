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
        return new Hibernate5JakartaModule();
    }
}
