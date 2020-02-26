package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.entity.Plan;
import com.prosesol.api.rest.models.entity.Servicio;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IPlanService {

    public Plan getPlanByIdServicio(Servicio servicio);
}
