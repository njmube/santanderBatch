package com.interfactura.firmalocal.datamodel;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

//import com.interfactura.firmalocal.domain.entities.CodigoISO;
import com.interfactura.firmalocal.xml.util.Util;

public class Invoice_Masivo {
	private String reference;
	@NotBlank
	@Length(min = 1)
	private String date;
	@NotBlank
	private String codigoPostal;
	private String lugarExpedicion;
	@NotBlank
	@Length(min = 1)
	private String name;
	private String address;
	@NotBlank
	@Length(min = 1)
	private String rfc;
	private String rfcL;
	private String shipped;
	private List<ElementsInvoice> elements = new ArrayList<ElementsInvoice>();
	private String numberMotion;
	private String dateMotion;
	private String customs;
	@NotBlank
	@Length(min = 1)
	private String calle;
	@NotBlank
	@Length(min = 1)
	private String exterior;
	private String interior;
	private String municipio;
	@NotBlank
	@Length(min = 1)
	private String pais;
	@NotBlank
	@Length(min = 1)
	private String estado;
	private String referencia;
	private String localidad;
	@NotBlank
	@Length(min = 1)
	private String colonia;
	private String cadena;
	private String sello;
	private String noCertificado;
	private String noAprobacion;
	private String yearAprobacion;
	private String fechaHora;
	private int idFiscal;
	private double iva;
	private double porcentaje;
	private double subTotal;
	private double total;
	private double vat;
	private String quantityWriting;
	private String[] quantity;
	private String[] unitMeasure;
	private String[] tokens;
	private String[] description;
	private String[] unitPrice;
	private String[] amount;
	private String[] aplicaIva;
	private double exchange;
	private String folio;
	private String serie;
	private String customerCode;
	private String period;
	private String contractNumber;
	private String costCenter;
	private String tipoMoneda;
	private String tipoFormato;
	private String descriptionConcept;
	private String descriptionIVA;
	private String providerNumber;
	
	private String beneficiaryName;
	private String email;
	private String accountNumber;
	private String receivingInstitution;
	private String idExtranjero;
	private String esDonataria;
	
	//Tipos de Addenda
	private String purchaseOrder;
	
	private String tipoAddenda;
	
	private String codigoISO;
	
	private String codigoisomonedaList;
	
	private String codigoisomonedaLog;
	private String posicioncompraLog;
		
	private String codigoisomonedaFin;
	private String cuentacontableFin;
		
	private String codigoisomonedaArr;
	private String fechavencimientoArr;
	private String numerocontratoArr;
		
	// Campos v2.2
	private String regimenFiscal;
	private String metodoPago;
	private String formaPago;

    private String version;
    private String moneda;
    private String ivaDescription;
    private String tipoCambio;
    private String direccion;

    //private TimbreFiscal timbreFiscal;
    private Donataria donataria;
    private Date fechaRecepcion;
    private String numeroEmpleado;

    private ByteArrayOutputStream byteArrXMLSinAddenda = null;
    private StringBuilder sbError = null;
    private String fe_Id;
    private String fe_taxid;
    private String xmlRoute;
    
    private TimbreFiscal timbreFiscal;
    
    private String tipoDeComprobante;

    private String tipoOperacion;
    
    private boolean siAplicaIva = false;
    
    private String addendaList;
    
    private String addendaValues;
    
    private String checkOtrasAddendas; 
    
    //Atributos de descuento
    private double descuento;
    private String motivoDescuento;
    
    //V3.3
    private String usoCFDI;
	private String[] impuestoTraslado;
	private String[] tipoImpuestoTraslado;
	private String[] tipoFactorTraslado;
	private String[] tasaOCuotaTraslado;
	private String [] importesTraslado;
	private List<Traslados> traslados;  
	private String [] trasladosId;
	private String numRegIdTrib;
	private double totalImpuestoRetenido;
	private int decimals = 2;
	private String residenciaFiscal;
	private String[] claveProdServ;
	private String[] claveUnidad;
	private String rfcEmisor;
	private String numAutorizacion;
	private String cfdiRelacional;
	private String tipoRelacion;
	private String estadoEmisor;
	
	private ArrayList<ComplementoPago> pagos;
	private String foliosComplPago;
    
    public String getNumCtaPago() {
        return numCtaPago;
    }

    public void setNumCtaPago(String numCtaPago) {
        this.numCtaPago = numCtaPago;
    }

