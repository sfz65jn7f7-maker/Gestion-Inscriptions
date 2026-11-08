package com.universite.inscriptions.service;

import com.universite.inscriptions.dto.request.ModuleRequest;
import com.universite.inscriptions.dto.response.ModuleResponse;
import com.universite.inscriptions.entity.Module;
import com.universite.inscriptions.enums.StatutInscription;
import com.universite.inscriptions.exception.ResourceNotFoundException;
import com.universite.inscriptions.mapper.ModuleMapper;
import com.universite.inscriptions.repository.EnseignantRepository;
import com.universite.inscriptions.repository.InscriptionRepository;
import com.universite.inscriptions.repository.ModuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModuleServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private EnseignantRepository enseignantRepository;

    @Mock
    private InscriptionRepository inscriptionRepository;

    @Mock
    private ModuleMapper moduleMapper;

    @InjectMocks
    private ModuleService moduleService;

    private Module module;
    private ModuleRequest request;
    private ModuleResponse moduleResponse;

    @BeforeEach
    void setUp() {
        module = Module.builder()
                .id(1L)
                .code("JAVA101")
                .nom("Java Avancé")
                .nombrePlaces(30)
                .prerequis(new HashSet<>())
                .build();

        request = new ModuleRequest();
        request.setCode("JAVA101");
        request.setNom("Java Avancé");
        request.setNombrePlaces(30);

        moduleResponse = ModuleResponse.builder()
                .id(1L)
                .code("JAVA101")
                .nom("Java Avancé")
                .nombrePlaces(30)
                .placesRestantes(30)
                .build();
    }

    @Test
    void creerModule_Succes() {
        when(moduleRepository.findByCode("JAVA101")).thenReturn(Optional.empty());
        when(moduleMapper.toEntity(eq(request), isNull())).thenReturn(module);
        when(moduleRepository.save(any(Module.class))).thenReturn(module);
        when(moduleMapper.toResponse(eq(module), anyLong())).thenReturn(moduleResponse);

        ModuleResponse response = moduleService.creerModule(request);

        assertNotNull(response);
        verify(moduleRepository, times(1)).save(any(Module.class));
    }

    @Test
    void obtenirModuleParId_Succes() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(module));
        when(inscriptionRepository.countByModuleAndStatut(module, StatutInscription.VALIDE)).thenReturn(0L);
        when(moduleMapper.toResponse(eq(module), anyLong())).thenReturn(moduleResponse);

        ModuleResponse response = moduleService.obtenirModuleParId(1L);

        assertNotNull(response);
        verify(moduleRepository, times(1)).findById(1L);
    }

    @Test
    void obtenirModuleParId_Introuvable() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            moduleService.obtenirModuleParId(1L);
        });
    }

    @Test
    void listerTousLesModules_Succes() {
        when(moduleRepository.findAll()).thenReturn(List.of(module));
        when(inscriptionRepository.countByModuleAndStatut(module, StatutInscription.VALIDE)).thenReturn(0L);
        when(moduleMapper.toResponse(eq(module), anyLong())).thenReturn(moduleResponse);

        List<ModuleResponse> responses = moduleService.listerTousLesModules();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(moduleRepository, times(1)).findAll();
    }
}
