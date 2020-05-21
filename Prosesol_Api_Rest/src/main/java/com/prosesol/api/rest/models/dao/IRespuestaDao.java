package com.prosesol.api.rest.models.dao;

import com.prosesol.api.rest.models.entity.Respuesta;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IRespuestaDao extends CrudRepository<Respuesta, Long> {
}
