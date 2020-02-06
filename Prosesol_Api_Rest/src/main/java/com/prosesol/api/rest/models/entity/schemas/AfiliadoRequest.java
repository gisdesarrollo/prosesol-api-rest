package com.prosesol.api.rest.models.entity.schemas;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.prosesol.api.rest.models.entity.Afiliado;
import com.prosesol.api.rest.models.entity.Servicio;


import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;


import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AfiliadoRequest {

	private Long id;

    private String clave;

    @NotNull(message = "Proporcione el nombre")
    private String nombre;

    @NotNull(message = "Proporciones el apellido paterno")
    private String apellidoPaterno;

    @NotNull(message = "Proporcione el apellido materno")
    private String apellidoMaterno;

    @NotNull(message = "Proporcione la fecha de nacimiento")
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", locale = "es-MX"
            , timezone = "America/Mexico_City")
    private Date fechaNacimiento;

    private String lugarNacimiento;

    private String estadoCivil;

    private String ocupacion;

    private String sexo;

    private String pais;

    private String curp;

    private Long nss;

    private String rfc;

    private Long telefonoFijo;

    private Long telefonoMovil;

    private String email;

    private String direccion;

    private String municipio;

    private Long codigoPostal;

    private String entidadFederativa;

    private String infonavit;

    private Long numeroInfonavit;

    private Date fechaAlta;

    private Date fechaAfiliacion;

    private Date fechaCorte;

    private Double saldoAcumulado;

    private Double saldoCorte;

    private int estatus;

    private Double inscripcion;
    
  
    @NotNull(message = "Proporcione su servicio")
    private Servicio servicio;

    private String comentarios;

    private Boolean isBeneficiario;

    private List<Afiliado> beneficiario;

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

    public void setIsBeneficiario(Boolean beneficiario) {
        isBeneficiario = beneficiario;
    }

    public List<Afiliado> getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(List<Afiliado> beneficiario) {
        this.beneficiario = beneficiario;
    }

    @Override
    public String toString() {
        return "AfiliadoRequest{" +
                "id=" + id +
                ", clave='" + clave + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidoPaterno='" + apellidoPaterno + '\'' +
                ", apellidoMaterno='" + apellidoMaterno + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", lugarNacimiento='" + lugarNacimiento + '\'' +
                ", estadoCivil='" + estadoCivil + '\'' +
                ", ocupacion='" + ocupacion + '\'' +
                ", sexo='" + sexo + '\'' +
                ", pais='" + pais + '\'' +
                ", curp='" + curp + '\'' +
                ", nss=" + nss +
                ", rfc='" + rfc + '\'' +
                ", telefonoFijo=" + telefonoFijo +
                ", telefonoMovil=" + telefonoMovil +
                ", email='" + email + '\'' +
                ", direccion='" + direccion + '\'' +
                ", municipio='" + municipio + '\'' +
                ", codigoPostal=" + codigoPostal +
                ", entidadFederativa='" + entidadFederativa + '\'' +
                ", infonavit='" + infonavit + '\'' +
                ", numeroInfonavit=" + numeroInfonavit +
                ", fechaAlta=" + fechaAlta +
                ", fechaAfiliacion=" + fechaAfiliacion +
                ", fechaCorte=" + fechaCorte +
                ", saldoAcumulado=" + saldoAcumulado +
                ", saldoCorte=" + saldoCorte +
                ", estatus=" + estatus +
                ", inscripcion=" + inscripcion +
                ", comentarios='" + comentarios + '\'' +
                ", isBeneficiario=" + isBeneficiario +
                ", beneficiario=" + beneficiario +
                '}';
    }
}
