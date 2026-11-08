package com.universite.inscriptions.service;

import com.universite.inscriptions.dto.request.InscriptionRequest;
import com.universite.inscriptions.dto.response.InscriptionResponse;
import com.universite.inscriptions.entity.Etudiant;
import com.universite.inscriptions.entity.Inscription;
import com.universite.inscriptions.entity.Module;
import com.universite.inscriptions.enums.StatutInscription;
import com.universite.inscriptions.exception.BusinessException;
import com.universite.inscriptions.exception.ResourceNotFoundException;
import com.universite.inscriptions.mapper.InscriptionMapper;
import com.universite.inscriptions.repository.EtudiantRepository;
import com.universite.inscriptions.repository.InscriptionRepository;
import com.universite.inscriptions.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final EtudiantRepository etudiantRepository;
    private final ModuleRepository moduleRepository;
    private final InscriptionMapper inscriptionMapper;

    @Transactional
    public InscriptionResponse inscrireEtudiant(InscriptionRequest request) {
        // 1. Récupérer l'étudiant
        Etudiant etudiant = etudiantRepository.findById(request.getEtudiantId())
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable avec l'ID : " + request.getEtudiantId()));

        // 2. Récupérer le module
        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module introuvable avec l'ID : " + request.getModuleId()));

        // 3. Anti-doublon : Vérifier si l'inscription existe déjà
        if (inscriptionRepository.existsByEtudiantAndModule(etudiant, module)) {
            throw new BusinessException("L'étudiant est déjà inscrit à ce module.");
        }

        // 4. Contrôle de capacité
        long nombreInscritsValides = inscriptionRepository.countByModuleAndStatut(module, StatutInscription.VALIDE);
        if (nombreInscritsValides >= module.getNombrePlaces()) {
            throw new BusinessException("Capacité maximale atteinte pour ce module (" + module.getNombrePlaces() + " places).");
        }

        // 5. Validation avancée des prérequis
        Set<Module> prerequis = module.getPrerequis();
        if (prerequis != null && !prerequis.isEmpty()) {
            List<Inscription> inscriptionsEtudiant = inscriptionRepository.findByEtudiantId(etudiant.getId());
            Set<Long> modulesValidesIds = inscriptionsEtudiant.stream()
                    .filter(i -> i.getStatut() == StatutInscription.VALIDE)
                    .map(i -> i.getModule().getId())
                    .collect(Collectors.toSet());

            for (Module prereq : prerequis) {
                if (!modulesValidesIds.contains(prereq.getId())) {
                    throw new BusinessException("Prérequis non validé : L'étudiant doit valider le module "
                            + prereq.getCode() + " (" + prereq.getNom() + ") avant de s'inscrire.");
                }
            }
        }

        // 6. Sauvegarde de l'inscription
        Inscription inscription = Inscription.builder()
                .dateInscription(LocalDate.now())
                .statut(StatutInscription.VALIDE)
                .etudiant(etudiant)
                .module(module)
                .build();

        Inscription savedInscription = inscriptionRepository.save(inscription);
        return inscriptionMapper.toResponse(savedInscription);
    }

    public List<InscriptionResponse> getInscriptionsParEtudiant(Long etudiantId) {
        if (!etudiantRepository.existsById(etudiantId)) {
            throw new ResourceNotFoundException("Étudiant introuvable avec l'ID : " + etudiantId);
        }

        return inscriptionRepository.findByEtudiantId(etudiantId).stream()
                .map(inscriptionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void supprimerInscription(Long id) {
        Inscription inscription = inscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription introuvable avec l'ID : " + id));
        inscriptionRepository.delete(inscription);
    }
}