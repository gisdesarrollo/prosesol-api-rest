package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.dao.IRelPreguntaRespuestaDao;
import com.prosesol.api.rest.models.entity.custom.PreguntaRespuestaCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
@Service
public class RelPreguntaRespuestaServiceImpl implements IRelPreguntaRespuestaService{

    @Autowired
    private IRelPreguntaRespuestaDao relPreguntaRespuestaDao;

    @Override
    public List<PreguntaRespuestaCustom> getPreguntaRespuesta() {
        return relPreguntaRespuestaDao.getPreguntaRespuesta();
    }
}
