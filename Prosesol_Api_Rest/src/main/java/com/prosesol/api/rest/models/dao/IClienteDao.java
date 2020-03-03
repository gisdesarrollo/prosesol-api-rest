package com.prosesol.api.rest.models.dao;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Cliente;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IClienteDao extends CrudRepository<Cliente, Long> {

    @Query("select c from Cliente c where c.afiliado = :afiliado")
    public Cliente getClienteByIdAfiliado(@Param(value = "afiliado")Afiliado afiliado);

}
