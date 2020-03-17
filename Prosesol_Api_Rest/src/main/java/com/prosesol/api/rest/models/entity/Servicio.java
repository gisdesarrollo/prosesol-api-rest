package com.prosesol.api.rest.models.entity;

import java.io.Serializable;
import java.util.List;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;


@Entity
@Table(name = "servicios")
public class Servicio implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_servicio", unique = true, nullable = false)
	private Long id;

	@NotEmpty
	@Column(name = "nombre")
	private String nombre;

	@Column(name = "notas")
	private String nota;

	@NumberFormat(style = Style.NUMBER, pattern = "#,###.##")
	@NotNull
	@Column(name = "inscripcion_titular")
	private Double inscripcionTitular;

	@NotNull
	@Column(name = "costo_titular")
	private Double costoTitular;

	@NotNull
	@Column(name = "inscripcion_beneficiario")
	private Double inscripcionBeneficiario;

	@NotNull
	@Column(name = "costo_beneficiario")
	private Double costoBeneficiario;

	@Column(name = "estatus")
	private Boolean estatus;

	@NotNull
	@Column(name = "tipo_privacidad")
	private Boolean tipoPrivacidad;

	@OneToMany(mappedBy = "servicio", fetch = FetchType.LAZY, cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<Afiliado> afiliado;

	

	public Servicio() {

	}

	public Servicio(String nombre, String nota, Double inscripcionTitular, Double costoTitular,
			Double inscripcionBeneficiario, Double costoBeneficiario, Boolean estatus, Boolean tipoPrivacidad ) {

		this.nombre = nombre;
		this.nota = nota;
		this.inscripcionTitular = inscripcionTitular;
		this.costoTitular = costoTitular;
		this.inscripcionBeneficiario = inscripcionBeneficiario;
		this.costoBeneficiario = costoBeneficiario;
		this.estatus = estatus;
		this.tipoPrivacidad = tipoPrivacidad;
		
		
		
		
	}

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

	public String getNota() {
		return nota;
	}

	public void setNota(String nota) {
		this.nota = nota;
	}

	public List<Afiliado> getAfiliado() {
		return afiliado;
	}

	public void setAfiliado(List<Afiliado> afiliado) {
		this.afiliado = afiliado;
	}

	public Boolean getEstatus() {
		return estatus;
	}

	public void setEstatus(Boolean estatus) {
		this.estatus = estatus;
	}

	public Double getInscripcionTitular() {
		return inscripcionTitular;
	}

	public void setInscripcionTitular(Double inscripcionTitular) {
		this.inscripcionTitular = inscripcionTitular;
	}

	public Double getCostoTitular() {
		return costoTitular;
	}

	public void setCostoTitular(Double costoTitular) {
		this.costoTitular = costoTitular;
	}

	public Double getInscripcionBeneficiario() {
		return inscripcionBeneficiario;
	}

	public void setInscripcionBeneficiario(Double inscripcionBeneficiario) {
		this.inscripcionBeneficiario = inscripcionBeneficiario;
	}

	public Double getCostoBeneficiario() {
		return costoBeneficiario;
	}

	public void setCostoBeneficiario(Double costoBeneficiario) {
		this.costoBeneficiario = costoBeneficiario;
	}

	

	public Boolean getTipoPrivacidad() {
		return tipoPrivacidad;
	}

	public void setTipoPrivacidad(Boolean tipoPrivacidad) {
		this.tipoPrivacidad = tipoPrivacidad;
	}


}
