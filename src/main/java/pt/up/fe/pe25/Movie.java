package pt.up.fe.pe25;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Movie extends PanacheEntity {

    @Column(length = 100)
    public String Title;

    @Column(length = 200)
    public String description;

    public String director;
    public String country;
}
