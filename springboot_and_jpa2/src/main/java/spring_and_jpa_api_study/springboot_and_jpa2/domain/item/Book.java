package spring_and_jpa_api_study.springboot_and_jpa2.domain.item;

import jakarta.persistence.Entity;

@Entity
public class Book extends Item{
    private String author;
    private String isbn;
}
