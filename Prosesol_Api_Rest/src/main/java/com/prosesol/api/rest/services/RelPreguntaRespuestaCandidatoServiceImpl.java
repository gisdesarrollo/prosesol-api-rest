package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.dao.IRelPreguntaRespuestaCandidatoDao;
import com.prosesol.api.rest.models.entity.custom.PreguntaRespuestaCandidatoCustom;
import com.prosesol.api.rest.models.rel.RelPreguntaRespuestaCandidato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
@Service
public class RelPreguntaRespuestaCandidatoServiceImpl implements IRelPreguntaRespuestaCandidatoService {

    @Autowired
    private IRelPreguntaRespuestaCandidatoDao relPreguntaRespuestaCandidatoDao;

    @Override
    public void save(RelPreguntaRespuestaCandidato relPreguntaRespuestaCandidato) {
        relPreguntaRespuestaCandidatoDao.save(relPreguntaRespuestaCandidato);
    }

    @Override
    public List<RelPreguntaRespuestaCandidato> findAll() {
        return (List)relPreguntaRespuestaCandidatoDao.findAll();
    }

    @Override
    public RelPreguntaRespuestaCandidato findById(Long id) {
        return relPreguntaRespuestaCandidatoDao.findById(id).orElse(null);
    }

	@Override
	@Transactional(readOnly = true)
	public List<PreguntaRespuestaCandidatoCustom> getPreguntaAndRespuestaBycandidatoById(Long id) {
		return relPreguntaRespuestaCandidatoDao.getPreguntaAndRespuestaBycandidatoById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PreguntaRespuestaCandidatoCustom> getDatetimeByCandidato() {
		return (List<PreguntaRespuestaCandidatoCustom>)relPreguntaRespuestaCandidatoDao.getDatetimeByCandidato();
	}
}
