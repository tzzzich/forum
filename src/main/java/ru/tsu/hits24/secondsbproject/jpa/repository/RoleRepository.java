package ru.tsu.hits24.secondsbproject.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tsu.hits24.secondsbproject.jpa.entity.Role;
import ru.tsu.hits24.secondsbproject.jpa.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByName(String name) ;
}