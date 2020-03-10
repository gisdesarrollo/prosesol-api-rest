package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.dao.ILogARDao;
import com.prosesol.api.rest.models.entity.LogAR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Luis Enrique Morales Soriano
 */
@Service
public class LogARServiceImpl implements ILogARService{

    @Autowired
    private ILogARDao logARDao;

    @Override
    @Transactional(readOnly = true)
    public LogAR findById(Long id) {
        return logARDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(LogAR logAR) {
        logARDao.save(logAR);
    }
}
