package com.prosesol.api.rest.utils;

import com.prosesol.api.rest.controllers.exception.AfiliadoException;
import com.prosesol.api.rest.models.entity.Servicio;
import com.prosesol.api.rest.services.IGetTemplateByServicio;
import com.prosesol.api.rest.services.IServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Luis Enrique Morales Soriano
 */
@Service
public class GetTemplateByServicio implements IGetTemplateByServicio {

    @Autowired
    private IServicioService servicioService;

    @Override
    public Integer getTemplateByIdServicio(Long id)throws AfiliadoException {

        Servicio servicio = servicioService.findById(id);
        Integer servicioEmpresa = 0;

        if(servicio != null){
            if(servicio.getId() == 65 || servicio.getId() == 70){
                servicioEmpresa = 1;
            }else{
                servicioEmpresa = 2;
            }
        }else{
            throw new AfiliadoException("Servicio no encontrado");
        }

        return servicioEmpresa;
    }
}
