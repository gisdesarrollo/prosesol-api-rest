package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.dao.ICandidatoDao;
import com.prosesol.api.rest.models.entity.Candidato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CandidatoServiceImpl implements ICandidatoService {

    @Autowired
    private ICandidatoDao candidatoDao;

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
