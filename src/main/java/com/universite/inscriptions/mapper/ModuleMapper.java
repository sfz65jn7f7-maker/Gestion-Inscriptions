package com.universite.inscriptions.mapper;

import com.universite.inscriptions.dto.request.ModuleRequest;
import com.universite.inscriptions.dto.response.ModuleResponse;
import com.universite.inscriptions.entity.Enseignant;
import com.universite.inscriptions.entity.Module;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ModuleMapper {

    public Module toEntity(ModuleRequest request, Enseignant enseignant) {
        if (request == null) return null;
        return Module.builder()
                .code(request.getCode())
                .nom(request.getNom())
                .nombrePlaces(request.getNombrePlaces())
                .semestre(request.getSemestre())
                .enseignant(enseignant)
                .build();
    }

    public ModuleResponse toResponse(Module module, long placesRestantes) {
        if (module == null) return null;

        String nomEnseignant = (module.getEnseignant() != null)
                ? module.getEnseignant().getPrenom() + " " + module.getEnseignant().getNom()
                : "Non assigné";

        return ModuleResponse.builder()
                .id(module.getId())
                .code(module.getCode())
                .nom(module.getNom())
                .nombrePlaces(module.getNombrePlaces())
                .placesRestantes(placesRestantes)
                .semestre(module.getSemestre())
                .nomEnseignant(nomEnseignant)
                .codesPrerequis(module.getPrerequis() != null ?
                        module.getPrerequis().stream().map(Module::getCode).collect(Collectors.toSet()) : null)
                .build();
    }
}