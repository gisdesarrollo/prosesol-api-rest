package com.prosesol.api.rest.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prosesol.api.rest.models.dao.ICustomerKeyDao;
import com.prosesol.api.rest.models.entity.CustomerKey;

@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService{
	
	@Autowired
	private ICustomerKeyDao custmerKeyDao;

	private Logger logger = LoggerFactory.getLogger(JpaUserDetailsService.class);
	
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String customerName) throws UsernameNotFoundException {
		
		CustomerKey customer = custmerKeyDao.findCustomerByCustomerName(customerName);
		
		
		if(customer == null) {
			logger.info("Error en el login: no existe el customer '" + customerName + "' en el sistema");
			throw new UsernameNotFoundException("Customer: " + customerName + " no existe en el sistema");
		}
		
		System.out.println(customer.toString());
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		
		if(authorities.isEmpty()) {
			logger.error("Error en el login: Customer '" + customerName + "' no tiene roles asignados");
			throw new UsernameNotFoundException("Error en el Login: Customer' " + customerName + "' no tiene roles asignados");
		}
		
		return new User(customer.getCustomer(), customer.getApiKey(), customer.getEstatus(), true, true, true, authorities);
	}

}
