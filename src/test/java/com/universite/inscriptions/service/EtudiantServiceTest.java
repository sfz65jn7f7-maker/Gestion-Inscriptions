package com.universite.inscriptions.service;

import com.universite.inscriptions.dto.request.EtudiantRequest;
import com.universite.inscriptions.dto.response.EtudiantResponse;
import com.universite.inscriptions.entity.Etudiant;
import com.universite.inscriptions.exception.ResourceNotFoundException;
import com.universite.inscriptions.mapper.EtudiantMapper;
import com.universite.inscriptions.repository.EtudiantRepository;
import com.universite.inscriptions.repository.InscriptionRepository;
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
class EtudiantServiceTest {

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private InscriptionRepository inscriptionRepository;

    @Mock
    private EtudiantMapper etudiantMapper;

    @InjectMocks
    private EtudiantService etudiantService;

    private Etudiant etudiant;
    private EtudiantRequest request;
    private EtudiantResponse etudiantResponse;

    @BeforeEach
    void setUp() {
        etudiant = Etudiant.builder()
                .id(1L)
                .nom("Dupont")
                .prenom("Jean")
                .email("jean.dupont@univ.fr")
                .build();

        request = new EtudiantRequest();
        request.setNom("Dupont");
        request.setPrenom("Jean");
        request.setEmail("jean.dupont@univ.fr");

        etudiantResponse = EtudiantResponse.builder()
                .id(1L)
                .nom("Dupont")
                .prenom("Jean")
                .email("jean.dupont@univ.fr")
                .build();
    }

    @Test
    void creerEtudiant_Succes() {
        when(etudiantRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(etudiantMapper.toEntity(request)).thenReturn(etudiant);
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);
        when(etudiantMapper.toResponse(etudiant)).thenReturn(etudiantResponse);

        EtudiantResponse response = etudiantService.creerEtudiant(request);

        assertNotNull(response);
        verify(etudiantRepository, times(1)).save(any(Etudiant.class));
    }

    @Test
    void obtenirEtudiantParId_Succes() {
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));
        when(etudiantMapper.toResponse(etudiant)).thenReturn(etudiantResponse);

        EtudiantResponse response = etudiantService.obtenirEtudiantParId(1L);

        assertNotNull(response);
        verify(etudiantRepository, times(1)).findById(1L);
    }

    @Test
    void obtenirEtudiantParId_Introuvable() {
        when(etudiantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            etudiantService.obtenirEtudiantParId(1L);
        });
    }

    @Test
    void listerTousLesEtudiants_Succes() {
        when(etudiantRepository.findAll()).thenReturn(List.of(etudiant));
        when(etudiantMapper.toResponse(etudiant)).thenReturn(etudiantResponse);

        List<EtudiantResponse> responses = etudiantService.listerTousLesEtudiants();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(etudiantRepository, times(1)).findAll();
    }
}
