package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.entity.Periodicidad;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IPeriodicidadService {

    public Periodicidad getPeriodicidadByNombrePeriodo(String periodo);

}
