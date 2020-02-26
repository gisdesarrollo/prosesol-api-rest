package com.prosesol.api.rest.models.dao;

import com.prosesol.api.rest.models.entity.Plan;
import com.prosesol.api.rest.models.entity.Servicio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IPlanDao extends CrudRepository<Plan, Long> {

    @Query("select p from Plan p where p.servicio = ?1")
    public Plan getPlanByIdServicio(Servicio servicio);
}
