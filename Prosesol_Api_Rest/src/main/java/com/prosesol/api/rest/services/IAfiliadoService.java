package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.utils.Paises;

import java.util.List;

public interface IAfiliadoService {

	public Afiliado findByRfc(String rfc);
	
	public List<Afiliado> findAll();

	public void deleteById(Long id);

	public void save(Afiliado afiliado);

	public Afiliado findById(Long id);

	public List<String> getAllEstados();

	public List<Paises> getAllPaises();
	
	public void insertBeneficiarioUsingJpa(Afiliado beneficiario, Long id);
	
	public List<Afiliado> getBeneficiarioByIdByIsBeneficiario(Long idAfiliado);
	
	public String getAllClave();
}
