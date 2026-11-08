package com.universite.inscriptions.service;

import com.universite.inscriptions.dto.request.EnseignantRequest;
import com.universite.inscriptions.dto.response.EnseignantResponse;
import com.universite.inscriptions.entity.Enseignant;
import com.universite.inscriptions.exception.ResourceNotFoundException;
import com.universite.inscriptions.mapper.EnseignantMapper;
import com.universite.inscriptions.repository.EnseignantRepository;
import com.universite.inscriptions.repository.ModuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnseignantServiceTest {

    @Mock
    private EnseignantRepository enseignantRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private EnseignantMapper enseignantMapper;

    @InjectMocks
    private EnseignantService enseignantService;

    private Enseignant enseignant;
    private EnseignantRequest request;
    private EnseignantResponse enseignantResponse;

    @BeforeEach
    void setUp() {
        enseignant = Enseignant.builder()
                .id(1L)
                .nom("Martin")
                .prenom("Alice")
                .email("alice.martin@univ.fr")
                .build();

        request = new EnseignantRequest();
        request.setNom("Martin");
        request.setPrenom("Alice");
        request.setEmail("alice.martin@univ.fr");

        enseignantResponse = EnseignantResponse.builder()
                .id(1L)
                .nom("Martin")
                .prenom("Alice")
                .email("alice.martin@univ.fr")
                .build();
    }

    @Test
    void creerEnseignant_Succes() {
        when(enseignantRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(enseignantMapper.toEntity(request)).thenReturn(enseignant);
        when(enseignantRepository.save(any(Enseignant.class))).thenReturn(enseignant);
        when(enseignantMapper.toResponse(enseignant)).thenReturn(enseignantResponse);

        EnseignantResponse response = enseignantService.creerEnseignant(request);

        assertNotNull(response);
        verify(enseignantRepository, times(1)).save(any(Enseignant.class));
    }

    @Test
    void obtenirEnseignantParId_Succes() {
        when(enseignantRepository.findById(1L)).thenReturn(Optional.of(enseignant));
        when(enseignantMapper.toResponse(enseignant)).thenReturn(enseignantResponse);

        EnseignantResponse response = enseignantService.obtenirEnseignantParId(1L);

        assertNotNull(response);
        verify(enseignantRepository, times(1)).findById(1L);
    }

    @Test
    void obtenirEnseignantParId_Introuvable() {
        when(enseignantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            enseignantService.obtenirEnseignantParId(1L);
        });
    }

    @Test
    void listerTousLesEnseignants_Succes() {
        when(enseignantRepository.findAll()).thenReturn(List.of(enseignant));
        when(enseignantMapper.toResponse(enseignant)).thenReturn(enseignantResponse);

        List<EnseignantResponse> responses = enseignantService.listerTousLesEnseignants();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(enseignantRepository, times(1)).findAll();
    }
}
