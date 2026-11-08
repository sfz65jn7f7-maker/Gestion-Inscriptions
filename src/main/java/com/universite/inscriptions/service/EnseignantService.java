package com.universite.inscriptions.service;

import com.universite.inscriptions.dto.request.EnseignantRequest;
import com.universite.inscriptions.dto.response.EnseignantResponse;
import com.universite.inscriptions.entity.Enseignant;
import com.universite.inscriptions.exception.BusinessException;
import com.universite.inscriptions.exception.ResourceNotFoundException;
import com.universite.inscriptions.mapper.EnseignantMapper;
import com.universite.inscriptions.repository.EnseignantRepository;
import com.universite.inscriptions.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnseignantService {

    private final EnseignantRepository enseignantRepository;
    private final ModuleRepository moduleRepository;
    private final EnseignantMapper enseignantMapper;

    @Transactional
    public EnseignantResponse creerEnseignant(EnseignantRequest request) {
        if (enseignantRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Un enseignant avec cet email existe déjà : " + request.getEmail());
        }

        Enseignant enseignant = enseignantMapper.toEntity(request);
        Enseignant savedEnseignant = enseignantRepository.save(enseignant);
        return enseignantMapper.toResponse(savedEnseignant);
    }

    public EnseignantResponse obtenirEnseignantParId(Long id) {
        Enseignant enseignant = enseignantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant introuvable avec l'ID : " + id));
        return enseignantMapper.toResponse(enseignant);
    }

    public List<EnseignantResponse> listerTousLesEnseignants() {
        return enseignantRepository.findAll().stream()
                .map(enseignantMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void supprimerEnseignant(Long id) {
        Enseignant enseignant = enseignantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant introuvable avec l'ID : " + id));

        if (moduleRepository.existsByEnseignantId(id)) {
            throw new BusinessException("Impossible de supprimer l'enseignant : il est encore assigné à un ou plusieurs modules.");
        }

        enseignantRepository.delete(enseignant);
    }
}
