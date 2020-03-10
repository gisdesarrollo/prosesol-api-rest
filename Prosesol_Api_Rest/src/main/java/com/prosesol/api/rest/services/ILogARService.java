package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.entity.LogAR;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface ILogARService {

    public LogAR findById(Long id);

    public void save (LogAR logAR);

}
