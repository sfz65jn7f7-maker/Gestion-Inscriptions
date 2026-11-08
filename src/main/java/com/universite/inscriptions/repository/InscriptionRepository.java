package com.universite.inscriptions.repository;

import com.universite.inscriptions.entity.Etudiant;
import com.universite.inscriptions.entity.Inscription;
import com.universite.inscriptions.entity.Module;
import com.universite.inscriptions.enums.StatutInscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    // Vérifier si un étudiant est déjà inscrit à un module
    boolean existsByEtudiantAndModule(Etudiant etudiant, Module module);

    // Trouver toutes les inscriptions d'un étudiant par son ID
    List<Inscription> findByEtudiantId(Long etudiantId);

    // Compter les inscriptions selon leur statut pour un module donné (ex: compter les VALIDE pour les places)
    long countByModuleAndStatut(Module module, StatutInscription statut);

    boolean existsByEtudiantId(Long etudiantId);

    boolean existsByModuleId(Long moduleId);
}