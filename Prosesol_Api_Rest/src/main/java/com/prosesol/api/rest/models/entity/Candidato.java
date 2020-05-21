package com.prosesol.api.rest.models.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.prosesol.api.rest.models.rel.RelPreguntaRespuestaCandidato;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "candidatos")
public class Candidato implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_candidato")
	private Long id;

	@Column(name = "clave")
	private String clave;

	@NotEmpty(message = "Proporcione su nombre")
	@Column(name = "nombre")
	private String nombre;

	@NotEmpty(message = "Proporciones su apellido paterno")
	@Column(name = "apellido_paterno")
	private String apellidoPaterno;

	@NotEmpty(message = "Proporcione su apellido materno")
	@Column(name = "apellido_materno")
	private String apellidoMaterno;

	@NotNull(message = "Proporcione su fecha de nacimiento")
	@Column(name = "fecha_nacimiento")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date fechaNacimiento;

	@Column(name = "lugar_nacimiento")
	private String lugarNacimiento;

	@Column(name = "estado_civil", length = 11)
	private String estadoCivil;

	@Column(name = "ocupacion")
	private String ocupacion;

	@Column(name = "sexo", length = 10)
	private String sexo;

	@Column(name = "pais", length = 3)
	private String pais;

	@Column(name = "curp")
	private String curp;

	@Column(name = "nss")
	private Long nss;

	@Column(name = "rfc")
	@NotEmpty(message = "Proporcione su RFC")
	@Size(min = 13, max = 13)
	private String rfc;

	@Column(name = "telefono_fijo")
	private Long telefonoFijo;

	@Column(name = "telefono_movil")
	private Long telefonoMovil;

	@Column(name = "email")
	private String email;

	@Column(name = "direccion")
	private String direccion;

	@Column(name = "municipio")
	private String municipio;

	@Column(name = "codigo_postal")
	private Long codigoPostal;

	@Column(name = "entidad_federativa", length = 3)
	private String entidadFederativa;

	@Column(name = "infonavit")
	private String infonavit;

	@Column(name = "numero_infonavit")
	private Long numeroInfonavit;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_alta")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date fechaAlta;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_afiliacion")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date fechaAfiliacion;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_corte")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date fechaCorte;

	@Column(name = "saldo_acumulado")
	private Double saldoAcumulado;

	@Column(name = "saldo_corte")
	private Double saldoCorte;

	@NotNull
	@Column(name = "estatus", length = 1)
	private int estatus;

	@Column(name = "inscripcion")
	private Double inscripcion;

	@NotNull(message = "Seleccione el tipo de servicio")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_servicio")
	private Servicio servicio;

	@Column(name = "comentarios")
	private String comentarios;

	@Column(name = "is_beneficiario")
	private Boolean isBeneficiario;

	@Column(name = "is_inscripcion")
	private Boolean isInscripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_periodicidad")
	private Periodicidad periodicidad;

	@OneToMany(mappedBy = "candidato", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RelPreguntaRespuestaCandidato> relPreguntaRespuestaCandidato;

	@Transient
	private Integer corte;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
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

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getLugarNacimiento() {
		return lugarNacimiento;
	}

	public void setLugarNacimiento(String lugarNacimiento) {
		this.lugarNacimiento = lugarNacimiento;
	}

	public String getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public String getOcupacion() {
		return ocupacion;
	}

	public void setOcupacion(String ocupacion) {
		this.ocupacion = ocupacion;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getCurp() {
		return curp;
	}

	public void setCurp(String curp) {
		this.curp = curp;
	}

	public Long getNss() {
		return nss;
	}

	public void setNss(Long nss) {
		this.nss = nss;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	public Long getTelefonoFijo() {
		return telefonoFijo;
	}

	public void setTelefonoFijo(Long telefonoFijo) {
		this.telefonoFijo = telefonoFijo;
	}

	public Long getTelefonoMovil() {
		return telefonoMovil;
	}

	public void setTelefonoMovil(Long telefonoMovil) {
		this.telefonoMovil = telefonoMovil;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public Long getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(Long codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public String getEntidadFederativa() {
		return entidadFederativa;
	}

	public void setEntidadFederativa(String entidadFederativa) {
		this.entidadFederativa = entidadFederativa;
	}

	public String getInfonavit() {
		return infonavit;
	}

	public void setInfonavit(String infonavit) {
		this.infonavit = infonavit;
	}

	public Long getNumeroInfonavit() {
		return numeroInfonavit;
	}

	public void setNumeroInfonavit(Long numeroInfonavit) {
		this.numeroInfonavit = numeroInfonavit;
	}

	public Date getFechaAlta() {
		return fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	public Date getFechaAfiliacion() {
		return fechaAfiliacion;
	}

	public void setFechaAfiliacion(Date fechaAfiliacion) {
		this.fechaAfiliacion = fechaAfiliacion;
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

	public int getEstatus() {
		return estatus;
	}

	public void setEstatus(int estatus) {
		this.estatus = estatus;
	}

	public Double getInscripcion() {
		return inscripcion;
	}

	public void setInscripcion(Double inscripcion) {
		this.inscripcion = inscripcion;
	}

	public Servicio getServicio() {
		return servicio;
	}

	public void setServicio(Servicio servicio) {
		this.servicio = servicio;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

	public Boolean getIsBeneficiario() {
		return isBeneficiario;
	}

	public void setIsBeneficiario(Boolean isBeneficiario) {
		this.isBeneficiario = isBeneficiario;
	}

	public Boolean getIsInscripcion() {
		return isInscripcion;
	}

	public void setIsInscripcion(Boolean isInscripcion) {
		this.isInscripcion = isInscripcion;
	}

	public Periodicidad getPeriodicidad() {
		return periodicidad;
	}

	public void setPeriodicidad(Periodicidad periodicidad) {
		this.periodicidad = periodicidad;
	}

	public Integer getCorte() {
		return corte;
	}

	public void setCorte(Integer corte) {
		this.corte = corte;
	}

	public Boolean getBeneficiario() {
		return isBeneficiario;
	}

	public void setBeneficiario(Boolean beneficiario) {
		isBeneficiario = beneficiario;
	}

	public void setInscripcion(Boolean inscripcion) {
		isInscripcion = inscripcion;
	}

	public List<RelPreguntaRespuestaCandidato> getRelPreguntaRespuestaCandidato() {
		return relPreguntaRespuestaCandidato;
	}

	public void setRelPreguntaRespuestaCandidato(List<RelPreguntaRespuestaCandidato> relPreguntaRespuestaCandidato) {
		this.relPreguntaRespuestaCandidato = relPreguntaRespuestaCandidato;
	}

	@Override
	public String toString() {
		return "Candidato [id=" + id + ", clave=" + clave + ", nombre=" + nombre + ", apellidoPaterno="
				+ apellidoPaterno + ", apellidoMaterno=" + apellidoMaterno + ", fechaNacimiento=" + fechaNacimiento
				+ ", lugarNacimiento=" + lugarNacimiento + ", estadoCivil=" + estadoCivil + ", ocupacion=" + ocupacion
				+ ", sexo=" + sexo + ", pais=" + pais + ", curp=" + curp + ", nss=" + nss + ", rfc=" + rfc
				+ ", telefonoFijo=" + telefonoFijo + ", telefonoMovil=" + telefonoMovil + ", email=" + email
				+ ", direccion=" + direccion + ", municipio=" + municipio + ", codigoPostal=" + codigoPostal
				+ ", entidadFederativa=" + entidadFederativa + ", infonavit=" + infonavit + ", numeroInfonavit="
				+ numeroInfonavit + ", fechaAlta=" + fechaAlta + ", fechaAfiliacion=" + fechaAfiliacion
				+ ", fechaCorte=" + fechaCorte + ", saldoAcumulado=" + saldoAcumulado + ", saldoCorte=" + saldoCorte
				+ ", estatus=" + estatus + ", inscripcion=" + inscripcion + ", servicio=" + servicio + ", comentarios="
				+ comentarios + ", isBeneficiario=" + isBeneficiario + ", isInscripcion=" + isInscripcion
				+ ", periodicidad=" + periodicidad + ", corte=" + corte + "]";
	}

}
