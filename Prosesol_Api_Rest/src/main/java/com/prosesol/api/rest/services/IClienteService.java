package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Cliente;

import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IClienteService {

    public List<Cliente> findAll();

    public Cliente findById(Long id);

    public void save(Cliente cliente);

    public void deleteById(Long id);

    public Cliente getClienteByIdAfiliado(Afiliado afiliado);
}
