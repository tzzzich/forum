package ru.tsu.hits24.secondsbproject.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tsu.hits24.secondsbproject.jpa.entity.PrivilegeEntity;
import ru.tsu.hits24.secondsbproject.jpa.entity.RoleEntity;

import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, Long> {
    PrivilegeEntity findByName(String name) ;

}
