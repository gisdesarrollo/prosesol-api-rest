package com.prosesol.api.rest.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prosesol.api.rest.models.dao.IServicioDao;
import com.prosesol.api.rest.models.entity.Servicio;



@Service
public class ServicioServiceImpl implements IServicioService{
	
	
	@Autowired
	private IServicioDao servicioDao;

	@Override
	@Transactional(readOnly = true)
	public List<Servicio> findAll() {
		return (List<Servicio>) servicioDao.findAll();
	}
}
