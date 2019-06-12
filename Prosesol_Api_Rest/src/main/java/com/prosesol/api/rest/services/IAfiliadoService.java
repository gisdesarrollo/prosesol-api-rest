package com.prosesol.api.rest.services;

import java.util.List;

import com.prosesol.api.rest.models.entity.Afiliado;

public interface IAfiliadoService {

	public Afiliado findByRfc(String rfc);
	
	public List<Afiliado> findAll();
	
}
