package ru.tsu.hits24.secondsbproject.dto.user;

import lombok.Data;
import ru.tsu.hits24.secondsbproject.jpa.entity.RoleEntity;

@Data
public class RoleDto {

    private Long id;

    private String name;

    public RoleDto(RoleEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
