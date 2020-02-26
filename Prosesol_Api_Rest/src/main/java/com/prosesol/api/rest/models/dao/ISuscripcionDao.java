package com.prosesol.api.rest.models.dao;

import com.prosesol.api.rest.models.entity.Suscripcion;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface ISuscripcionDao extends CrudRepository<Suscripcion, Long> {
}
