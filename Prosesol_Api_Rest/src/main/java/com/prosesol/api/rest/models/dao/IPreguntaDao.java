package com.prosesol.api.rest.models.dao;

import com.prosesol.api.rest.models.entity.Pregunta;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IPreguntaDao extends CrudRepository<Pregunta, Long> {
}
