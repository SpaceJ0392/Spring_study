package spring_study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spring_study.querydsl.entity.Hello;
import spring_study.querydsl.entity.QHello;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class QuerydslApplicationTests {

    @PersistenceContext
    private EntityManager em;

    @Test
    void contextLoads() {
        Hello hello = new Hello();
        em.persist(hello);

        JPAQueryFactory query = new JPAQueryFactory(em);
        QHello qHello = new QHello("h");
        //QHello qHello = QHello.hello; //위와 동일 -- queryDSL에서 자동으로 만들어둔 new 함수.

        Hello res = query.selectFrom(qHello)
                .fetchOne();

        assertThat(res).isEqualTo(hello);
        assertThat(res.getId()).isEqualTo(hello.getId());
    }

}
