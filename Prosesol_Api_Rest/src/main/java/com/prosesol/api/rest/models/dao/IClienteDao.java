package com.prosesol.api.rest.models.dao;

import com.prosesol.api.rest.models.entity.Cliente;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IClienteDao extends CrudRepository<Cliente, Long> {
}
