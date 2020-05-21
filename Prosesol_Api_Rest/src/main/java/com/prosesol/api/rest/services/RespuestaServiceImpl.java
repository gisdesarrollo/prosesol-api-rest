package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.dao.IRespuestaDao;
import com.prosesol.api.rest.models.entity.Respuesta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
@Service
public class RespuestaServiceImpl implements IRespuestaService{

    @Autowired
    private IRespuestaDao respuestaDao;

    @Override
    public List<Respuesta> findAll() {
        return (List)respuestaDao.findAll();
    }

    @Override
    public Respuesta findById(Long id) {
        return respuestaDao.findById(id).orElse(null);
    }
}
