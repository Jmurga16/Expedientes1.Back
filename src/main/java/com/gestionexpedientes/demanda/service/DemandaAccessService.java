package com.gestionexpedientes.demanda.service;

import com.gestionexpedientes.demanda.entity.DemandaEntity;
import com.gestionexpedientes.file.service.FileService;
import com.gestionexpedientes.security.service.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class DemandaAccessService {

    private static final Logger logger = LoggerFactory.getLogger(DemandaAccessService.class);
    private static final Pattern AREA_LANE = Pattern.compile("<(?:bpmn:)?lane\\s[^>]*id=\"Lane_(\\d+)\"");

    private final FileService fileService;

    private final Map<String, Set<Integer>> areasPorBpmn = new ConcurrentHashMap<>();

    public DemandaAccessService(FileService fileService) {
        this.fileService = fileService;
    }

    public boolean canAccess(DemandaEntity demanda, UserPrincipal user) {
        if (user.isAdmin() || demanda.getIdUsuario() == user.getId())
            return true;
        return user.isAreaStaff() && user.getIdArea() != null
                && areasDelFlujo(demanda.getUrlBpmn()).contains(user.getIdArea());
    }

    public void checkAccess(DemandaEntity demanda, UserPrincipal user) {
        if (!canAccess(demanda, user))
            throw new AccessDeniedException("Sin acceso al expediente " + demanda.getId());
    }

    public boolean canAdvance(DemandaEntity demanda, UserPrincipal user) {
        return user.isAdmin() || (user.isAreaStaff() && canAccess(demanda, user));
    }

    private Set<Integer> areasDelFlujo(String urlBpmn) {
        if (urlBpmn == null || urlBpmn.isBlank())
            return Collections.emptySet();
        return areasPorBpmn.computeIfAbsent(urlBpmn, this::leerAreas);
    }

    private Set<Integer> leerAreas(String urlBpmn) {
        try {
            Matcher matcher = AREA_LANE.matcher(fileService.readBlobUrl(urlBpmn));
            Set<Integer> areas = ConcurrentHashMap.newKeySet();
            while (matcher.find())
                areas.add(Integer.parseInt(matcher.group(1)));
            return areas;
        } catch (RuntimeException e) {
            // No se cachea: se reintenta en la proxima consulta.
            logger.warn("No se pudo leer el BPMN {}: {}", urlBpmn, e.getMessage());
            throw new AccessDeniedException("No se pudo verificar el acceso al expediente.");
        }
    }
}
