package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public Long saveItem(Item item){
        itemRepository.save(item);
        return item.getId();
    }

    @Transactional //준영속성 객체를 영속성 객체에 set하여 dirty checking으로 처리
    public void updateItem(Long itemId, int price, String name, int stockQuantity){
        Item item = itemRepository.findOne(itemId);
        item.setPrice(price);
        item.setName(name);
        item.setStockQuantity(stockQuantity);
        //... 이러한 형태로 처리 가능.
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }
}
