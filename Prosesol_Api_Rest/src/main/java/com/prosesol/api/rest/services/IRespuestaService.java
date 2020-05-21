package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.entity.Respuesta;

import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IRespuestaService{

    public List<Respuesta> findAll();

    public Respuesta findById(Long id);

}