    private String numCtaPago;
	
	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
        if (this.address == null) {
            if (!Util.isNullEmpty(interior)) {
                interior = " NO. INTERIOR: " + interior;
            } else {
                interior = "";
            }

            if (!Util.isNullEmpty(exterior)) {
                exterior = " NO. EXTERIOR: " + exterior;
            } else {
                exterior = "";
            }

            if (Util.isNullEmpty(calle)) {
                calle = "";
            }

            if (!Util.isNullEmpty(colonia)) {
                colonia = "  COL. " + colonia;
            } else {
                colonia = "";
            }

            if (!Util.isNullEmpty(referencia)) {
                referencia = " " + referencia;
            } else {
                referencia = "";
            }

            if (!Util.isNullEmpty(codigoPostal)) {
                codigoPostal = "  C.P. " + codigoPostal;
            } else {
                codigoPostal = "";
            }

            if (!Util.isNullEmpty(localidad)) {
                localidad = " " + localidad + ",";
            } else {
                localidad = "";
            }

            if (!Util.isNullEmpty(municipio)) {
                municipio = " " + municipio;
            } else {
                municipio = "";
            }

            if (!Util.isNullEmpty(estado)) {
                estado = ", " + estado;
            } else {
                estado = "";
            }

            if (!Util.isNullEmpty(pais)) {
                pais = " " + pais;
            } else {
                pais = "";
            }

            setAddress(calle + exterior + interior
                    + colonia + localidad + referencia
                    + municipio + estado + pais + codigoPostal );
        }
        return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	public String getRfcL() {
		return rfcL;
	}

	public void setRfcL(String rfcL) {
		this.rfcL = rfcL;
	}

	public String getShipped() {
		return shipped;
	}

	public void setShipped(String shipped) {
		this.shipped = shipped;
	}

	public List<ElementsInvoice> getElements() {
		return elements;
	}

	public void setElements(List<ElementsInvoice> elements) {
		this.elements = elements;
	}

	public String getNumberMotion() {
		return numberMotion;
	}

	public void setNumberMotion(String numberMotion) {
		this.numberMotion = numberMotion;
	}

	public String getDateMotion() {
		return dateMotion;
	}

	public void setDateMotion(String dateMotion) {
		this.dateMotion = dateMotion;
	}

	public String getCustoms() {
		return customs;
	}

	public void setCustoms(String customs) {
		this.customs = customs;
	}

	public double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public double getVat() {
		return vat;
	}

	public void setVat(double vat) {
		this.vat = vat;
	}

	public String getQuantityWriting() {
		return quantityWriting;
	}

	public void setQuantityWriting(String quantityWriting) {
		this.quantityWriting = quantityWriting;
	}

	public double getExchange() {
		return exchange;
	}

	public void setExchange(double exchange) {
		this.exchange = exchange;
	}

	public String getCalle() {
		return calle;
	}

	public void setCalle(String calle) {
		this.calle = calle;
	}

	public String getExterior() {
		return exterior;
	}

	public void setExterior(String exterior) {
		this.exterior = exterior;
	}

	public String getInterior() {
		return interior;
	}

