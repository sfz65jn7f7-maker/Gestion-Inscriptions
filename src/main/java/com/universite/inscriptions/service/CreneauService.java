package com.universite.inscriptions.service;

import com.universite.inscriptions.dto.request.CreneauRequest;
import com.universite.inscriptions.dto.response.CreneauResponse;
import com.universite.inscriptions.entity.Creneau;
import com.universite.inscriptions.entity.Module;
import com.universite.inscriptions.exception.BusinessException;
import com.universite.inscriptions.exception.ResourceNotFoundException;
import com.universite.inscriptions.mapper.CreneauMapper;
import com.universite.inscriptions.repository.CreneauRepository;
import com.universite.inscriptions.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreneauService {

    private final CreneauRepository creneauRepository;
    private final ModuleRepository moduleRepository;
    private final CreneauMapper creneauMapper;

    @Transactional
    public CreneauResponse creerCreneau(CreneauRequest request) {
        if (!request.getHeureFin().isAfter(request.getHeureDebut())) {
            throw new BusinessException("L'heure de fin doit être postérieure à l'heure de début.");
        }

        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module introuvable avec l'ID : " + request.getModuleId()));

        Creneau creneau = creneauMapper.toEntity(request, module);
        Creneau savedCreneau = creneauRepository.save(creneau);
        return creneauMapper.toResponse(savedCreneau);
    }

    public CreneauResponse obtenirCreneauParId(Long id) {
        Creneau creneau = creneauRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau introuvable avec l'ID : " + id));
        return creneauMapper.toResponse(creneau);
    }

    public List<CreneauResponse> listerTousLesCreneaux() {
        return creneauRepository.findAll().stream()
                .map(creneauMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void supprimerCreneau(Long id) {
        Creneau creneau = creneauRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau introuvable avec l'ID : " + id));
        creneauRepository.delete(creneau);
    }
}
