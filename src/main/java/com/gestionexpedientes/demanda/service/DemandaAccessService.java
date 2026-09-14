package com.gestionexpedientes.demanda.service;

import com.gestionexpedientes.demanda.entity.DemandaEntity;
import com.gestionexpedientes.security.service.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class DemandaAccessService {

    public boolean canAccess(DemandaEntity demanda, UserPrincipal user) {
        if (user.isAdmin() || demanda.getIdUsuario() == user.getId())
            return true;
        return user.isAreaStaff() && user.getIdArea() != null
                && demanda.getIdsArea() != null && demanda.getIdsArea().contains(user.getIdArea());
    }

    public void checkAccess(DemandaEntity demanda, UserPrincipal user) {
        if (!canAccess(demanda, user))
            throw new AccessDeniedException("Sin acceso al expediente " + demanda.getId());
    }

    public boolean canAdvance(DemandaEntity demanda, UserPrincipal user) {
        return user.isAdmin() || (user.isAreaStaff() && canAccess(demanda, user));
    }
}
