package com.prosesol.api.rest.utils;

import com.prosesol.api.rest.controllers.exception.AfiliadoException;
import com.prosesol.api.rest.models.entity.Servicio;
import com.prosesol.api.rest.services.IGetTemplateByServicio;
import com.prosesol.api.rest.services.IServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Luis Enrique Morales Soriano
 */
@Service
public class GetTemplateByServicio implements IGetTemplateByServicio {

    @Autowired
    private IServicioService servicioService;

    @Value("${servicio.covid.id}")
    private Long servicioCovid;

    @Value("${servicio.familiar.total.id}")
    private Long servicioFamiliarTotal;

    @Value("${servicio.sin.fronteras.id}")
    private Long servicioSinFronteras;

    @Override
    public Integer getTemplateByIdServicio(Long id)throws AfiliadoException {

        Servicio servicio = servicioService.findById(id);
        Integer servicioEmpresa = 0;

        if(servicio != null){
            if(servicio.getId() == servicioFamiliarTotal || servicio.getId() == servicioSinFronteras ||
                    servicio.getId() == servicioCovid){
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
