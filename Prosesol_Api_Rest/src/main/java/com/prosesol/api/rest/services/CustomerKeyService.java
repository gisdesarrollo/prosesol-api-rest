package com.prosesol.api.rest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prosesol.api.rest.models.dao.ICustomerKeyDao;
import com.prosesol.api.rest.models.entity.CustomerKey;

@Service
public class CustomerKeyService implements ICustomerKeyService{

	@Autowired
	private ICustomerKeyDao customerKeyDao;
	
	@Override
	@Transactional(readOnly = true)
	public CustomerKey findCustomerKeybyCustomerKey(String key) {
		return customerKeyDao.findCustomerKeybyCustomerKey(key);
	}

	@Override
	@Transactional(readOnly = true)
	public CustomerKey findCustomerByCustomerName(String customer) {
		return customerKeyDao.findCustomerByCustomerName(customer);
	}

}