	public void setInterior(String interior) {
		this.interior = interior;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getColonia() {
		return colonia;
	}

	public void setColonia(String colonia) {
		this.colonia = colonia;
	}

	public int getIdFiscal() {
		return idFiscal;
	}

	public void setIdFiscal(int idFiscal) {
		this.idFiscal = idFiscal;
	}

	public double getIva() {
		return iva;
	}

	public void setIva(double iva) {
		this.iva = iva;
	}

	public double getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(double porcentaje) {
		this.porcentaje = porcentaje;
	}

	public String getLugarExpedicion() {
		return lugarExpedicion;
	}

	public void setLugarExpedicion(String lugarExpedicion) {
		this.lugarExpedicion = lugarExpedicion;
	}

	public String getCadena() {
		return cadena;
	}

	public void setCadena(String cadena) {
		this.cadena = cadena;
	}

	public String getSello() {
		return sello;
	}

	public void setSello(String sello) {
		this.sello = sello;
	}

	public String getNoCertificado() {
		return noCertificado;
	}

	public void setNoCertificado(String noCertificado) {
		this.noCertificado = noCertificado;
	}

	public String getNoAprobacion() {
		return noAprobacion;
	}

	public void setNoAprobacion(String noAprobacion) {
		this.noAprobacion = noAprobacion;
	}

	public String getFechaHora() {
		return fechaHora;
	}

	public void setFechaHora(String fechaHora) {
		this.fechaHora = fechaHora;
	}

	public String[] getQuantity() {
		return quantity;
	}

	public void setQuantity(String[] quantity) {
		this.quantity = quantity;
	}

	public String[] getUnitMeasure() {
		return unitMeasure;
	}

	public void setUnitMeasure(String[] unitMeasure) {
		this.unitMeasure = unitMeasure;
	}

	public String[] getDescription() {
		return description;
	}

	public void setDescription(String[] description) {
		this.description = description;
	}

	public String[] getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String[] unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String[] getAmount() {
		return amount;
	}

	public void setAmount(String[] amount) {
		this.amount = amount;
	}

	public String getFolio() {
		return folio;
	}

	public void setFolio(String folio) {
		this.folio = folio;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public String[] getTokens() {
		return tokens;
	}

	public void setTokens(String[] tokens) {
		this.tokens = tokens;
	}

	public String getTipoMoneda() {
		return tipoMoneda;
	}

	public void setTipoMoneda(String tipoMoneda) {
		this.tipoMoneda = tipoMoneda;
	}

	public String getYearAprobacion() {
		return yearAprobacion;
	}

	public void setYearAprobacion(String yearAprobacion) {
		this.yearAprobacion = yearAprobacion;
	}

	public String getTipoFormato() {
		return tipoFormato;
	}

	public void setTipoFormato(String tipoFormato) {
		this.tipoFormato = tipoFormato;
	}

	public String getDescriptionConcept() {
		return descriptionConcept;
	}

	public void setDescriptionConcept(String descriptionConcept) {
		this.descriptionConcept = descriptionConcept;
	}

	public String getProviderNumber() {
		return providerNumber;
	}

	public void setProviderNumber(String providerNumber) {
		this.providerNumber = providerNumber;
	}

	public String getPurchaseOrder() {
		return purchaseOrder;
	}

	public void setPurchaseOrder(String purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}
	
	public String getTipoAddenda() {
		return tipoAddenda;
	}

	public void setTipoAddenda(String tipoAddenda) {
		this.tipoAddenda = tipoAddenda;
	}

	public String getCodigoisomonedaLog() {
		return codigoisomonedaLog;
	}

	public void setCodigoisomonedaLog(String codigoisomonedaLog) {
		this.codigoisomonedaLog = codigoisomonedaLog;
	}

	public String getPosicioncompraLog() {
		return posicioncompraLog;
	}

	public void setPosicioncompraLog(String posicioncompraLog) {
		this.posicioncompraLog = posicioncompraLog;
	}

	public String getCodigoisomonedaFin() {
		return codigoisomonedaFin;
	}

	public void setCodigoisomonedaFin(String codigoisomonedaFin) {
		this.codigoisomonedaFin = codigoisomonedaFin;
	}

	public String getCuentacontableFin() {
		return cuentacontableFin;
	}

	public void setCuentacontableFin(String cuentacontableFin) {
		this.cuentacontableFin = cuentacontableFin;
	}
	
	public String getCodigoisomonedaArr() {
		return codigoisomonedaArr;
	}

	public void setCodigoisomonedaArr(String codigoisomonedaArr) {
		this.codigoisomonedaArr = codigoisomonedaArr;
	}

	public String getNumerocontratoArr() {
		return numerocontratoArr;
	}

	public void setNumerocontratoArr(String numerocontratoArr) {
		this.numerocontratoArr = numerocontratoArr;
	}

	public String getFechavencimientoArr() {
		return fechavencimientoArr;
	}

	public void setFechavencimientoArr(String fechavencimientoArr) {
		this.fechavencimientoArr = fechavencimientoArr;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getReceivingInstitution() {
		return receivingInstitution;
	}

	public void setReceivingInstitution(String receivingInstitution) {
		this.receivingInstitution = receivingInstitution;
	}

	public String getDescriptionIVA() {
		return descriptionIVA;
	}

	public void setDescriptionIVA(String descriptionIVA) {
		this.descriptionIVA = descriptionIVA;
	}

	public String getIdExtranjero() {
		return idExtranjero;
	}

	public void setIdExtranjero(String idExtranjero) {
		this.idExtranjero = idExtranjero;
	}

	public String getRegimenFiscal() {
		return regimenFiscal;
	}

	public void setRegimenFiscal(String regimenFiscal) {
		this.regimenFiscal = regimenFiscal;
	}

	public String getMetodoPago() {
		return metodoPago;
	}

	public void setMetodoPago(String metodoPago) {
		this.metodoPago = metodoPago;
	}

	public String getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getIvaDescription() {
        return ivaDescription;
    }

    public void setIvaDescription(String ivaDescription) {
        this.ivaDescription = ivaDescription;
    }

    public String getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(String tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Donataria getDonataria() {
        return donataria;
    }

    public void setDonataria(Donataria donataria) {
        this.donataria = donataria;
    }
/*
    public TimbreFiscal getTimbreFiscal() {
        return timbreFiscal;
    }

    public void setTimbreFiscal(TimbreFiscal timbreFiscal) {
        this.timbreFiscal = timbreFiscal;
    }
*/
	public String getEsDonataria() {
		return esDonataria;
	}

	public void setEsDonataria(String esDonataria) {
		this.esDonataria = esDonataria;
	}

	public Date getFechaRecepcion() {
		return fechaRecepcion;
	}

	public void setFechaRecepcion(Date fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}
	
	public String getFechaRecepcionString() {
		if( fechaRecepcion != null )
			return new SimpleDateFormat("dd/MM/yyyy").format(fechaRecepcion);
		else
			return new String("");
	}
	
	public void setFechaRecepcionString(String date){
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			this.fechaRecepcion = format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }
    
	

	public String getCodigoisomonedaList() {
		return codigoisomonedaList;
	}

	public void setCodigoisomonedaList(String codigoisomonedaList) {
		this.codigoisomonedaList = codigoisomonedaList;
	}

	public ByteArrayOutputStream getByteArrXMLSinAddenda() {
		return byteArrXMLSinAddenda;
	}

	public void setByteArrXMLSinAddenda(ByteArrayOutputStream byteArrXMLSinAddenda) {
		this.byteArrXMLSinAddenda = byteArrXMLSinAddenda;
	}

	public StringBuilder getSbError() {
		return sbError;
	}

	public void setSbError(StringBuilder sbError) {
		this.sbError = sbError;
	}

	public String getFe_Id() {
		return fe_Id;
	}

	public void setFe_Id(String fe_Id) {
		this.fe_Id = fe_Id;
	}

	public String getFe_taxid() {
		return fe_taxid;
	}

	public void setFe_taxid(String fe_taxid) {
		this.fe_taxid = fe_taxid;
	}

	public String getXmlRoute() {
		return xmlRoute;
	}

	public void setXmlRoute(String xmlRoute) {
		this.xmlRoute = xmlRoute;
	}

	public TimbreFiscal getTimbreFiscal() {
		return timbreFiscal;
	}

	public String getTipoDeComprobante() {
		return tipoDeComprobante;
	}

	public void setTipoDeComprobante(String tipoDeComprobante) {
		this.tipoDeComprobante = tipoDeComprobante;
	}

	public void setTimbreFiscal(TimbreFiscal timbreFiscal) {
		this.timbreFiscal = timbreFiscal;
	}

	public String getCodigoISO() {
		return codigoISO;
	}

	public void setCodigoISO(String codigoISO) {
		this.codigoISO = codigoISO;
	}

	public String getTipoOperacion() {
		return tipoOperacion;
	}

	public void setTipoOperacion(String tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}

	public boolean getSiAplicaIva() {
		return siAplicaIva;
	}

	public void setSiAplicaIva(boolean siAplicaIva) {
		this.siAplicaIva = siAplicaIva;
	}
	
	public double getDescuento() {
		return descuento;
	}

	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}
	
	public String getMotivoDescuento() {
		return motivoDescuento;
	}

	public void setMotivoDescuento(String motivoDescuento) {
		this.motivoDescuento = motivoDescuento;
	}

	public String[] getAplicaIva() {
		return aplicaIva;
	}

	public void setAplicaIva(String[] aplicaIva) {
		this.aplicaIva = aplicaIva;
	}

	public String getAddendaList() {
		return addendaList;
	}

	public void setAddendaList(String addendaList) {
		this.addendaList = addendaList;
	}

	public String getAddendaValues() {
		return addendaValues;
	}

	public void setAddendaValues(String addendaValues) {
		this.addendaValues = addendaValues;
	}

	public String getCheckOtrasAddendas() {
		return checkOtrasAddendas;
	}

	public void setCheckOtrasAddendas(String checkOtrasAddendas) {
		this.checkOtrasAddendas = checkOtrasAddendas;
	}

	public String getUsoCFDI() {
		return usoCFDI;
	}

	public void setUsoCFDI(String usoCFDI) {
		this.usoCFDI = usoCFDI;
	}

	public String[] getImpuestoTraslado() {
		return impuestoTraslado;
	}

	public void setImpuestoTraslado(String[] impuestoTraslado) {
		this.impuestoTraslado = impuestoTraslado;
	}

	public String[] getTipoImpuestoTraslado() {
		return tipoImpuestoTraslado;
	}

	public void setTipoImpuestoTraslado(String[] tipoImpuestoTraslado) {
		this.tipoImpuestoTraslado = tipoImpuestoTraslado;
	}

	public String[] getTipoFactorTraslado() {
		return tipoFactorTraslado;
	}

	public void setTipoFactorTraslado(String[] tipoFactorTraslado) {
		this.tipoFactorTraslado = tipoFactorTraslado;
	}

	public String[] getTasaOCuotaTraslado() {
		return tasaOCuotaTraslado;
	}

	public void setTasaOCuotaTraslado(String[] tasaOCuotaTraslado) {
		this.tasaOCuotaTraslado = tasaOCuotaTraslado;
	}

	public String[] getImportesTraslado() {
		return importesTraslado;
	}

	public void setImportesTraslado(String[] importesTraslado) {
		this.importesTraslado = importesTraslado;
	}

	public List<Traslados> getTraslados() {
		return traslados;
	}

	public void setTraslados(List<Traslados> traslados) {
		this.traslados = traslados;
	}

	public String[] getTrasladosId() {
		return trasladosId;
	}

	public void setTrasladosId(String[] trasladosId) {
		this.trasladosId = trasladosId;
	}

	public String getNumRegIdTrib() {
		return numRegIdTrib;
	}

	public void setNumRegIdTrib(String numRegIdTrib) {
		this.numRegIdTrib = numRegIdTrib;
	}

	public double getTotalImpuestoRetenido() {
		return totalImpuestoRetenido;
	}

	public void setTotalImpuestoRetenido(double totalImpuestoRetenido) {
		this.totalImpuestoRetenido = totalImpuestoRetenido;
	}

	public int getDecimals() {
		return decimals;
	}

	public void setDecimals(int decimals) {
		this.decimals = decimals;
	}

	public String getResidenciaFiscal() {
		return residenciaFiscal;
	}

	public void setResidenciaFiscal(String residenciaFiscal) {
		this.residenciaFiscal = residenciaFiscal;
	}

	public String[] getClaveProdServ() {
		return claveProdServ;
	}

	public void setClaveProdServ(String[] claveProdServ) {
		this.claveProdServ = claveProdServ;
	}

	public String[] getClaveUnidad() {
		return claveUnidad;
	}

	public void setClaveUnidad(String[] claveUnidad) {
		this.claveUnidad = claveUnidad;
	}

	public String getRfcEmisor() {
		return rfcEmisor;
	}

	public void setRfcEmisor(String rfcEmisor) {
		this.rfcEmisor = rfcEmisor;
	}

	public String getNumAutorizacion() {
		return numAutorizacion;
	}

	public void setNumAutorizacion(String numAutorizacion) {
		this.numAutorizacion = numAutorizacion;
	}

	public String getCfdiRelacional() {
		return cfdiRelacional;
	}

	public void setCfdiRelacional(String cfdiRelacional) {
		this.cfdiRelacional = cfdiRelacional;
	}

	public String getTipoRelacion() {
		return tipoRelacion;
	}

	public void setTipoRelacion(String tipoRelacion) {
		this.tipoRelacion = tipoRelacion;
	}

	public String getEstadoEmisor() {
		return estadoEmisor;
	}

	public void setEstadoEmisor(String estadoEmisor) {
		this.estadoEmisor = estadoEmisor;
	}

	public ArrayList<ComplementoPago> getPagos() {
		return pagos;
	}

	public void setPagos(ArrayList<ComplementoPago> pagos) {
		this.pagos = pagos;
	}

	public String getFoliosComplPago() {
		return foliosComplPago;
	}

	public void setFoliosComplPago(String foliosComplPago) {
		this.foliosComplPago = foliosComplPago;
	}
	
}
