package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.dao.IAfiliadoDao;
import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.repository.BeneficiarioRepository;
import com.prosesol.api.rest.utils.Estados;
import com.prosesol.api.rest.utils.Paises;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AfiliadoServiceImpl implements IAfiliadoService {

	@Autowired
	private BeneficiarioRepository beneficiarioRepository;
	
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
	public void deleteById(Long id) {
		afiliadoDao.deleteById(id);
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
	
	@Override
	public void insertBeneficiarioUsingJpa(Afiliado beneficiario, Long id) {
		beneficiarioRepository.insertBeneficiario(beneficiario, id);
		
	}

	@Override
	public List<Afiliado> getBeneficiarioByIdByIsBeneficiario(Long idAfiliado) {
		return afiliadoDao.getBeneficiarioByIdByIsBeneficiario(idAfiliado);
	}
}
