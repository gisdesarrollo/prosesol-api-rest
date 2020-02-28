package com.prosesol.api.rest.models.dao;

import com.prosesol.api.rest.models.entity.Periodicidad;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IPeriodicidadDao extends CrudRepository<Periodicidad, Long> {

    @Query("select p from Periodicidad p where p.periodo like :periodo")
    public Periodicidad getPeriodicidadByNombrePeriodo(@Param("periodo") String periodo);

}
