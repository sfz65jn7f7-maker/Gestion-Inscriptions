package com.universite.inscriptions.service;

import com.universite.inscriptions.dto.request.EtudiantRequest;
import com.universite.inscriptions.dto.response.EtudiantResponse;
import com.universite.inscriptions.entity.Etudiant;
import com.universite.inscriptions.exception.BusinessException;
import com.universite.inscriptions.exception.ResourceNotFoundException;
import com.universite.inscriptions.mapper.EtudiantMapper;
import com.universite.inscriptions.repository.EtudiantRepository;
import com.universite.inscriptions.repository.InscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final InscriptionRepository inscriptionRepository;
    private final EtudiantMapper etudiantMapper;

    @Transactional
    public EtudiantResponse creerEtudiant(EtudiantRequest request) {
        // Vérifier si l'email existe déjà
        if (etudiantRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Un étudiant avec cet email existe déjà : " + request.getEmail());
        }

        Etudiant etudiant = etudiantMapper.toEntity(request);
        Etudiant savedEtudiant = etudiantRepository.save(etudiant);
        return etudiantMapper.toResponse(savedEtudiant);
    }

    public EtudiantResponse obtenirEtudiantParId(Long id) {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable avec l'ID : " + id));
        return etudiantMapper.toResponse(etudiant);
    }

    public List<EtudiantResponse> listerTousLesEtudiants() {
        return etudiantRepository.findAll().stream()
                .map(etudiantMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void supprimerEtudiant(Long id) {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable avec l'ID : " + id));

        if (inscriptionRepository.existsByEtudiantId(id)) {
            throw new BusinessException("Impossible de supprimer l'étudiant : des inscriptions sont encore associées.");
        }

        etudiantRepository.delete(etudiant);
    }
}