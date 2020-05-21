package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.entity.Pregunta;

import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IPreguntaService{

    public List<Pregunta> findAll();

    public Pregunta findById(Long id);
}
