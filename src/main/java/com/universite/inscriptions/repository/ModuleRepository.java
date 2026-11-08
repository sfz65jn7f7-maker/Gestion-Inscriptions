package com.universite.inscriptions.repository;

import com.universite.inscriptions.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findByCode(String code);

    boolean existsByEnseignantId(Long enseignantId);

    boolean existsByPrerequis_Id(Long prerequisId);
}