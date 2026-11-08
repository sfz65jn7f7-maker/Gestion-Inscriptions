package com.universite.inscriptions.service;

import com.universite.inscriptions.dto.request.ModuleRequest;
import com.universite.inscriptions.dto.response.ModuleResponse;
import com.universite.inscriptions.entity.Enseignant;
import com.universite.inscriptions.entity.Module;
import com.universite.inscriptions.exception.BusinessException;
import com.universite.inscriptions.exception.ResourceNotFoundException;
import com.universite.inscriptions.mapper.ModuleMapper;
import com.universite.inscriptions.repository.EnseignantRepository;
import com.universite.inscriptions.repository.InscriptionRepository;
import com.universite.inscriptions.repository.ModuleRepository;
import com.universite.inscriptions.enums.StatutInscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final EnseignantRepository enseignantRepository;
    private final InscriptionRepository inscriptionRepository;
    private final ModuleMapper moduleMapper;

    @Transactional
    public ModuleResponse creerModule(ModuleRequest request) {
        // Vérifier si le code du module existe déjà
        if (moduleRepository.findByCode(request.getCode()).isPresent()) {
            throw new BusinessException("Un module avec le code '" + request.getCode() + "' existe déjà.");
        }

        // Récupérer l'enseignant si son ID est fourni
        Enseignant enseignant = null;
        if (request.getEnseignantId() != null) {
            enseignant = enseignantRepository.findById(request.getEnseignantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Enseignant introuvable avec l'ID : " + request.getEnseignantId()));
        }

        // Gérer les prérequis (liste d'IDs de modules)
        Set<Module> prerequis = new HashSet<>();
        if (request.getPrerequisIds() != null && !request.getPrerequisIds().isEmpty()) {
            for (Long prereqId : request.getPrerequisIds()) {
                Module prereqModule = moduleRepository.findById(prereqId)
                        .orElseThrow(() -> new ResourceNotFoundException("Module prérequis introuvable avec l'ID : " + prereqId));
                prerequis.add(prereqModule);
            }
        }

        Module module = moduleMapper.toEntity(request, enseignant);
        module.setPrerequis(prerequis);

        Module savedModule = moduleRepository.save(module);

        // Calculer les places restantes (au début, égal à nombrePlaces)
        long placesRestantes = savedModule.getNombrePlaces();
        return moduleMapper.toResponse(savedModule, placesRestantes);
    }

    public ModuleResponse obtenirModuleParId(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module introuvable avec l'ID : " + id));

        long inscritsValides = inscriptionRepository.countByModuleAndStatut(module, StatutInscription.VALIDE);
        long placesRestantes = module.getNombrePlaces() - inscritsValides;

        return moduleMapper.toResponse(module, Math.max(0, placesRestantes));
    }

    public List<ModuleResponse> listerTousLesModules() {
        return moduleRepository.findAll().stream().map(module -> {
            long inscritsValides = inscriptionRepository.countByModuleAndStatut(module, StatutInscription.VALIDE);
            long placesRestantes = module.getNombrePlaces() - inscritsValides;
            return moduleMapper.toResponse(module, Math.max(0, placesRestantes));
        }).collect(Collectors.toList());
    }

    @Transactional
    public void supprimerModule(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module introuvable avec l'ID : " + id));

        if (inscriptionRepository.existsByModuleId(id)) {
            throw new BusinessException("Impossible de supprimer le module : des étudiants y sont inscrits.");
        }

        if (moduleRepository.existsByPrerequis_Id(id)) {
            throw new BusinessException("Impossible de supprimer le module : il est utilisé comme prérequis d'un autre module.");
        }

        moduleRepository.delete(module);
    }
}