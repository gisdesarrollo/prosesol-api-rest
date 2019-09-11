package com.prosesol.api.rest.services;

import java.util.List;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.utils.Paises;

public interface IAfiliadoService {

	public Afiliado findByRfc(String rfc);

	public List<Afiliado> findAll();

	public void save(Afiliado afiliado);

	public List<String> getAllEstados();

	public List<Paises> getAllPaises();

	public Afiliado findById(Long id);
}
