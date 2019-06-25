package com.prosesol.api.rest.services;

import com.prosesol.api.rest.models.entity.CustomerKey;

public interface ICustomerKeyService{

	public CustomerKey findCustomerKeybyCustomerKey(String key);
	
	public CustomerKey findCustomerByCustomerName(String customer);
	
}
