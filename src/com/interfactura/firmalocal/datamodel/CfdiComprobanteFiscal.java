/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.interfactura.firmalocal.datamodel;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Maximino Llovera
 */
public class CfdiComprobanteFiscal {

    private BigDecimal descuento;
    private String fecha;
    private String folio;
    private String formaPago;
    private String lugarExpedicion;
    private String metodoPago;
    private String moneda;
    private String noCertificado;
    private String sello;
    private String serie;
    private BigDecimal subTotal;
    private String tipoCambio;
    private String tipoDeComprobante;
    private BigDecimal total;
    private String version;
    private CfdiEmisor emisor;
    private CfdiReceptor receptor;
    private List<CfdiConcepto> conceptos;
    private CfdiImpuesto impuestos;
    private CfdiComplemento complemento;
    private CfdiAddendaSantanderV1 addenda;
    private String confirmacion;
    private CfdiRelacionado cfdiRelacionados;
    
    private String ivaCellValue;
    private String tipoAddendaCellValue;
    private String motivoDescCellValue;

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public String getLugarExpedicion() {
        return lugarExpedicion;
    }

    public void setLugarExpedicion(String lugarExpedicion) {
        this.lugarExpedicion = lugarExpedicion;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getNoCertificado() {
        return noCertificado;
    }

    public void setNoCertificado(String noCertificado) {
        this.noCertificado = noCertificado;
    }

    public String getSello() {
        return sello;
    }

    public void setSello(String sello) {
        this.sello = sello;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public String getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(String tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public String getTipoDeComprobante() {
        return tipoDeComprobante;
    }

    public void setTipoDeComprobante(String tipoDeComprobante) {
        this.tipoDeComprobante = tipoDeComprobante;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public CfdiEmisor getEmisor() {
        return emisor;
    }

    public void setEmisor(CfdiEmisor emisor) {
        this.emisor = emisor;
    }

    public CfdiReceptor getReceptor() {
        return receptor;
    }

    public void setReceptor(CfdiReceptor receptor) {
        this.receptor = receptor;
    }

    public List<CfdiConcepto> getConceptos() {
        return conceptos;
    }

    public void setConceptos(List<CfdiConcepto> conceptos) {
        this.conceptos = conceptos;
    }

    public CfdiImpuesto getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(CfdiImpuesto impuestos) {
        this.impuestos = impuestos;
    }

    public CfdiComplemento getComplemento() {
        return complemento;
    }

    public void setComplemento(CfdiComplemento complemento) {
        this.complemento = complemento;
    }

    public CfdiAddendaSantanderV1 getAddenda() {
        return addenda;
    }

    public void setAddenda(CfdiAddendaSantanderV1 addenda) {
        this.addenda = addenda;
    }

	public String getConfirmacion() {
		return confirmacion;
	}

	public void setConfirmacion(String confirmacion) {
		this.confirmacion = confirmacion;
	}

	public CfdiRelacionado getCfdiRelacionados() {
		return cfdiRelacionados;
	}

	public void setCfdiRelacionados(CfdiRelacionado cfdiRelacionados) {
		this.cfdiRelacionados = cfdiRelacionados;
	}

	public String getIvaCellValue() {
		return ivaCellValue;
	}

	public void setIvaCellValue(String ivaCellValue) {
		this.ivaCellValue = ivaCellValue;
	}

	public String getTipoAddendaCellValue() {
		return tipoAddendaCellValue;
	}

	public void setTipoAddendaCellValue(String tipoAddendaCellValue) {
		this.tipoAddendaCellValue = tipoAddendaCellValue;
	}

	public String getMotivoDescCellValue() {
		return motivoDescCellValue;
	}

	public void setMotivoDescCellValue(String motivoDescCellValue) {
		this.motivoDescCellValue = motivoDescCellValue;
	}

	@Override
	public String toString() {
		return "CfdiComprobanteFiscal [descuento=" + descuento + ", fecha="
				+ fecha + ", folio=" + folio + ", formaPago=" + formaPago
				+ ", lugarExpedicion=" + lugarExpedicion + ", metodoPago="
				+ metodoPago + ", moneda=" + moneda + ", noCertificado="
				+ noCertificado + ", sello=" + sello + ", serie=" + serie
				+ ", subTotal=" + subTotal + ", tipoCambio=" + tipoCambio
				+ ", tipoDeComprobante=" + tipoDeComprobante + ", total="
				+ total + ", version=" + version + ", emisor=" + emisor
				+ ", receptor=" + receptor + ", conceptos=" + conceptos
				+ ", impuestos=" + impuestos + ", complemento=" + complemento
				+ ", addenda=" + addenda + ", confirmacion=" + confirmacion
				+ ", cfdiRelacionados=" + cfdiRelacionados + "]";
	}
    
    

}