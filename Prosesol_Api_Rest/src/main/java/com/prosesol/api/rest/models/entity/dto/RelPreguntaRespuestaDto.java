package com.prosesol.api.rest.models.entity.dto;

import com.prosesol.api.rest.models.rel.RelPreguntaRespuesta;

import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
public class RelPreguntaRespuestaDto {

    private List<RelPreguntaRespuesta> relPreguntaRespuestas;

    public List<RelPreguntaRespuesta> getRelPreguntaRespuestas() {
        return relPreguntaRespuestas;
    }

    public void setRelPreguntaRespuestas(List<RelPreguntaRespuesta> relPreguntaRespuestas) {
        this.relPreguntaRespuestas = relPreguntaRespuestas;
    }
}
