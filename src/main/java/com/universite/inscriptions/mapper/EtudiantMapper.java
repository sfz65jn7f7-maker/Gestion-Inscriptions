package com.universite.inscriptions.mapper;

import com.universite.inscriptions.dto.request.EtudiantRequest;
import com.universite.inscriptions.dto.response.EtudiantResponse;
import com.universite.inscriptions.entity.Etudiant;
import org.springframework.stereotype.Component;

@Component
public class EtudiantMapper {

    public Etudiant toEntity(EtudiantRequest request) {
        if (request == null) return null;
        return Etudiant.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .niveau(request.getNiveau())
                .filiere(request.getFiliere())
                .build();
    }

    public EtudiantResponse toResponse(Etudiant etudiant) {
        if (etudiant == null) return null;
        return EtudiantResponse.builder()
                .id(etudiant.getId())
                .nom(etudiant.getNom())
                .prenom(etudiant.getPrenom())
                .email(etudiant.getEmail())
                .niveau(etudiant.getNiveau())
                .filiere(etudiant.getFiliere())
                .build();
    }
}