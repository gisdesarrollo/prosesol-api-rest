package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.dao.IPreguntaDao;
import com.prosesol.api.rest.models.entity.Pregunta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
@Service
public class PreguntaServiceImpl implements IPreguntaService{

    @Autowired
    private IPreguntaDao preguntaDao;

    @Override
    public List<Pregunta> findAll() {
        return (List)preguntaDao.findAll();
    }

    @Override
    public Pregunta findById(Long id) {
        return preguntaDao.findById(id).orElse(null);
    }
}
