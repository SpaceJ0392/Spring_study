package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpashopApplication {

    public static void main(String[] args) {

        /*
        //just Lombok setting test
        LombokTest test = new LombokTest();
        test.setData("test");
        String data = test.getData();
        System.out.println("data = " + data);
        */

        SpringApplication.run(JpashopApplication.class, args);
    }

}
