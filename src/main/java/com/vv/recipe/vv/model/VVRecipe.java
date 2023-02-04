package com.vv.recipe.vv.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Builder(toBuilder = true)
public class VVRecipe {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    @Convert(converter = StringListConverter.class)
    private List<String> ingredients;

    @Column@Convert(converter = StringListConverter.class)
    private List<String> directions;

    @Column
    private String category;

    @Column
    private LocalDateTime date;

    @Column
    private String owner;
}
