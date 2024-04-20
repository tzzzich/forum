package ru.tsu.hits24.secondsbproject.jpa.entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.List;

@Entity
@Table(name = "categories")
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Setter
@Getter
@Builder
public class CategoryEntity extends ForumEntity{

    @Column(nullable = false)
    private String name;

//    @Column(nullable = false)
//    private boolean containsTopics;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private CategoryEntity parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private List<CategoryEntity> subcategories;

    @OneToMany(mappedBy = "category")
    private List<TopicEntity> topics;

    @ManyToMany
    @JoinTable(
            name = "category_moderators",
            joinColumns = @JoinColumn(
                    name = "category_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"))
    private List<UserEntity> moderators;
}
