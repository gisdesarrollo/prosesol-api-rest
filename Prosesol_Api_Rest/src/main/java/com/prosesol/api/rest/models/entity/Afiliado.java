package com.prosesol.api.rest.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "afiliados")
public class Afiliado implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_afiliado")
	private Long id;

	@NotEmpty(message = "El nombre no debe quedar vacío")
	@Column(name = "nombre")
	private String nombre;

	@NotEmpty(message = "El apellido paterno no debe quedar vacío")
	@Column(name = "apellido_paterno")
	private String apellidoPaterno;

	@NotEmpty(message = "El apellido materno no debe quedar vacío")
	@Column(name = "apellido_materno")
	private String apellidoMaterno;

	@Column(name = "rfc")
	@NotEmpty(message = "{TextField.rfc.empty.afiliado.message}")
	@Size(min = 12, max = 13, message = "{TextField.rfc.min.afiliado.message}")
	private String rfc;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_corte")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date fechaCorte;

	@Column(name = "saldo_acumulado")
	private Double saldoAcumulado;

	@Column(name = "saldo_corte")
	private Double saldoCorte;
	
	@Column(name = "is_beneficiario")
	private String isBeneficiario;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidoPaterno() {
		return apellidoPaterno;
	}

	public void setApellidoPaterno(String apellidoPaterno) {
		this.apellidoPaterno = apellidoPaterno;
	}

	public String getApellidoMaterno() {
		return apellidoMaterno;
	}

	public void setApellidoMaterno(String apellidoMaterno) {
		this.apellidoMaterno = apellidoMaterno;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	public Date getFechaCorte() {
		return fechaCorte;
	}

	public void setFechaCorte(Date fechaCorte) {
		this.fechaCorte = fechaCorte;
	}

	public Double getSaldoAcumulado() {
		return saldoAcumulado;
	}

	public void setSaldoAcumulado(Double saldoAcumulado) {
		this.saldoAcumulado = saldoAcumulado;
	}

	public Double getSaldoCorte() {
		return saldoCorte;
	}

	public void setSaldoCorte(Double saldoCorte) {
		this.saldoCorte = saldoCorte;
	}
	
	public String isBeneficiario() {
		return isBeneficiario;
	}

	public void setBeneficiario(String isBeneficiario) {
		this.isBeneficiario = isBeneficiario;
	}
	
	@Override
	public String toString() {
		
		final StringBuilder builder = new StringBuilder();
		
		builder.append("Id: [").append(id).append("]")
			   .append("Nombre Completo: [").append(nombre + " " + apellidoPaterno + " " + apellidoMaterno).append("]")
			   .append("RFC: [").append(rfc).append("]")
			   .append("Fecha de Corte: [").append(fechaCorte).append("]")
			   .append("Saldo Acumulado: [").append(saldoAcumulado).append("]")
			   .append("Saldo al Corte: [").append(saldoCorte).append("]");
		
		return builder.toString();
	}


	
}

