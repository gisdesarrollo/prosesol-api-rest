package com.prosesol.api.rest.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.prosesol.api.rest.models.entity.Servicio;

public interface IServicioDao extends CrudRepository<Servicio, Long> {

}
