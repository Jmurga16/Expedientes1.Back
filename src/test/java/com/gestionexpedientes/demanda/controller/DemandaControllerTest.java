package com.gestionexpedientes.demanda.controller;

import com.gestionexpedientes.counter.service.CounterService;
import com.gestionexpedientes.demanda.entity.DemandaEntity;
import com.gestionexpedientes.demanda.repository.IDemandaRepository;
import com.gestionexpedientes.demanda.service.DemandaAccessService;
import com.gestionexpedientes.demanda.service.DemandaExcelService;
import com.gestionexpedientes.demanda.service.DemandaService;
import com.gestionexpedientes.file.service.FileService;
import com.gestionexpedientes.global.exceptions.GlobalException;
import com.gestionexpedientes.historial_demanda.service.HistorialDemandaService;
import com.gestionexpedientes.security.jwt.JwtProvider;
import com.gestionexpedientes.security.service.UserDetailsServiceImpl;
import com.gestionexpedientes.security.service.UserPrincipal;
import com.gestionexpedientes.subtipologia.repository.ISubTipologiaRepository;
import com.gestionexpedientes.tipologia.repository.ITipologiaRepository;
import com.gestionexpedientes.user.repository.IUserRepository;
import com.gestionexpedientes.workflow.repository.IWorkflowRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DemandaController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({DemandaService.class, DemandaAccessService.class, DemandaExcelService.class, GlobalException.class})
class DemandaControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private IDemandaRepository demandaRepository;
    @MockitoBean private ITipologiaRepository tipologiaRepository;
    @MockitoBean private ISubTipologiaRepository subtipologiaRepository;
    @MockitoBean private IUserRepository userRepository;
    @MockitoBean private IWorkflowRepository workflowRepository;
    @MockitoBean private FileService fileService;
    @MockitoBean private HistorialDemandaService historialDemandaService;
    @MockitoBean private CounterService counterService;
    @MockitoBean private MongoTemplate mongoTemplate;
    @MockitoBean private UserDetailsServiceImpl userDetailsService;
    @MockitoBean private JwtProvider jwtProvider;

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Un vecino no puede leer el expediente de otro")
    void unVecinoNoLeeElExpedienteDeOtro() throws Exception {
        when(demandaRepository.findById(7)).thenReturn(Optional.of(demandaDe(99)));
        autenticar(vecino(3));

        mockMvc.perform(get("/demanda/7"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Un vecino lee su propio expediente")
    void unVecinoLeeSuExpediente() throws Exception {
        when(demandaRepository.findById(7)).thenReturn(Optional.of(demandaDe(3)));
        autenticar(vecino(3));

        mockMvc.perform(get("/demanda/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caratula").value("007-PT-2026-00001"));
    }

    @Test
    @DisplayName("Un referente solo lee los expedientes cuyo circuito pasa por su area")
    void unReferenteLeeSoloLosDeSuArea() throws Exception {
        when(demandaRepository.findById(7)).thenReturn(Optional.of(demandaDe(99)));

        autenticar(referente(8, 5));
        mockMvc.perform(get("/demanda/7"))
                .andExpect(status().isOk());

        autenticar(referente(8, 11));
        mockMvc.perform(get("/demanda/7"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Un expediente inexistente responde 404")
    void expedienteInexistente() throws Exception {
        when(demandaRepository.findById(7)).thenReturn(Optional.empty());
        autenticar(vecino(3));

        mockMvc.perform(get("/demanda/7"))
                .andExpect(status().isNotFound());
    }

    private static DemandaEntity demandaDe(int idUsuario) {
        return new DemandaEntity(7, idUsuario, "007-PT-2026-00001", 1, 7, 20, "Calle Falsa 123", null,
                "sin datos", "Inicio", "https://cuenta.blob.core.windows.net/demanda-bpmn/demanda7.bpmn",
                List.of(4, 5), new Date(), 1);
    }

    private static UserPrincipal vecino(int id) {
        return new UserPrincipal(id, null, "vecino@demo.test", "vecino@demo.test", "x",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private static UserPrincipal referente(int id, int idArea) {
        return new UserPrincipal(id, idArea, "referente@demo.test", "referente@demo.test", "x",
                List.of(new SimpleGrantedAuthority("ROLE_AREA")));
    }

    private static void autenticar(UserPrincipal principal) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }
}
