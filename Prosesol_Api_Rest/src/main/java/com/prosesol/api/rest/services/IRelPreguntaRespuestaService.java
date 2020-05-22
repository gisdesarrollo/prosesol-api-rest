package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.entity.custom.PreguntaRespuestaCustom;

import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IRelPreguntaRespuestaService {

    public List<PreguntaRespuestaCustom> getPreguntaRespuesta();
}
