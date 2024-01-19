package spring_study.data_jpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spring_study.data_jpa.entity.Item;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    @Test //data_jpa는 구현체에 트랜잭션이 붙어 있어, 외부에서 그냥 데이터 넣어도 저장된다.
    //단, 영속성을 보장하기 위해서는 외부어서 트랜잭션을 붙여, 외부의 트랜잭션을 그냥 사용해야 한다.
    public void saveTest() throws Exception {
        itemRepository.save(new Item("A"));
    }

}