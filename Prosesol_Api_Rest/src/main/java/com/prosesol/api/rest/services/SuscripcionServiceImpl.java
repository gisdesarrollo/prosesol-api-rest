package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.dao.ISuscripcionDao;
import com.prosesol.api.rest.models.entity.Suscripcion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
@Service
public class SuscripcionServiceImpl implements ISuscripcionService {

    @Autowired
    private ISuscripcionDao suscripcionDao;

    @Override
    @Transactional(readOnly = true)
    public List<Suscripcion> findAll() {
        return (List<Suscripcion>) suscripcionDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Suscripcion findById(Long id) {
        return suscripcionDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(Suscripcion suscripcion) {
        suscripcionDao.save(suscripcion);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        suscripcionDao.deleteById(id);
    }
}
