package com.prosesol.api.rest.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prosesol.api.rest.models.dao.IPagoDao;
import com.prosesol.api.rest.models.entity.Pago;

@Service
public class PagoServiceImpl implements IPagoService{

	@Autowired
	private IPagoDao pagoDao;
	
	@Override
	public Pago save(Pago pago) {
		return pagoDao.save(pago);
	}

	@Override
	public List<Pago> findAll() {
		return (List<Pago>)pagoDao.findAll();
	}

	@Override
	public List<Pago> getPagoByRfc(String rfc) {
		return pagoDao.getPagoByRfc(rfc);
	}

	@Override
	public void deleteById(Long id) {
		pagoDao.deleteById(id);		
	}

}
