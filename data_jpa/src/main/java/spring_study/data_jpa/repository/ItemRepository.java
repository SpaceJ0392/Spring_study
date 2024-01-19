package spring_study.data_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring_study.data_jpa.entity.Item;

public interface ItemRepository extends JpaRepository<Item, String> {
}
