package com.prosesol.api.rest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prosesol.api.rest.models.dao.ICandidatoDao;
import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Candidato;
import com.prosesol.api.rest.repository.InsertCandidatoIntoAfiliadoRepository;

@Service
public class CandidatoServiceImpl implements ICandidatoService {

	@Autowired
	private ICandidatoDao candidatoDao; 
	
	@Autowired
	private InsertCandidatoIntoAfiliadoRepository candidatoRepository;
	
	@Override
	@Transactional
	public void save(Candidato candidato) {
		candidatoDao.save(candidato);
		
	}

	@Override
	@Transactional(readOnly = true)
	public Candidato finById(Long id) {
		return candidatoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Candidato findByRfc(String rfc) {
		return candidatoDao.getCandidatoByRfc(rfc);
	}

	@Override
	public void insertCandidatoIntoAfiliadoUsingJpa(Candidato candidato) {
		candidatoRepository.insertCandidatoIntoAfiliado(candidato);
		
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		candidatoDao.deleteById(id);
		
	}

	@Override
	@Transactional
	public void deleteRelCandidatoPagosById(Long idCandidato) {
		candidatoDao.deleteRelCandidatoPagosById(idCandidato);
		
	}

}
