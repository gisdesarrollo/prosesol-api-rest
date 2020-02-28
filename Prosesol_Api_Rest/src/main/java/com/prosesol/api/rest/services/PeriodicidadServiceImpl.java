package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.dao.IPeriodicidadDao;
import com.prosesol.api.rest.models.entity.Periodicidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Luis Enrique Morales Soriano
 */
@Service
public class PeriodicidadServiceImpl implements IPeriodicidadService{

    @Autowired
    private IPeriodicidadDao periodicidadDao;

    @Override
    @Transactional
    public Periodicidad getPeriodicidadByNombrePeriodo(String periodo) {
        return periodicidadDao.getPeriodicidadByNombrePeriodo(periodo);
    }
}
