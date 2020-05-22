package com.prosesol.api.rest.models.dao;

import com.prosesol.api.rest.models.entity.custom.PreguntaRespuestaCustom;
import com.prosesol.api.rest.models.rel.RelPreguntaRespuesta;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IRelPreguntaRespuestaDao extends CrudRepository<RelPreguntaRespuesta, Long> {

    @Query("select new com.prosesol.api.rest.models.entity.custom.PreguntaRespuestaCustom(" +
            "rel.pregunta.id, rel.respuesta.id, p.pregunta, r.respuesta) from RelPreguntaRespuesta rel " +
            "join rel.pregunta p " +
            "join rel.respuesta r " +
            "where rel.pregunta.id = p.id and rel.respuesta.id = r.id")
    public List<PreguntaRespuestaCustom> getPreguntaRespuesta();

}
