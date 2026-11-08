package com.universite.inscriptions.service;

import com.universite.inscriptions.dto.request.CreneauRequest;
import com.universite.inscriptions.dto.response.CreneauResponse;
import com.universite.inscriptions.entity.Creneau;
import com.universite.inscriptions.entity.Module;
import com.universite.inscriptions.exception.ResourceNotFoundException;
import com.universite.inscriptions.mapper.CreneauMapper;
import com.universite.inscriptions.repository.CreneauRepository;
import com.universite.inscriptions.repository.ModuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreneauServiceTest {

    @Mock
    private CreneauRepository creneauRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private CreneauMapper creneauMapper;

    @InjectMocks
    private CreneauService creneauService;

    private Creneau creneau;
    private Module module;
    private CreneauRequest request;
    private CreneauResponse creneauResponse;

    @BeforeEach
    void setUp() {
        module = Module.builder()
                .id(10L)
                .code("JAVA101")
                .nom("Programmation Java")
                .nombrePlaces(30)
                .build();

        creneau = Creneau.builder()
                .id(1L)
                .jour("LUNDI")
                .heureDebut(LocalTime.of(8, 30))
                .heureFin(LocalTime.of(10, 0))
                .salle("Amphi A")
                .module(module)
                .build();

        request = new CreneauRequest();
        request.setJour("LUNDI");
        request.setHeureDebut(LocalTime.of(8, 30));
        request.setHeureFin(LocalTime.of(10, 0));
        request.setSalle("Amphi A");
        request.setModuleId(10L);

        creneauResponse = CreneauResponse.builder()
                .id(1L)
                .jour("LUNDI")
                .heureDebut(LocalTime.of(8, 30))
                .heureFin(LocalTime.of(10, 0))
                .salle("Amphi A")
                .build();
    }

    @Test
    void creerCreneau_Succes() {
        when(moduleRepository.findById(10L)).thenReturn(Optional.of(module));
        when(creneauMapper.toEntity(request, module)).thenReturn(creneau);
        when(creneauRepository.save(any(Creneau.class))).thenReturn(creneau);
        when(creneauMapper.toResponse(creneau)).thenReturn(creneauResponse);

        CreneauResponse response = creneauService.creerCreneau(request);

        assertNotNull(response);
        verify(creneauRepository, times(1)).save(any(Creneau.class));
    }

    @Test
    void obtenirCreneauParId_Succes() {
        when(creneauRepository.findById(1L)).thenReturn(Optional.of(creneau));
        when(creneauMapper.toResponse(creneau)).thenReturn(creneauResponse);

        CreneauResponse response = creneauService.obtenirCreneauParId(1L);

        assertNotNull(response);
        verify(creneauRepository, times(1)).findById(1L);
    }

    @Test
    void obtenirCreneauParId_Introuvable() {
        when(creneauRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            creneauService.obtenirCreneauParId(1L);
        });
    }

    @Test
    void listerTousLesCreneaux_Succes() {
        when(creneauRepository.findAll()).thenReturn(List.of(creneau));
        when(creneauMapper.toResponse(creneau)).thenReturn(creneauResponse);

        List<CreneauResponse> responses = creneauService.listerTousLesCreneaux();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(creneauRepository, times(1)).findAll();
    }
}
