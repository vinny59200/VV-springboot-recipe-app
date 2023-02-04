package com.vv.recipe.vv.model;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Builder(toBuilder = true)
public class VVRegistration {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String role;

}
