package com.prosesol.api.rest.services;

import java.util.List;

import com.prosesol.api.rest.models.entity.Servicio;


public interface IServicioService {
	
	public List<Servicio> findAll();
	
	public Servicio findById(Long id);
}
