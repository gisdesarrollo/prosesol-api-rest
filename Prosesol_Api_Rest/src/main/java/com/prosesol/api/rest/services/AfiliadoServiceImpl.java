package com.prosesol.api.rest.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prosesol.api.rest.models.dao.IAfiliadoDao;
import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.utils.Estados;
import com.prosesol.api.rest.utils.Paises;

@Service
public class AfiliadoServiceImpl implements IAfiliadoService {

	@Autowired
	private IAfiliadoDao afiliadoDao;

	@Override
	@Transactional(readOnly = true)
	public Afiliado findByRfc(String rfc) {
		return afiliadoDao.getAfiliadoByRfc(rfc);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Afiliado> findAll() {
		return (List<Afiliado>) afiliadoDao.findAll();
	}

	@Override
	@Transactional
	public void save(Afiliado afiliado) {
		afiliadoDao.save(afiliado);
	}

	@Override
	public List<String> getAllEstados() {

		Estados estados = new Estados();

		return estados.getEstados();
	}

	@Override
	public List<Paises> getAllPaises() {

		List<Paises> paises = new ArrayList<Paises>(Arrays.asList(Paises.values()));

		return paises;
	}

	@Override
	@Transactional(readOnly = true)
	public Afiliado findById(Long id) {
		return afiliadoDao.findById(id).orElse(null);
	}

}
