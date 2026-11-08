package com.universite.inscriptions.mapper;

import com.universite.inscriptions.dto.response.InscriptionResponse;
import com.universite.inscriptions.entity.Inscription;
import org.springframework.stereotype.Component;

@Component
public class InscriptionMapper {

    public InscriptionResponse toResponse(Inscription inscription) {
        if (inscription == null) return null;

        return InscriptionResponse.builder()
                .id(inscription.getId())
                .dateInscription(inscription.getDateInscription())
                .statut(inscription.getStatut())
                .nomEtudiant(inscription.getEtudiant() != null ? inscription.getEtudiant().getNom() : null)
                .prenomEtudiant(inscription.getEtudiant() != null ? inscription.getEtudiant().getPrenom() : null)
                .codeModule(inscription.getModule() != null ? inscription.getModule().getCode() : null)
                .nomModule(inscription.getModule() != null ? inscription.getModule().getNom() : null)
                .build();
    }
}