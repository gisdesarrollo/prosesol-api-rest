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
	@Transactional
	public void actualizarEstatusPagoByIdTransaccion(String referencia, String estatus, String idTransaccion) {
		pagoDao.actualizarEstatusPagoByIdTransaccion(referencia, estatus, idTransaccion);
	}

	@Override
	@Transactional
	public String getRfcByIdTransaccion(String idTransaccion) {
		return pagoDao.getRfcByIdTransaccion(idTransaccion);
	}


	@Override
	@Transactional
	public String getRfcCandidatoByIdTransaccion(String idTransaccion) {
		return pagoDao.getRfcCandidatoByIdTransaccion(idTransaccion);
	}

	@Override
	public Pago getPagosByIdTransaccion(String idTransaccion) {
		return pagoDao.getPagosByIdTransaccion(idTransaccion);
	}

}
