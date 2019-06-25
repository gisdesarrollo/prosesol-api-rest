package com.prosesol.api.rest.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.prosesol.api.rest.models.entity.CustomerKey;

public interface ICustomerKeyDao extends CrudRepository<CustomerKey, Long>{

	@Query("select c from CustomerKey c where c.apiKey = ?1")
	public CustomerKey findCustomerKeybyCustomerKey(@Param("apiKey") String key);
	
	@Query("select c from CustomerKey c where c.customer = ?1")
	public CustomerKey findCustomerByCustomerName(@Param("customer") String customer);
}
