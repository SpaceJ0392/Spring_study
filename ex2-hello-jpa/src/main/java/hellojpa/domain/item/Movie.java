package hellojpa.domain.item;

import jakarta.persistence.Entity;

@Entity
public class Movie extends Item{
    private String driector;
    private String actor;
}
