package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.dao.IRelPreguntaRespuestaCandidatoDao;
import com.prosesol.api.rest.models.rel.RelPreguntaRespuestaCandidato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
