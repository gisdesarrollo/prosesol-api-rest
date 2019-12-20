package com.prosesol.api.rest.models.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "pagos")
public class Pago implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_pago")
	private Long id;
	
	@Column(name = "rfc")
	@NotEmpty(message = "El rfc no debe quedar vacío")
	private String rfc;
	
	@Column(name = "referencia_bancaria")
	@NotEmpty(message = "Debe proporcionar una referencia bancaria")
	private String referenciaBancaria;
	
	@Column(name = "monto")
	@NotNull(message = "Debe proporcionar un monto")
	private Double monto;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Column(name = "fecha_pago")
	private Date fechaPago;

	@Column(name = "estatus")
	private String estatus;

	@Column(name = "tipo_transaccion")
	private String tipoTransaccion;

	@Column(name = "id_transaccion")
	private String idTransaccion;
		
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	public Date getFechaPago() {
		return fechaPago;
	}

	public void setFechaPago(Date fechaPago) {
		this.fechaPago = fechaPago;
	}

	public String getReferenciaBancaria() {
		return referenciaBancaria;
	}

	public void setReferenciaBancaria(String referenciaBancaria) {
		this.referenciaBancaria = referenciaBancaria;
	}

	public String getEstatus() {
		return estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}

	public String getTipoTransaccion() {
		return tipoTransaccion;
	}

	public void setTipoTransaccion(String tipoTransaccion) {
		this.tipoTransaccion = tipoTransaccion;
	}

	public String getIdTransaccion() {
		return idTransaccion;
	}

	public void setIdTransaccion(String idTransaccion) {
		this.idTransaccion = idTransaccion;
	}
}
