package com.universite.inscriptions.mapper;

import com.universite.inscriptions.dto.request.EnseignantRequest;
import com.universite.inscriptions.dto.response.EnseignantResponse;
import com.universite.inscriptions.entity.Enseignant;
import org.springframework.stereotype.Component;

@Component
public class EnseignantMapper {

    public Enseignant toEntity(EnseignantRequest request) {
        if (request == null) return null;
        return Enseignant.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .build();
    }

    public EnseignantResponse toResponse(Enseignant enseignant) {
        if (enseignant == null) return null;
        return EnseignantResponse.builder()
                .id(enseignant.getId())
                .nom(enseignant.getNom())
                .prenom(enseignant.getPrenom())
                .email(enseignant.getEmail())
                .build();
    }
}
