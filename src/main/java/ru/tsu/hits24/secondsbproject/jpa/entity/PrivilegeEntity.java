package ru.tsu.hits24.secondsbproject.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "privilrges")
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Setter
@Getter
public class PrivilegeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /*@ManyToMany(mappedBy = "privileges")
    private List<RoleEntity> roles;


     */
    public PrivilegeEntity(String name) {
        this.name = name;
    }
}
