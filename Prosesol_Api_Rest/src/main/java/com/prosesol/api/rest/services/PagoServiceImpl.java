package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.dao.IPagoDao;
import com.prosesol.api.rest.models.entity.Pago;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class PagoServiceImpl implements IPagoService{

	@Autowired
	private IPagoDao pagoDao;
	
	@Override
	public Pago save(Pago pago) {
		return pagoDao.save(pago);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Pago> findAll() {
		return (List<Pago>)pagoDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Pago> getPagoByRfc(String rfc) {
		return pagoDao.getPagoByRfc(rfc);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		pagoDao.deleteById(id);		
	}

	@Override
	@Transactional
	public void actualizarEstatusPagoByIdTransaccion(String referencia, String estatus, String idTransaccion) {
		pagoDao.actualizarEstatusPagoByIdTransaccion(referencia, estatus, idTransaccion);
	}

	@Override
	@Transactional
	public Pago getRfcByIdTransaccion(String idTransaccion) {
		return pagoDao.getRfcByIdTransaccion(idTransaccion);
	}

}
