package com.universite.inscriptions.mapper;

import com.universite.inscriptions.dto.request.CreneauRequest;
import com.universite.inscriptions.dto.response.CreneauResponse;
import com.universite.inscriptions.entity.Creneau;
import com.universite.inscriptions.entity.Module;
import org.springframework.stereotype.Component;

@Component
public class CreneauMapper {

    public Creneau toEntity(CreneauRequest request, Module module) {
        if (request == null) return null;
        return Creneau.builder()
                .jour(request.getJour())
                .heureDebut(request.getHeureDebut())
                .heureFin(request.getHeureFin())
                .salle(request.getSalle())
                .module(module)
                .build();
    }

    public CreneauResponse toResponse(Creneau creneau) {
        if (creneau == null) return null;
        return CreneauResponse.builder()
                .id(creneau.getId())
                .jour(creneau.getJour())
                .heureDebut(creneau.getHeureDebut())
                .heureFin(creneau.getHeureFin())
                .salle(creneau.getSalle())
                .build();
    }
}
