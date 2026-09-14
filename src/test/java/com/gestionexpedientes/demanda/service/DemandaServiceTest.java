package com.gestionexpedientes.demanda.service;

import com.gestionexpedientes.counter.service.CounterService;
import com.gestionexpedientes.demanda.dto.DemandaRequestDto;
import com.gestionexpedientes.demanda.entity.DemandaEntity;
import com.gestionexpedientes.demanda.repository.IDemandaRepository;
import com.gestionexpedientes.file.service.FileService;
import com.gestionexpedientes.global.dto.BpmnDto;
import com.gestionexpedientes.historial_demanda.service.HistorialDemandaService;
import com.gestionexpedientes.security.service.UserPrincipal;
import com.gestionexpedientes.subtipologia.repository.ISubTipologiaRepository;
import com.gestionexpedientes.tipologia.repository.ITipologiaRepository;
import com.gestionexpedientes.user.repository.IUserRepository;
import com.gestionexpedientes.workflow.repository.IWorkflowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DemandaServiceTest {

    private static final String BPMN_WORKFLOW = "https://cuenta.blob.core.windows.net/workflow-bpmn/workflow-01.bpmn";
    private static final String XML_DOS_AREAS =
            "<bpmn:definitions><bpmn:laneSet id=\"LaneSet_1\">"
                    + "<bpmn:lane id=\"Lane_Vecino\" name=\"Vecino\" />"
                    + "<bpmn:lane id=\"Lane_4\" name=\"Servicios Publicos\" />"
                    + "<bpmn:lane id=\"Lane_5\" name=\"Obras Publicas\" />"
                    + "</bpmn:laneSet></bpmn:definitions>";

    @Mock private IDemandaRepository demandaRepository;
    @Mock private ITipologiaRepository tipologiaRepository;
    @Mock private ISubTipologiaRepository subtipologiaRepository;
    @Mock private IUserRepository userRepository;
    @Mock private IWorkflowRepository workflowRepository;
    @Mock private FileService fileService;
    @Mock private HistorialDemandaService historialDemandaService;
    @Mock private CounterService counterService;
    @Mock private MongoTemplate mongoTemplate;

    private DemandaService demandaService;

    @BeforeEach
    void setUp() {
        demandaService = new DemandaService(demandaRepository, tipologiaRepository, subtipologiaRepository, userRepository,
                workflowRepository, fileService, new DemandaAccessService(), historialDemandaService, counterService, mongoTemplate);
    }

    @Test
    @DisplayName("La caratula sale con formato NNN-TIPO-AAAA-SSSSS")
    void caratulaConFormatoCompleto() throws Exception {
        DemandaEntity guardada = guardar(demanda(1, 7, 20), 42L);

        String anio = new SimpleDateFormat("yyyy").format(new Date());
        assertThat(guardada.getCaratula()).isEqualTo("007-PT-" + anio + "-00042");
        assertThat(guardada.getCaratula()).matches("\\d{3}-[A-Z]{2}-\\d{4}-\\d{5}");
    }

    @Test
    @DisplayName("La secuencia avanza por terna anio-tipologia-tipo de demanda")
    void secuenciaPorTerna() throws Exception {
        guardar(demanda(4, 3, 25), 1L);

        String anio = new SimpleDateFormat("yyyy").format(new Date());
        verify(counterService).next(anio + "-003-RC");
    }

    @Test
    @DisplayName("La demanda guarda las areas del flujo para la bandeja de referentes")
    void guardaLasAreasDelFlujo() throws Exception {
        DemandaEntity guardada = guardar(demanda(1, 7, 20), 1L);

        assertThat(guardada.getIdsArea()).containsExactly(4, 5);
    }

    @Test
    @DisplayName("Un tipo de demanda inexistente no genera caratula")
    void tipoDemandaInexistente() {
        when(workflowRepository.findBpmnByIdTipoDemandaAndIdTipologiaAndIdSubtipologia(99, 7, 20))
                .thenReturn(Optional.of(new BpmnDto(BPMN_WORKFLOW)));

        assertThatThrownBy(() -> demandaService.save(demanda(99, 7, 20), usuario()))
                .hasMessage("El tipo de demanda no existe.");
    }

    private DemandaEntity guardar(DemandaRequestDto dto, long secuencia) throws Exception {
        when(workflowRepository.findBpmnByIdTipoDemandaAndIdTipologiaAndIdSubtipologia(
                dto.getIdTipoDemanda(), dto.getIdTipologia(), dto.getIdSubtipologia()))
                .thenReturn(Optional.of(new BpmnDto(BPMN_WORKFLOW)));
        when(counterService.next(anyString())).thenReturn(secuencia);
        when(counterService.nextId("demanda")).thenReturn(9);
        when(fileService.copyFileWithNewName(anyString(), anyString(), anyString())).thenReturn("https://cuenta.blob.core.windows.net/demanda-bpmn/demanda9.bpmn");
        when(fileService.readBlobUrl(BPMN_WORKFLOW)).thenReturn(XML_DOS_AREAS);
        when(demandaRepository.save(any(DemandaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        demandaService.save(dto, usuario());

        ArgumentCaptor<DemandaEntity> captor = ArgumentCaptor.forClass(DemandaEntity.class);
        verify(demandaRepository).save(captor.capture());
        return captor.getValue();
    }

    private static DemandaRequestDto demanda(int idTipoDemanda, int idTipologia, int idSubtipologia) {
        DemandaRequestDto dto = new DemandaRequestDto();
        dto.setIdTipoDemanda(idTipoDemanda);
        dto.setIdTipologia(idTipologia);
        dto.setIdSubtipologia(idSubtipologia);
        dto.setDomicilio("Calle Falsa 123");
        dto.setEstado(1);
        return dto;
    }

    private static UserPrincipal usuario() {
        return new UserPrincipal(3, null, "vecino@demo.test", "vecino@demo.test", "x",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
