package com.prosesol.api.rest.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prosesol.api.rest.models.dao.IAfiliadoDao;
import com.prosesol.api.rest.models.entity.Afiliado;

@Service
public class AfiliadoServiceImpl implements IAfiliadoService {

	@Autowired
	private IAfiliadoDao afiliadoDao;

	@Override
	@Transactional(readOnly = true)
	public Afiliado findByRfc(String rfc) {
		return afiliadoDao.getAfiliadoByRfc(rfc);
	}


	@Override
	public List<Afiliado> findAll() {
		return (List<Afiliado>)afiliadoDao.findAll();
	}

}
