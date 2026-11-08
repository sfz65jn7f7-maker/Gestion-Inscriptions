package com.universite.inscriptions.service;

import com.universite.inscriptions.dto.request.InscriptionRequest;
import com.universite.inscriptions.dto.response.InscriptionResponse;
import com.universite.inscriptions.entity.Etudiant;
import com.universite.inscriptions.entity.Inscription;
import com.universite.inscriptions.entity.Module;
import com.universite.inscriptions.enums.StatutInscription;
import com.universite.inscriptions.exception.BusinessException;
import com.universite.inscriptions.exception.ResourceNotFoundException;
import com.universite.inscriptions.mapper.InscriptionMapper;
import com.universite.inscriptions.repository.EtudiantRepository;
import com.universite.inscriptions.repository.InscriptionRepository;
import com.universite.inscriptions.repository.ModuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InscriptionServiceTest {

    @Mock
    private InscriptionRepository inscriptionRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private InscriptionMapper inscriptionMapper;

    @InjectMocks
    private InscriptionService inscriptionService;

    private Etudiant etudiant;
    private Module module;
    private InscriptionRequest request;

    @BeforeEach
    void setUp() {
        etudiant = Etudiant.builder()
                .id(1L)
                .nom("Dupont")
                .prenom("Jean")
                .email("jean.dupont@univ.fr")
                .build();

        module = Module.builder()
                .id(10L)
                .code("JAVA101")
                .nom("Programmation Java")
                .nombrePlaces(30)
                .prerequis(new HashSet<>())
                .build();

        request = new InscriptionRequest();
        request.setEtudiantId(1L);
        request.setModuleId(10L);
    }

    @Test
    void inscrireEtudiant_Succes() {
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));
        when(moduleRepository.findById(10L)).thenReturn(Optional.of(module));
        when(inscriptionRepository.existsByEtudiantAndModule(etudiant, module)).thenReturn(false);
        when(inscriptionRepository.countByModuleAndStatut(module, StatutInscription.VALIDE)).thenReturn(5L);

        Inscription savedInscription = Inscription.builder()
                .id(100L)
                .etudiant(etudiant)
                .module(module)
                .statut(StatutInscription.VALIDE)
                .dateInscription(LocalDate.now())
                .build();

        when(inscriptionRepository.save(any(Inscription.class))).thenReturn(savedInscription);
        when(inscriptionMapper.toResponse(savedInscription)).thenReturn(InscriptionResponse.builder().build());

        InscriptionResponse response = inscriptionService.inscrireEtudiant(request);

        assertNotNull(response);
        verify(inscriptionRepository, times(1)).save(any(Inscription.class));
    }

    @Test
    void inscrireEtudiant_Echec_EtudiantIntrouvable() {
        when(etudiantRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            inscriptionService.inscrireEtudiant(request);
        });

        assertTrue(exception.getMessage().contains("Étudiant introuvable"));
        verify(inscriptionRepository, never()).save(any());
    }

    @Test
    void inscrireEtudiant_Echec_ModuleIntrouvable() {
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));
        when(moduleRepository.findById(10L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            inscriptionService.inscrireEtudiant(request);
        });

        assertTrue(exception.getMessage().contains("Module introuvable"));
        verify(inscriptionRepository, never()).save(any());
    }

    @Test
    void inscrireEtudiant_Echec_DejaInscrit() {
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));
        when(moduleRepository.findById(10L)).thenReturn(Optional.of(module));
        when(inscriptionRepository.existsByEtudiantAndModule(etudiant, module)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            inscriptionService.inscrireEtudiant(request);
        });

        assertTrue(exception.getMessage().contains("déjà inscrit"));
        verify(inscriptionRepository, never()).save(any());
    }

    @Test
    void inscrireEtudiant_Echec_CapaciteMaximaleAtteinte() {
        module.setNombrePlaces(2);
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));
        when(moduleRepository.findById(10L)).thenReturn(Optional.of(module));
        when(inscriptionRepository.existsByEtudiantAndModule(etudiant, module)).thenReturn(false);
        when(inscriptionRepository.countByModuleAndStatut(module, StatutInscription.VALIDE)).thenReturn(2L);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            inscriptionService.inscrireEtudiant(request);
        });

        assertTrue(exception.getMessage().contains("Capacité maximale atteinte"));
        verify(inscriptionRepository, never()).save(any());
    }

    @Test
    void inscrireEtudiant_Echec_PrerequisNonValide() {
        Module prerequis = Module.builder()
                .id(20L)
                .code("ALGO100")
                .nom("Algorithmique")
                .nombrePlaces(30)
                .build();
        module.setPrerequis(Set.of(prerequis));

        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));
        when(moduleRepository.findById(10L)).thenReturn(Optional.of(module));
        when(inscriptionRepository.existsByEtudiantAndModule(etudiant, module)).thenReturn(false);
        when(inscriptionRepository.countByModuleAndStatut(module, StatutInscription.VALIDE)).thenReturn(5L);
        when(inscriptionRepository.findByEtudiantId(1L)).thenReturn(Collections.emptyList());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            inscriptionService.inscrireEtudiant(request);
        });

        assertTrue(exception.getMessage().contains("Prérequis non validé"));
        assertTrue(exception.getMessage().contains("ALGO100"));
        verify(inscriptionRepository, never()).save(any());
    }

    @Test
    void inscrireEtudiant_Succes_PrerequisValides() {
        Module prerequis = Module.builder()
                .id(20L)
                .code("ALGO100")
                .nom("Algorithmique")
                .nombrePlaces(30)
                .build();
        module.setPrerequis(Set.of(prerequis));

        Inscription inscriptionPrerequis = Inscription.builder()
                .id(50L)
                .etudiant(etudiant)
                .module(prerequis)
                .statut(StatutInscription.VALIDE)
                .dateInscription(LocalDate.now())
                .build();

        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));
        when(moduleRepository.findById(10L)).thenReturn(Optional.of(module));
        when(inscriptionRepository.existsByEtudiantAndModule(etudiant, module)).thenReturn(false);
        when(inscriptionRepository.countByModuleAndStatut(module, StatutInscription.VALIDE)).thenReturn(5L);
        when(inscriptionRepository.findByEtudiantId(1L)).thenReturn(List.of(inscriptionPrerequis));

        Inscription savedInscription = Inscription.builder()
                .id(100L)
                .etudiant(etudiant)
                .module(module)
                .statut(StatutInscription.VALIDE)
                .dateInscription(LocalDate.now())
                .build();

        when(inscriptionRepository.save(any(Inscription.class))).thenReturn(savedInscription);
        when(inscriptionMapper.toResponse(savedInscription)).thenReturn(InscriptionResponse.builder().build());

        InscriptionResponse response = inscriptionService.inscrireEtudiant(request);

        assertNotNull(response);
        verify(inscriptionRepository, times(1)).save(any(Inscription.class));
    }
}
