package com.prosesol.api.rest.auth.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prosesol.api.rest.auth.filter.SimpleGrantedAuthorityMixin;
import com.prosesol.api.rest.models.entity.CustomerKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTServiceImpl implements JWTService{
	
	public static final String SECRET = Base64Utils.encodeToString("0398f068-dec8-493a-a65f-10d94bd25c75".getBytes());
	
	public static final long EXPIRATION_DATE = 14000000L;
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";

	@Override
	public String create(Authentication auth) throws IOException{
		
		String customer = ((User)auth.getPrincipal()).getUsername();
	
		Collection<? extends GrantedAuthority> apiKey = auth.getAuthorities();
		
		Claims claims = Jwts.claims();
		claims.put("authorities", new ObjectMapper().writeValueAsString(apiKey));
			
		SecretKey secretKey = new SecretKeySpec(SECRET.getBytes(), SignatureAlgorithm.HS512.getJcaName());
			
		String token = Jwts.builder().setClaims(claims).setSubject(customer)
			       .signWith(secretKey)
			       .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_DATE)).compact();	
		
		return token;		
	}

	@Override
	public boolean validate(String token) {

		try {
			
			getClaims(token);
			
			return true;
		}catch(JwtException | IllegalArgumentException ex) {
			return false;
		}	
	}

	@Override
	public Claims getClaims(String token) {

		Claims claims = Jwts.parser().setSigningKey(SECRET.getBytes())
							.parseClaimsJws(resolve(token)).getBody();
		
		return claims;
	}

	@Override
	public String getUsername(String token) {

		return getClaims(token).getSubject();
		
	}

	@Override
	public Collection<? extends GrantedAuthority> getRoles(String token) throws IOException{

		Object roles = getClaims(token).get("authorities");
		
		Collection<? extends GrantedAuthority> authorities = Arrays
				.asList(new ObjectMapper().addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class)
						.readValue(roles.toString().getBytes(), SimpleGrantedAuthority[].class));
		
		
		return authorities;
	}

	@Override
	public String resolve(String token) {

		if(token != null && token.startsWith(TOKEN_PREFIX)) {
			return token.replace(TOKEN_PREFIX, "");
		}
		
		return null;
	}

}
