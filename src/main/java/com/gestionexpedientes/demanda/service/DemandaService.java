package com.gestionexpedientes.demanda.service;

import com.gestionexpedientes.demanda.dto.DemandaRequestDto;
import com.gestionexpedientes.demanda.dto.DemandaListDto;
import com.gestionexpedientes.counter.service.CounterService;
import com.gestionexpedientes.file.service.FileService;
import com.gestionexpedientes.global.dto.BpmnDto;
import com.gestionexpedientes.global.dto.PageDto;
import com.gestionexpedientes.demanda.entity.DemandaEntity;
import com.gestionexpedientes.demanda.repository.IDemandaRepository;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.WorkflowNotConfiguredException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.historial_demanda.service.HistorialDemandaService;
import com.gestionexpedientes.security.service.UserPrincipal;
import com.gestionexpedientes.subtipologia.entity.SubTipologiaEntity;
import com.gestionexpedientes.subtipologia.repository.ISubTipologiaRepository;
import com.gestionexpedientes.tipodemanda.TipoDemanda;
import com.gestionexpedientes.tipologia.entity.TipologiaEntity;
import com.gestionexpedientes.tipologia.repository.ITipologiaRepository;
import com.gestionexpedientes.user.entity.UserEntity;
import com.gestionexpedientes.user.repository.IUserRepository;
import com.gestionexpedientes.workflow.repository.IWorkflowRepository;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DemandaService {

    private static final String PASO_INICIAL = "Inicio";
    private static final String PASO_FINAL = "Finalizado";
    private static final int ESTADO_ELIMINADA = 0;
    private static final int ESTADO_RECEPTADA = 1;
    private static final int ESTADO_FINALIZADO = 7;
    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_EXPORT_SIZE = 5000;
    private static final Sort ORDEN_BANDEJA = Sort.by(Sort.Direction.DESC, "fechaCreacion").and(Sort.by(Sort.Direction.DESC, "_id"));
    private static final String WORKFLOW_NO_CONFIGURADO =
            "No hay un flujo de trabajo definido para esa combinación de Tipo de Demanda, Tipología y Subtipología.";
    private static final String DEMANDA_FINALIZADA =
            "El expediente está finalizado, ya no admite cambios de paso ni de estado.";

    private final IDemandaRepository demandaRepository;
    private final ITipologiaRepository tipologiaRepository;
    private final ISubTipologiaRepository subtipologiaRepository;
    private final IUserRepository userRepository;
    private final IWorkflowRepository workflowRepository;
    private final FileService fileService;
    private final DemandaAccessService demandaAccessService;
    private final HistorialDemandaService historialDemandaService;
    private final CounterService counterService;
    private final MongoTemplate mongoTemplate;

    public DemandaService(IDemandaRepository demandaRepository,
                          ITipologiaRepository tipologiaRepository,
                          ISubTipologiaRepository subtipologiaRepository,
                          IUserRepository userRepository,
                          IWorkflowRepository workflowRepository,
                          FileService fileService,
                          DemandaAccessService demandaAccessService,
                          HistorialDemandaService historialDemandaService,
                          CounterService counterService,
                          MongoTemplate mongoTemplate) {
        this.demandaRepository = demandaRepository;
        this.tipologiaRepository = tipologiaRepository;
        this.subtipologiaRepository = subtipologiaRepository;
        this.userRepository = userRepository;
        this.workflowRepository = workflowRepository;
        this.fileService = fileService;
        this.demandaAccessService = demandaAccessService;
        this.historialDemandaService = historialDemandaService;
        this.counterService = counterService;
        this.mongoTemplate = mongoTemplate;
    }

    public PageDto<DemandaListDto> getDatatable(String search, int pageIndex, int pageSize, UserPrincipal user) {
        int page = Math.max(1, pageIndex);
        int size = Math.min(Math.max(1, pageSize), MAX_PAGE_SIZE);

        Query query = Query.query(new Criteria().andOperator(alcance(user), busqueda(search)));
        long total = mongoTemplate.count(query, DemandaEntity.class);
        List<DemandaEntity> demandas =
                mongoTemplate.find(query.with(ORDEN_BANDEJA).skip((long) (page - 1) * size).limit(size), DemandaEntity.class);

        return new PageDto<>(toListDto(demandas), page, size, total);
    }

    public List<DemandaListDto> getExport(String search, UserPrincipal user) {
        Query query = Query.query(new Criteria().andOperator(alcance(user), busqueda(search)));
        return toListDto(mongoTemplate.find(query.with(ORDEN_BANDEJA).limit(MAX_EXPORT_SIZE), DemandaEntity.class));
    }

    public Map<Integer, Long> getResumen(UserPrincipal user) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(alcance(user)),
                Aggregation.group("estado").count().as("total"));

        return mongoTemplate.aggregate(aggregation, DemandaEntity.class, Document.class).getMappedResults().stream()
                .collect(Collectors.toMap(doc -> doc.getInteger("_id"), doc -> ((Number) doc.get("total")).longValue()));
    }

    private Criteria alcance(UserPrincipal user) {
        Criteria criteria = Criteria.where("estado").ne(ESTADO_ELIMINADA);
        if (user.isAdmin())
            return criteria;
        if (user.isAreaStaff())
            return criteria.orOperator(
                    Criteria.where("idsArea").is(user.getIdArea()),
                    Criteria.where("idUsuario").is(user.getId()));
        return criteria.and("idUsuario").is(user.getId());
    }

    private Criteria busqueda(String search) {
        if (search == null || search.isBlank())
            return new Criteria();

        Pattern pattern = Pattern.compile(Pattern.quote(search.trim()), Pattern.CASE_INSENSITIVE);
        List<Criteria> opciones = new ArrayList<>(List.of(
                Criteria.where("caratula").regex(pattern),
                Criteria.where("domicilio").regex(pattern),
                Criteria.where("informacionAdicional").regex(pattern),
                Criteria.where("paso").regex(pattern)));

        Query usuarios = Query.query(new Criteria().orOperator(
                Criteria.where("dni").regex(pattern),
                Criteria.where("name").regex(pattern),
                Criteria.where("lastname").regex(pattern)));
        usuarios.fields().include("_id");
        List<Integer> idsUsuario = mongoTemplate.find(usuarios, Document.class, "users").stream()
                .map(doc -> doc.getInteger("_id"))
                .collect(Collectors.toList());
        if (!idsUsuario.isEmpty())
            opciones.add(Criteria.where("idUsuario").in(idsUsuario));

        return new Criteria().orOperator(opciones.toArray(new Criteria[0]));
    }

    private List<DemandaListDto> toListDto(List<DemandaEntity> demandas) {
        Map<Integer, UserEntity> usuarios = porId(userRepository.findAllById(ids(demandas, DemandaEntity::getIdUsuario)), UserEntity::getId);
        Map<Integer, TipologiaEntity> tipologias = porId(tipologiaRepository.findAllById(ids(demandas, DemandaEntity::getIdTipologia)), TipologiaEntity::getId);
        Map<Integer, SubTipologiaEntity> subtipologias = porId(subtipologiaRepository.findAllById(ids(demandas, DemandaEntity::getIdSubtipologia)), SubTipologiaEntity::getId);

        return demandas.stream()
                .map(demanda -> mapToListDto(demanda,
                        usuarios.get(demanda.getIdUsuario()),
                        tipologias.get(demanda.getIdTipologia()),
                        subtipologias.get(demanda.getIdSubtipologia())))
                .collect(Collectors.toList());
    }

    private static List<Integer> ids(List<DemandaEntity> demandas, Function<DemandaEntity, Integer> campo) {
        return demandas.stream().map(campo).distinct().collect(Collectors.toList());
    }

    private static <T> Map<Integer, T> porId(Iterable<T> entidades, Function<T, Integer> id) {
        Map<Integer, T> map = new HashMap<>();
        entidades.forEach(entidad -> map.put(id.apply(entidad), entidad));
        return map;
    }

    private DemandaListDto mapToListDto(DemandaEntity demanda, UserEntity usuario, TipologiaEntity tipologia, SubTipologiaEntity subtipologia) {
        DemandaListDto dto = new DemandaListDto();

        dto.setId(demanda.getId());
        dto.setCaratula(demanda.getCaratula());
        dto.setInformacionAdicional(demanda.getInformacionAdicional());
        dto.setPaso(demanda.getPaso());
        dto.setEstado(demanda.getEstado());

        if (usuario != null) {
            dto.setDemandante(usuario.getName() + " " + usuario.getLastname());
            dto.setDni(usuario.getDni());
        }
        if (tipologia != null) {
            dto.setDescripcion(tipologia.getDescripcion());
            dto.setTipologia(tipologia.getNombre());
        }
        if (subtipologia != null)
            dto.setSubtipologia(subtipologia.getNombre());

        dto.setTipoDemanda(TipoDemanda.fromId(demanda.getIdTipoDemanda()).map(TipoDemanda::getCodigo).orElse(null));
        return dto;
    }

    public DemandaEntity getOne(int id, UserPrincipal user) throws ResourceNotFoundException {

        DemandaEntity demanda = demandaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado."));

        demandaAccessService.checkAccess(demanda, user);
        return demanda;
    }

    public List<DemandaEntity> getActives() {

        List<DemandaEntity> actives = demandaRepository.findByEstado(1);

        return actives;
    }

    public DemandaEntity save(DemandaRequestDto dto, UserPrincipal user) throws Exception {
        DemandaEntity demanda = demandaRepository.save(mapTipologiaFromDto(dto, user));

        historialDemandaService.registrar(demanda, user.getId(), null);
        return demanda;
    }

    public DemandaEntity update(int id, DemandaRequestDto dto, UserPrincipal user) throws ResourceNotFoundException, AttributeException {
        DemandaEntity demanda = getOne(id, user);

        demanda.setIdTipoDemanda(dto.getIdTipoDemanda());
        demanda.setIdTipologia(dto.getIdTipologia());
        demanda.setIdSubtipologia(dto.getIdSubtipologia());
        demanda.setDomicilio(dto.getDomicilio());
        demanda.setRutaImagen(dto.getRutaImagen());
        demanda.setInformacionAdicional(dto.getInformacionAdicional());

        if (demandaAccessService.canAdvance(demanda, user))
            avanzar(demanda, dto);

        DemandaEntity saved = demandaRepository.save(demanda);
        historialDemandaService.registrar(saved, user.getId(), dto.getObservaciones());
        return saved;
    }

    private void avanzar(DemandaEntity demanda, DemandaRequestDto dto) throws AttributeException {
        if (Objects.equals(demanda.getPaso(), dto.getPaso()) && demanda.getEstado() == dto.getEstado())
            return;

        if (estaFinalizada(demanda))
            throw new AttributeException(DEMANDA_FINALIZADA);

        demanda.setPaso(dto.getPaso());
        demanda.setEstado(dto.getEstado());
    }

    private static boolean estaFinalizada(DemandaEntity demanda) {
        return PASO_FINAL.equals(demanda.getPaso()) && demanda.getEstado() == ESTADO_FINALIZADO;
    }

    public DemandaEntity delete(int id, UserPrincipal user) throws ResourceNotFoundException {
        DemandaEntity demanda = getOne(id, user);

        demanda.setEstado(ESTADO_ELIMINADA);
        return demandaRepository.save(demanda);
    }

    private DemandaEntity mapTipologiaFromDto(DemandaRequestDto dto, UserPrincipal user) throws Exception {
        String urlBPMN = workflowRepository.findBpmnByIdTipoDemandaAndIdTipologiaAndIdSubtipologia(
                        dto.getIdTipoDemanda(), dto.getIdTipologia(), dto.getIdSubtipologia()
                ).map(BpmnDto::getBpmn)
                .orElseThrow(() -> new WorkflowNotConfiguredException(WORKFLOW_NO_CONFIGURADO));

        String caratula = setCaratula(dto);
        int id = counterService.nextId("demanda");
        Date fechaCreacion = new Date();

        String newNameBpmn = "demanda" + id + ".bpmn";
        String container = "demanda-bpmn";

        String bpmnDemanda = fileService.copyFileWithNewName(urlBPMN, container, newNameBpmn);
        List<Integer> idsArea = BpmnAreas.parse(fileService.readBlobUrl(urlBPMN));

        return new DemandaEntity(id, user.getId(), caratula, dto.getIdTipoDemanda(), dto.getIdTipologia(), dto.getIdSubtipologia(), dto.getDomicilio(), dto.getRutaImagen(), dto.getInformacionAdicional(), PASO_INICIAL, bpmnDemanda, idsArea, fechaCreacion, ESTADO_RECEPTADA);
    }

    private String setCaratula(DemandaRequestDto dto) throws AttributeException {
        String codigoTipologia = String.format("%03d", dto.getIdTipologia());

        String tipoDemanda = TipoDemanda.fromId(dto.getIdTipoDemanda())
                .map(TipoDemanda::getCodigo)
                .orElseThrow(() -> new AttributeException("El tipo de demanda no existe."));

        String anio = new SimpleDateFormat("yyyy").format(new Date());

        long secuencia = counterService.next(anio + "-" + codigoTipologia + "-" + tipoDemanda);

        return codigoTipologia + "-" + tipoDemanda + "-" + anio + "-" + String.format("%05d", secuencia);
    }
}
