package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.entity.Suscripcion;

import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface ISuscripcionService {

    public List<Suscripcion> findAll();

    public Suscripcion findById(Long id);

    public void save(Suscripcion suscripcion);

    public void deleteById(Long id);

}
