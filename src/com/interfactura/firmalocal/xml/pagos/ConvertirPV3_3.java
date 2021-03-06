package com.interfactura.firmalocal.xml.pagos;

import static com.interfactura.firmalocal.xml.util.Util.isNull;
import static com.interfactura.firmalocal.xml.util.Util.isNullEmpity;
import static com.interfactura.firmalocal.xml.util.Util.tags;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.interfactura.firmalocal.domain.entities.FiscalEntity;
import com.interfactura.firmalocal.xml.CatalogosDom;
import com.interfactura.firmalocal.xml.Properties;
import com.interfactura.firmalocal.xml.TagsXML;
import com.interfactura.firmalocal.xml.util.Util;
import com.interfactura.firmalocal.xml.util.NombreAplicativo;
import com.interfactura.firmalocal.xml.util.UtilCatalogos;

/**
 * Clase que se encargara de convertir las lineas a XML
 * 
 * @author jose luis
 * 
 */
@Component
public class ConvertirPV3_3 {

	private Logger logger = Logger.getLogger(ConvertirPV3_3.class);

	@Autowired
	private TagsXML tags;
	
	@Autowired
	private XMLPagos pagosTags;
	
	public  HashMap<String, String> cfdiRelacionados = new HashMap<String, String>();
	
	
	private Stack<String> pila;
	// private StringBuilder concat;
	private StringBuffer concat;
	private List<String> descriptionFormat;
	private String[] lineas;
	private String[] datos;
	@Autowired
	private Properties properties;

	private List<StringBuffer> lstMovimientosECB = new ArrayList<StringBuffer>();

	// AMDA Pruebas V3.3
	private String valImporteRetencion;
	private String valImporteTraslado;

	private static final String RFC_PATTERN = "[A-Z,Ñ,&]{3,4}[0-9]{2}[0-1][0-9][0-3][0-9][A-Z,0-9]?[A-Z,0-9]?[0-9,A-Z]?";
	private static final String RFC_PATTERN_TWO = "[A-Z&Ñ]{3,4}[0-9]{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])[A-Z0-9]{2}[0-9A]";
	private static final String UUID_PATTERN = "[a-f0-9A-F]{8}-[a-f0-9A-F]{4}-[a-f0-9A-F]{4}-[a-f0-9A-F]{4}-[a-f0-9A-F]{12}";
	private Pattern pattern;
	private Matcher matcher;
	
	
	
	private List<String> errFormat;
	
	
	
	
	
	public byte[] setComprobante(String linea, String fileNames, HashMap<String, String> hashApps,
			String numeroMalla) throws UnsupportedEncodingException {
		System.out.println("Iniciando parseo de lineas");
		datos = linea.split("\\|");
		
		clearFlag();
		
		tags.mapCatalogos = Util.readXLSFile(properties.getUrlArchivoCatalogs());
		
		pila = new Stack<String>();
		
		pagosTags.cfdiRelacionados = new HashMap<String, String>();
		
		
		if (datos.length >= 7) {
			
			concat = new StringBuffer();
			
			String serie = "";
			
			if (datos[1] != null  && !datos[1].trim().equals(""))
				pagosTags.version = datos[1];
			else {
				pagosTags.version = "3.3";
			}
			
			
			if (datos[2] != null  && !datos[2].trim().equals(""))
				pagosTags.total = "0";
			else {
				pagosTags.total = "0";
			}
			
			if (datos[3] != null  && !datos[3].trim().equals(""))
				pagosTags.tipoComprobante = datos[3].trim();
			else {
				pagosTags.tipoComprobante = "P";
			}
			
			if (datos[4] != null  && !datos[4].trim().equals(""))
				pagosTags.subTotal = "0";
			else { 
				pagosTags.subTotal = "0";
			}
			
			if (datos[5] != null  && !datos[5].trim().equals("")) {
				pagosTags.serie = datos[5];
				serie = " Serie=\"" + pagosTags.serie +"\"";
		 	} 
			
			if (datos[6] != null  && !datos[6].trim().equals(""))
				pagosTags.moneda = datos[6];
			else {
				pagosTags.moneda = "XXX";
			}
			
			
			String regex = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])T(([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9])";
			if ((datos[7] != null & !datos[7].trim().equals("")) && datos[7].matches(regex)) {
				pagosTags.fecha = datos[7].trim();
				pagosTags.numAprobacion = pagosTags.fecha.split("-")[0];
				pagosTags.yearAprobacion = pagosTags.numAprobacion;
			} else {
				pagosTags.fecha = ((datos[7] != null & !datos[7].trim().equals("")) ? "vacioErr" : "patronErr");
			}
			
			/*
			 * Atributos que se agregaran mas adelante
			 * Sello
			 * NoCertificado
			 * Confirmacion
			 * Folio
			 * Certificado
			 * lugar Expedicion
			 * 
			 * */
			
//			return concat.append("<cfdi:Comprobante "
//					+ "Version=\""+ +"\" "
//					+ "").toString().getBytes("UTF-8");
			
			return Util.conctatArguments("<cfdi:Comprobante ", concat.toString() ,
					" Version=\"", pagosTags.version, "\"",
					" Sello=\"", properties.getLabelSELLO().trim(), "\"", 
					" NoCertificado=\"", properties.getLblNO_CERTIFICADO().trim(), "\"", 
					" Folio=\"", properties.getLblFOLIOCFD().trim(), "\"",
					" Certificado=\"", properties.getLblCERTIFICADO().trim(), "\"",
					" LugarExpedicion=\"", properties.getLblLUGAREXPEDICION().trim(), "\" ",
					" Total=\"", pagosTags.total, "\"",
					" TipoDeComprobante=\"", pagosTags.tipoComprobante, "\"",
					" SubTotal=\"", pagosTags.subTotal, "\"",
					" Moneda=\"", pagosTags.moneda, "\"",
					" Fecha=\"", pagosTags.fecha, "\"",
					serie,
					" xmlns:cfdi=\"http://www.sat.gob.mx/cfd/3\" xmlns:pago10=\"http://www.sat.gob.mx/Pagos\" "
					+ " xsi:schemaLocation=\"http://www.sat.gob.mx/cfd/3 http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd  http://www.sat.gob.mx/ecb http://www.sat.gob.mx/sitio_internet/cfd/ecb/ecb.xsd http://www.sat.gob.mx/Pagos http://www.sat.gob.mx/sitio_internet/cfd/Pagos/pagos10.xsd\" "
					+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
					+ ">").toString().getBytes("UTF-8");
			
		} else {
			return errFormat(linea);
		}
		
		
	}

	
	public byte[] setRelacionado(String linea) throws UnsupportedEncodingException {
		
		datos = linea.split("\\|");
		
		if (datos.length >= 2) {
			concat = new StringBuffer();
			
			String tipoRelacion = "";
			String uuid = "";
			
			tipoRelacion = (datos[2] != null && !datos[2].trim().equals("") ? datos[2].trim() : "");
						if (datos[1] !=  null && !datos[1].trim().equals("")  ) {
				
				pattern = Pattern.compile(UUID_PATTERN);
				matcher = pattern.matcher(datos[1].trim());
			
				if (!matcher.matches()) {
						uuid = "patronErr";
				} else
					uuid = datos[1].trim().toUpperCase();
			} 
			
			if (!tipoRelacion.trim().equals("") && !uuid.trim().equals("")) {
			
				pagosTags.cfdiRelacionados.put(uuid, uuid);
				concat.append("\n<cfdi:CfdiRelacionados TipoRelacion=\"" + tipoRelacion + "\">");
				
				concat.append("\n<cfdi:CfdiRelacionado UUID=\"" + uuid + "\"/>\n</cfdi:CfdiRelacionados>");
				
				return concat.toString().getBytes("UTF-8");
			} else 
				return "".getBytes("UTF-8");
			
		} else {
			return errFormat(linea);
		}
			
		
		
		
	}
	
	
	public byte[] setEmisor(String linea ,HashMap<String, FiscalEntity> lstFiscal, HashMap campos22) throws UnsupportedEncodingException {
		
		datos = linea.split("\\|");
		
		if (datos.length >= 3) {
			
			concat = new StringBuffer();
			
			

			
			if (datos[1] == null || datos[1].trim().equals("")) {
				
				pagosTags.rfcEmisor = "vacioErr";
				
			} else {
				
				pattern = Pattern.compile(RFC_PATTERN);
				matcher = pattern.matcher(datos[1].trim());
				if (!matcher.matches()) {
					pattern = Pattern.compile(RFC_PATTERN_TWO);
					matcher = pattern.matcher(datos[1].trim());
					if(matcher.matches()){
						pagosTags.rfcEmisor = datos[1].trim().toUpperCase();
					}else{
						pagosTags.rfcEmisor = "patronErr";
					}
				} else
					pagosTags.rfcEmisor = datos[1].trim().toUpperCase();
				
			}
			
			if ( !pagosTags.rfcEmisor.contains("Err") ) {
				FiscalEntity fiscal = null;
				
				fiscal = lstFiscal.get(pagosTags.rfcEmisor);
				if (fiscal != null) {
					
					String valNombre = fiscal.getFiscalName().replaceAll("\\.", "");
					valNombre = valNombre.replaceAll("\\(", "");
					valNombre = valNombre.replaceAll("\\)", "");
					valNombre = valNombre.replace("/", "").toUpperCase();
					pagosTags.nombreEmisor = valNombre;
					pagosTags.regimenFiscal = "601";
					concat.append("\n<cfdi:Emisor Rfc=\""+pagosTags.rfcEmisor+"\""
							+ " Nombre=\""+pagosTags.nombreEmisor+"\""
							+ " RegimenFiscal=\""+pagosTags.regimenFiscal+"\" />");
					
					HashMap map1 = (HashMap) campos22.get(pagosTags.rfcEmisor);
					if (map1 != null) {
						String LugarExpedicion = (String) map1.get("codPostal");
						pagosTags.lugarExpedicion = LugarExpedicion;
					}
					
					tags.fis = fiscal;
					
				}
				
			} else {
				
				pagosTags.nombreEmisor = "vacioErr";
				pagosTags.regimenFiscal = "vacioErr";
				concat.append("\n<cfdi:Emisor Rfc=\""+pagosTags.rfcEmisor+"\""
						+ " Nombre=\""+pagosTags.nombreEmisor+"\""
						+ " RegimenFiscal=\""+pagosTags.regimenFiscal+"\" />");
			}
			
			
			return concat.toString().getBytes("UTF-8");
			
			
		} else {
			return errFormat(linea);
		} 
	}
	
	
	public byte[] setReceptor(String linea) throws UnsupportedEncodingException {
		
		datos = linea.split("\\|");
		
		if ( datos.length >= 7 ) {
			
			concat = new StringBuffer();
			
			
			String residenciaFiscalReceptor = "";
			String numRegIdTribReceptor = "";
			String nombreReceptor = "";
			
			if (datos[1] == null || datos[1].trim().equals("")) {
				
				pagosTags.rfcReceptor = "vacioErr";
				
			} else {
				
				pattern = Pattern.compile(RFC_PATTERN);
				matcher = pattern.matcher(datos[1].trim());
				if (!matcher.matches()) {
					pattern = Pattern.compile(RFC_PATTERN_TWO);
					matcher = pattern.matcher(datos[1].trim());
					if(matcher.matches()){
						pagosTags.rfcReceptor = datos[1].trim().toUpperCase();
					}else{
						if (tags.RECEPCION_RFC.equalsIgnoreCase("XEXX010101000")
								|| tags.RECEPCION_RFC.equalsIgnoreCase("XEXE010101000")
								|| tags.RECEPCION_RFC.equalsIgnoreCase("XEXX010101000")) {
							
							residenciaFiscalReceptor = " ResidenciaFiscal=\""+ datos[2] +"\"";
							
						} else 
							pagosTags.rfcReceptor = "XAXX010101000";
					}
				} else
					pagosTags.rfcReceptor = datos[1].trim().toUpperCase();
				
				
				

				if (datos[2] != null && !datos[2].trim().equals("")) {
					String valNombre = datos[2].trim().replaceAll("\\.", "");
					valNombre = valNombre.replaceAll("\\(", "");
					valNombre = valNombre.replaceAll("\\)", "");
					valNombre = valNombre.replace("/", "");
					// System.out.println("Receptor Reg: "+valNombre );
					nombreReceptor =  Util.convierte(valNombre).toUpperCase();
					pagosTags.nombreReceptor = nombreReceptor;
					nombreReceptor = " Nombre=\""+ pagosTags.nombreReceptor  +"\"" ;
				} else {
					pagosTags.nombreReceptor = "";
				}
				
				
				
				
				
				if (datos[3] != null && !datos[3].equals("")) {
					pagosTags.usoCFDI = datos[3].trim();
				} else {
					pagosTags.usoCFDI = "P01";
				}
				
				if (datos[4] != null && !datos[4].equals("")) {
					pagosTags.contrato = datos[4];
				} 
				
				if (datos[5] != null && !datos[5].equals("")) {
					pagosTags.numeroCliente = datos[5];
				}
				
				
				if (datos[6] != null && !datos[6].equals("")) {
					pagosTags.numRegIdTrib = datos[6];
					numRegIdTribReceptor = " NumRegIdTrib=\""+datos[6]+"\" ";
				} else {
					pagosTags.numRegIdTrib = "";
				}
				
				if (datos[7] != null && !datos[7].equals("")) {
					pagosTags.residenciaFiscal = datos[7];
					residenciaFiscalReceptor = " ResidenciaFiscal=\""+datos[7]+"\" ";
				} else {
					pagosTags.residenciaFiscal = "";
				}
				
					
			}
			
			return concat.append("\n<cfdi:Receptor Rfc=\"" + pagosTags.rfcReceptor + "\"" + 
									nombreReceptor+
									" UsoCFDI=\""+ pagosTags.usoCFDI +"\"" +									
									numRegIdTribReceptor +
									residenciaFiscalReceptor+ "/>").toString().getBytes("UTF-8");
			
		}else {
			return errFormat(linea);
		}
	}
	
	public byte[] setConcepto(String linea) throws UnsupportedEncodingException {
		datos = linea.split("\\|");
		
		if ( datos.length >= 6 ) {
			
			concat = new StringBuffer();
			
			if (datos[1] != null && !datos[1].trim().equals("")) {
				pagosTags.valorUnitario = datos[1].trim(); 
			} else {
				pagosTags.valorUnitario = "0";
			}
			
			if (datos[2] != null && !datos[2].trim().equals("") || datos[2].trim().equals("0")) {
				pagosTags.importe = datos[2].trim(); 
			} else {
				pagosTags.importe = "0";
			}
			
			if (datos[3] != null && !datos[3].trim().equals("") && !datos[3].trim().equals("PAGO")) {
				pagosTags.descripcion = "Pago"; 
			} else {
				pagosTags.descripcion = "Pago";
			}
			
			if (datos[4] != null && !datos[4].trim().equals("")) {
				pagosTags.claveUnidad = datos[4].trim(); 
			} else {
				pagosTags.claveUnidad = "ACT";
			}
			
			if (datos[5] != null && !datos[5].trim().equals("")) {
				pagosTags.claveProdServ = datos[5].trim(); 
			} else {
				pagosTags.claveProdServ = "84111506";
			}
			if (datos[6] != null && !datos[6].trim().equals("")) {
				pagosTags.cantidad = datos[6].trim(); 
			} else {
				pagosTags.cantidad = "1";
			}
			
			return concat.append("\n<cfdi:Conceptos>\n<cfdi:Concepto"
								+ " ValorUnitario=\""+ pagosTags.valorUnitario +"\""
								+ " Importe=\""+ pagosTags.importe +"\""
								+ " Descripcion=\""+ pagosTags.descripcion +"\""
								+ " ClaveUnidad=\""+ pagosTags.claveUnidad +"\""
								+ " ClaveProdServ=\""+ pagosTags.claveProdServ +"\""
								+ " Cantidad=\""+ pagosTags.cantidad +"\""
								+ " />\n</cfdi:Conceptos>").toString().getBytes("UTF-8");
			
		}else {
			return errFormat(linea);
		}
	}
	
	public byte[] setPago(String linea) throws UnsupportedEncodingException {
		
		datos = linea.split("\\|");
		
		if ( datos.length >= 17 ) {
			
			concat = new StringBuffer();
			
			Pago pago = new Pago();
			String rfcEmisorCta = "";
			String rfcEmisorCtaBen = "";
			String nomBancoOrd = "";
			String ctaOrd = "";
			String cntaBene = "";
			String tipoCadPago = "";
			String certPago = "";
			String cadPago = "";
			String selloPago = "";
			
			
			if (datos[1] != null && !datos[1].trim().equals("")) {
				
				pattern = Pattern.compile(UUID_PATTERN);
				matcher = pattern.matcher(datos[1].trim().toUpperCase());
			
				if (!matcher.matches()) {
					pago.setIdDocumento("patronErr");
				} else
					pago.setIdDocumento(datos[1].trim().toUpperCase());
			} else {
				pago.setIdDocumento("vacioErr");
			}
			
			if (datos[2] != null && !datos[2].trim().equals("")) {
				pago.setVersion("1.0");
			} else
				pago.setVersion("1.0");
			
			String numOper = "";
			
			if (datos[3] != null && !datos[3].trim().equals("")) {
				pago.setNumOperacion(datos[3].trim());
				numOper = " NumOperacion=\""+ pago.getNumOperacion() +"\"";
			} else
				pago.setNumOperacion("");
			
			
			String tipoMoneda = "";
			if (datos[5] != null && !datos[5].trim().equals("")) {
				pago.setMonedaP(datos[5].trim());
				tipoMoneda = datos[5].trim();
			} else
				pago.setMonedaP("vacioErr");
			
			
			
			int decimalesMoneda = UtilCatalogos.findDecimalesMoneda(tags.mapCatalogos, tipoMoneda);
			
			if (datos[4] != null && !datos[4].trim().equals("")) {
				if (UtilCatalogos.decimalesValidationMsj(datos[4].trim(), decimalesMoneda)) {
					pago.setMonto(datos[4].trim());
				} else {
					pago.setMonto("decimalesErr");
				}
				
			} else
				pago.setMonto("vacioErr");
			
			String tipoCambio = "";
			if (datos[6] != null && !datos[6].trim().equals("") && !tipoMoneda.equalsIgnoreCase("MXN")) {
					String tipocambioVal = UtilCatalogos.findTipoCambioByMoneda(tags.mapCatalogos,tipoMoneda);
					if (!tipocambioVal.equalsIgnoreCase("vacio")) {
						pago.setTipoCambioP(tipocambioVal);
					} else
						pago.setTipoCambioP("vacioErr");
					
					tipoCambio = " TipoCambioP=\""+ pago.getTipoCambioP() +"\"";
			} else {
				pago.setTipoCambioP("");
			}
			
			
			if (datos[7] != null && !datos[7].trim().equals("")) {
				pago.setFormaDePagoP(datos[7].trim());
			} else
				pago.setFormaDePagoP("vacioErr");
			
			
			Pattern p = Pattern.compile(
					"[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])T(([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9])");
			Matcher m = p.matcher(datos[8].trim());
			if (datos[8] != null && !datos[8].trim().equals("") && m.find()) {
					pago.setFechaPago(datos[8].trim());				
			} else
				pago.setFechaPago((datos[8] != null || !datos[8].trim().equals("") ? "vacioErr" : "patronErr"));
			
			
			
			if (datos[9] != null && !datos[9].trim().equals("")) {
				pago.setRfcEmisorCtaOrd(datos[9].trim());
				rfcEmisorCta = " RfcEmisorCtaOrd=\""+ pago.getRfcEmisorCtaOrd() +"\"";
			} else
				pago.setRfcEmisorCtaOrd("");
			
			if (datos[10] != null && !datos[10].trim().equals("")) {
				pago.setNomBancoOrdExt(datos[10].trim() );
				nomBancoOrd = " NomBancoOrdExt=\""+ pago.getNomBancoOrdExt() +"\"";
			} else
				pago.setNomBancoOrdExt("");
			
			if (datos[11] != null && !datos[11].trim().equals("")) {
				pago.setCtaOrdenante(datos[11].trim());
				ctaOrd =" CtaOrdenante=\""+ pago.getCtaOrdenante() +"\"";
			} else
				pago.setCtaOrdenante("");
			
			if (datos[12] != null && !datos[12].trim().equals("")) {
				pago.setRfcEmisorCtaBen(datos[12].trim());
				rfcEmisorCtaBen = " RfcEmisorCtaBen=\""+ pago.getRfcEmisorCtaBen() +"\"";
			} else 
				pago.setRfcEmisorCtaBen("");
			
			if (datos[13] != null && !datos[13].trim().equals("")) {
				pago.setCtaBeneficiario(datos[13].trim());
				cntaBene = " CtaBeneficiario=\""+ pago.getCtaBeneficiario() +"\"";
			} else
				pago.setCtaBeneficiario("");
		
			
			
			if (datos[14] != null && !datos[14].trim().equals("")) {
				pago.setTipoCadPago(datos[14].trim());
				tipoCadPago = " TipoCadPago=\""+ datos[14] +"\"";
			} else
				pago.setTipoCadPago("");
			
			if (datos[15] != null && !datos[15].trim().equals("")) {
				pago.setCertPago(datos[15].trim());
				certPago = " CertPago=\""+ datos[15] +"\"";
			} else
				pago.setCertPago("");
			
			if (datos[16] != null && !datos[16].trim().equals("")) {
				pago.setCadPago(datos[16].trim());
				cadPago = " CadPago=\""+ datos[16] +"\"";
			} else
				pago.setCadPago("");
			
			
			if (datos[17] != null && !datos[17].trim().equals("")) {
				pago.setSelloPago(datos[17].trim());
				selloPago = " SelloPago=\""+ datos[17] +"\"";
			} else
				pago.setSelloPago("");
			
			
			pagosTags.pagos.add(pago);
			
			return concat.append("\n<pago10:Pago"
							+ " FechaPago=\""+ pago.getFechaPago() +"\""
							+ " FormaDePagoP=\""+ pago.getFormaDePagoP()+"\""
							+ " MonedaP=\""+ pago.getMonedaP() +"\""
							+ tipoCambio
							+ " Monto=\""+ pago.getMonto() +"\""
							+ numOper
							+ rfcEmisorCta
							+ nomBancoOrd
							+ ctaOrd
							+ rfcEmisorCtaBen
							+ cntaBene
							+ tipoCadPago + certPago + cadPago + selloPago
							+ " >").toString().getBytes("UTF-8");
			
		}else {
			return errFormat(linea);
		}
	}
	
	public byte[] setDocRelacionado(String linea) throws UnsupportedEncodingException {
		
		datos = linea.split("\\|");
		
		if ( datos.length >= 10 ) {
			
			concat = new StringBuffer();
			
			Documento doc = new Documento();
			
			String regExUUID = "[a-f0-9A-F]{8}-[a-f0-9A-F]{4}-[a-f0-9A-F]{4}-[a-f0-9A-F]{4}-[a-f0-9A-F]{12}";
			
			String folio = "";
			String serie = "";
			String tipoCambio = "";
			String numParcialidad = "";
			String impSaldoAnt = "";
			String impPagado = "";
			String impInsoluto = "";
			
			if (datos[1] != null && !datos[1].trim().equals("")) {
				
				pattern = Pattern.compile(UUID_PATTERN);
				matcher = pattern.matcher(datos[1].trim().toUpperCase());
			
				if (!matcher.matches()) {
					doc.setIdDocumento("patronErr");
				} else
					doc.setIdDocumento(datos[1].trim().toUpperCase());
			} else {
				doc.setIdDocumento("vacioErr");
			}
			
			
			if (datos[2] != null && !datos[2].trim().equals("")) {
				doc.setFolio(datos[2]);
				folio = " Folio=\""+datos[2]+"\"";
			} else {
				doc.setFolio("");
			}
			
			if (datos[3] != null && !datos[3].trim().equals("")) {
				doc.setSerie(datos[3]);
				serie = " Serie=\""+datos[3]+"\"";
			} else {
				doc.setSerie("");;
			}
			
			if (datos[4] != null && !datos[4].trim().equals("")) {
				doc.setMonedaDR(datos[4]);
			} else {
				doc.setMonedaDR("vacioErr");
			}
			
			String monedaP = pagosTags.pagos.get(pagosTags.pagos.size()-1).getMonedaP();
			
			logger.info(monedaP);
			
			if ( !monedaP.trim().equalsIgnoreCase(datos[5].trim()) ) {
				if (datos[5] != null && !datos[5].trim().equals("")) {
					doc.setTipoCambioDR(datos[5]);
					tipoCambio = " TipoCambioDR=\""+datos[5]+"\"";
				} else {
					doc.setTipoCambioDR("");
				}
			} else {
				doc.setTipoCambioDR("");
				tipoCambio = "";
			}
			
			if (datos[6] != null && !datos[6].trim().equals("")) {
				doc.setMetodoDePagoDR(datos[6]);
			} else {
				doc.setMetodoDePagoDR("vacioErr");
			}
			
			if (datos[7] != null && !datos[7].trim().equals("")) {
				doc.setNumParcialidad(datos[7]);
				numParcialidad = " NumParcialidad=\""+datos[7]+"\"";
			} else {
				doc.setNumParcialidad("");
			}
			
			if (datos[8] != null && !datos[8].trim().equals("")) {
				doc.setImpSaldoAnt(datos[8]);
				impSaldoAnt = " ImpSaldoAnt=\""+datos[8]+"\"";
			} else {
				doc.setImpSaldoAnt("");
			}
			
			if (datos[9] != null && !datos[9].trim().equals("")) {
				doc.setImpPagado(datos[9]);
				impPagado = " ImpPagado=\""+datos[9]+"\"";
			} else {
				doc.setImpPagado("");
			}
			
			if (datos[10] != null && !datos[10].trim().equals("")) {
				doc.setImpSaldoInsoluto(datos[10]);
				impInsoluto = " ImpSaldoInsoluto=\""+datos[10]+"\"";
			} else {
				doc.setImpSaldoInsoluto("");
			}
			
			pagosTags.documentos.add(doc);
			
			return concat.append("\n<pago10:DoctoRelacionado"
					+ " IdDocumento=\""+doc.getIdDocumento()+"\""
					+ serie
					+ folio
					+ " MonedaDR=\""+doc.getMonedaDR()+"\""
					+ tipoCambio
					+ " MetodoDePagoDR=\""+doc.getMetodoDePagoDR()+"\""
					+ numParcialidad
					+ impSaldoAnt
					+ impPagado
					+ impInsoluto
					+ " />\n</pago10:Pago>").toString().getBytes("UTF-8");
		}else {
			return errFormat(linea);
		}
	}
	
	
	
	
	/**
	 * Agrega error de estructura
	 * */
	
	public byte[] errFormat(String numberLine) {
		errFormat.add(""+numberLine);
		return "".getBytes();
	}
	
	/**
	 * Reinicia las Banderas
	 */
	public void clearFlag() {
		tags.isEntidadFiscal = false;
		tags.isEmisor = false;
		tags.isReceptor = false;
		tags.isConceptos = false;
		tags.isConcepto = false;
		tags.isPredial = false;
		tags.isParte = false;
		tags.isComplementoConcepto = false;
		tags.isAduanera = false;
		tags.isRetenciones = false;
		tags.isTralados = false;
		tags.isImpuestos = false;
		tags.isComplemento = false;
		tags.isMovimiento = false;
		// tags.isComprobante=false;
		tags.isAdenda = false;
		tags.isDescriptionTASA = false;
		tags.isFormat = false;
		descriptionFormat = new ArrayList<String>();
		// Limpia variable
		tags.totalRetAndTraDoubl = 0.0D;
	}

	// 24 de Abril 2013 Verificar si una cadena es numérica
	public boolean isNotNumeric(String strNumber) {
		boolean fNotNumber = true; // Cambio de validacion antes False AMDA version 3.3
		int i = 0;
		// while(!fNotNumber && i < strNumber.length()){
		// try{
		// Integer.parseInt(Character.toString(strNumber.charAt(i)));
		// }catch(NumberFormatException ex){
		// fNotNumber = true;
		// break;
		// }
		// i++;
		// };
		if (strNumber.matches(
				"([A-Z]|[a-z]|[0-9]| |Ñ|ñ|!|&quot;|%|&amp;|&apos;|´|-|:|;|&gt;|=|&lt;|@|_|,|\\{|\\}|`|~|á|é|í|ó|ú|Á|É|Í|Ó|Ú|ü |Ü){1,40}")) {
			fNotNumber = false;
		}
		return fNotNumber;
	}

	public String getNameEntityFiscal() {
		if (tags.fis == null) {
			tags.isEntidadFiscal = false;
			return " Nombre=\"No existe la Entidad Fiscal\"";
		} else {
			tags.isEntidadFiscal = true;
			if (tags.fis.getFiscalName() != null) {
				String valNombre = tags.fis.getFiscalName().replaceAll("\\.", "");
				valNombre = valNombre.replaceAll("\\(", "");
				valNombre = valNombre.replaceAll("\\)", "");
				valNombre = valNombre.replace("/", "").toUpperCase();
				System.out.println("Emisro Reg: " + valNombre);
				return " Nombre=\"" + valNombre + "\"";
			} else {
				return " Nombre=\"" + "" + "\"";
			}

		}
	}

	public byte[] receptor(String linea, long numberLine) throws UnsupportedEncodingException {
		lineas = linea.split("\\|");
		//System.out.println("Entrando a Receptor: ");
		//System.out.println("ECB:Antes de reemplazo RFC Generico Receptor: ");
		if (lineas.length >= 3) {
			// reempazar RFC incorrecto por generico
			//System.out.println("ECB:Entrando a reemplazo RFC Generico Receptor: ");
			if (lineas[1].trim().length() > 0) {
				pattern = Pattern.compile(RFC_PATTERN);
				matcher = pattern.matcher(lineas[1].trim());

				if (matcher.matches()) {
					pattern = Pattern.compile(RFC_PATTERN_TWO);
					matcher = pattern.matcher(lineas[1].trim());
					if(matcher.matches()){
						//System.out.println("RFC valido:"+lineas[1].trim());
					}else{
						System.out.println("Reemplazar RFC incorrecto: "+lineas[1].trim()+" por generico: XAXX010101000");
						lineas[1] = "XAXX010101000";
					}
				} else {
					System.out
							.println("Reemplazar RFC incorrecto: " + lineas[1].trim() + " por generico: XAXX010101000");
					lineas[1] = "XAXX010101000";
				}
			}

			tags.RECEPCION_RFC = lineas[1].trim();
			if (tags.RECEPCION_RFC.trim().length() == 0) { // Validacion AMDA Version 3.3
				tags.RECEPCION_RFC = "RFCNecesario";
			}
			// Doble Sellado
			String nombreReceptor = "";

			if (lineas.length > 2) {
				if (!lineas[2].trim().equals("")) {
					String valNombre = lineas[2].trim().replaceAll("\\.", "");
					valNombre = valNombre.replaceAll("\\(", "");
					valNombre = valNombre.replaceAll("\\)", "");
					valNombre = valNombre.replace("/", "");
					// System.out.println("Receptor Reg: "+valNombre );
					nombreReceptor = " Nombre=\"" + Util.convierte(valNombre).toUpperCase() + "\"";
				}
			}

			// Nuevos Atributos AMDA Version 3.3
			String valPais = "";
			String residenciaFiscalReceptor = "";
			String numRegIdTribReceptor = "";
			String usoCFDIReceptor = "";// " UsoCFDI=\"" + "P01" + "\""; // Fijo por el momento

			if (!UtilCatalogos.findUsoCfdi(tags.mapCatalogos, "Por definir").equalsIgnoreCase("vacio")) { // Fijo por el
																											// momento
				usoCFDIReceptor = " UsoCFDI=\"" + UtilCatalogos.findUsoCfdi(tags.mapCatalogos, "Por definir") + "\"";
			} else {
				usoCFDIReceptor = " ErrCompUsoCFDI001=\"" + UtilCatalogos.findUsoCfdi(tags.mapCatalogos, "Por definir")
						+ "\"";
			}

			//System.out.println("Receptor recepPais: " + tags.recepPais);
			//System.out.println("Receptor RFC Receptor Valida ResidenciaFiscal: " + tags.RECEPCION_RFC);
			if (tags.RECEPCION_RFC.equalsIgnoreCase("XEXX010101000")
					|| tags.RECEPCION_RFC.equalsIgnoreCase("XEXE010101000")
					|| tags.RECEPCION_RFC.equalsIgnoreCase("XEXX010101000")) {
				if (tags.recepPais.trim().length() > 0) {
					valPais = UtilCatalogos.findValPais(tags.mapCatalogos, tags.recepPais);
					//System.out.println("Valor Abreviado Pais: " + valPais);
					if (valPais.equalsIgnoreCase("vacio")) {
						valPais = UtilCatalogos.findEquivalenciaPais(tags.mapCatalogos, tags.recepPais);
						//System.out.println("Valor Equivalencia Abreviado Pais: " + valPais);
						if (!valPais.equalsIgnoreCase("vacio")) {
							residenciaFiscalReceptor = " ResidenciaFiscal=\"" + valPais + "\"";
						} else if (valPais.equalsIgnoreCase("MEX")) {
							residenciaFiscalReceptor = " ErrCompResidenciaFiscal001=\"" + tags.recepPais + "\"";
						} else {
							residenciaFiscalReceptor = " ErrCompResidenciaFiscal002=\"" + tags.recepPais + "\"";
						}
					} else if (valPais.equalsIgnoreCase("MEX")) {
						residenciaFiscalReceptor = " ErrCompResidenciaFiscal001=\"" + valPais + "\"";
					} else {
						residenciaFiscalReceptor = " ResidenciaFiscal=\"" + valPais + "\"";
					}

				}
			}

			// Validando RFC si es RFC Generico
			if (!tags.RECEPCION_RFC.equalsIgnoreCase("RFCNecesario")) {

				if (tags.RECEPCION_RFC.equalsIgnoreCase("XEXX010101000")
						|| tags.RECEPCION_RFC.equalsIgnoreCase("XEXE010101000")
						|| tags.RECEPCION_RFC.equalsIgnoreCase("XEXX010101000")) {
					String valRegIdTrib = UtilCatalogos.findNumRegIdTrib(tags.mapCatalogos, lineas[2].trim()); //
					numRegIdTribReceptor = " NumRegIdTrib=\"" + valRegIdTrib + "\"";
//					if (!valRegIdTrib.equalsIgnoreCase("vacio")) {
//						// numRegIdTribReceptor = " NumRegIdTrib=\"" + valRegIdTrib + "\"";
//
//						// Valida Num RegIdTrib
//						String patternReg = "";
//						if (!valPais.trim().equalsIgnoreCase("vacio") && valPais.trim().length() > 0) {
//							patternReg = UtilCatalogos.findPatternRFCPais(tags.mapCatalogos, valPais);
//							System.out.println("PATTERN REGEX:  " + patternReg);
//							if (!patternReg.trim().equalsIgnoreCase("vacio") && patternReg.trim().length() > 0) {
//								// System.out.println("Validando PATTERN REGEX");
//								Pattern p = Pattern.compile(patternReg);
//								Matcher m = p.matcher(valRegIdTrib);
//
//								if (!m.find()) {
//									// RFC no valido
//									// System.out.println("PATTERN REGEX NO ES Valido el RegIdTrib: " + valRegIdTrib
//									// + " : " + valPais + " : " + patternReg);
//									// numRegIdTribReceptor = " ErrReceNumRegIdTrib001=\"" + valRegIdTrib + "\"";
//									numRegIdTribReceptor = " ErrReceNumRegIdTrib001=\"" + " " + "\"";
//								} else {
//									numRegIdTribReceptor = " NumRegIdTrib=\"" + valRegIdTrib + "\"";
//								}
//
//							}
//						}
//
//					} else {
//						// numRegIdTribReceptor = " ErrReceNumRegIdTrib002=\"" + tags.RECEPCION_RFC +
//						// "\"";
//						numRegIdTribReceptor = " ErrReceNumRegIdTrib002=\"" + " " + "\"";
//					}
				}

				// System.out.println("RFC PARA NUMREGIDTRIB: " + tags.RECEPCION_RFC);
				// String valRegIdTrib = UtilCatalogos.findNumRegIdTrib(tags.mapCatalogos,
				// tags.RECEPCION_RFC);
				// if(!valRegIdTrib.equalsIgnoreCase("vacio")){
				// numRegIdTribReceptor = " NumRegIdTrib=\"" +
				// UtilCatalogos.findNumRegIdTrib(tags.mapCatalogos, tags.RECEPCION_RFC) + "\"";
				// String patternReg = "";
				// if(!valPais.trim().equalsIgnoreCase("vacio") && valPais.trim().length() > 0){
				// patternReg = UtilCatalogos.findPatternRFCPais(tags.mapCatalogos, valPais);
				// System.out.println("PATTERN REGEX: " + patternReg);
				// if(!patternReg.trim().equalsIgnoreCase("vacio") && patternReg.trim().length()
				// > 0){
				// System.out.println("Validando PATTERN REGEX");
				// Pattern p = Pattern.compile(patternReg);
				// Matcher m = p.matcher(tags.RECEPCION_RFC);
				//
				// if(!m.find()){
				// //RFC no valido
				// numRegIdTribReceptor = "
				// ElValorRFCNoCumpleConElPatronCorrespondienteDelNumRegIdTrib=\"" +
				// UtilCatalogos.findNumRegIdTrib(tags.mapCatalogos, tags.RECEPCION_RFC) + "\"";
				// }
				//
				// }
				// }
				//
				// }else{
				// numRegIdTribReceptor = "
				// NoSeHaEncontradoElRFCDelReceptorRelacionadoConNumRegIdTrib=\"" +
				// tags.RECEPCION_RFC + "\"";
				// }

				// System.out.println("Valor NumRegIdTrib: " + valRegIdTrib);
			}

			//System.out.println("Saliendo de Receptor: ");
			tags("Receptor", pila); // Validando la forma
			return Util.conctatArguments(
					// tags("Receptor", pila), // Veamos si coloca bien el cierre tag de Emisor
					"\n<cfdi:Receptor Rfc=\"", Util.convierte(lineas[1].trim()), "\"", nombreReceptor,
					residenciaFiscalReceptor, numRegIdTribReceptor, usoCFDIReceptor, " />").toString()
					.getBytes("UTF-8");
			/*
			 * return Util .conctatArguments( tags("Receptor", pila),
			 * "\n<cfdi:Receptor rfc=\"", Util.convierte(lineas[1].trim()), "\"",
			 * lineas.length > 2 ? " nombre=\"" + Util.convierte(lineas[2].trim()) + "\"" :
			 * "", " >").toString().getBytes("UTF-8");
			 */
		} else {
			return formatECB(numberLine);
		}
	}

	/**
	 * 
	 * @param linea
	 * @param numberLine
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unused")
	public byte[] concepto(String linea, long numberLine, HashMap fiscalEntities, HashMap campos22)///, String fileNames)
			throws UnsupportedEncodingException {
		lineas = linea.split("\\|");
		String fileNames = "";
		if (lineas.length >= 3) {

			// System.out.println("Asignando Numero De Conceptos: " +
			// tags.numeroConceptosFac);
			tags.numeroConceptosFac = tags.numeroConceptosFac + 1;
			logger.info("Asignando Numero De Conceptos Despues: " + tags.numeroConceptosFac);
			// System.out.println("Importe en Concepto ANTES: " + lineas[2].trim());
			// HashMap campos = (HashMap) campos22.get(tags.fis.getTaxID());
			// System.out.println("concepto RFC:" + tags.EMISION_RFC);
			HashMap campos = (HashMap) campos22.get(tags.EMISION_RFC);
			//System.out.println("tags.EMISION_RFC:" + tags.EMISION_RFC);
			String unidadVal;
			if (campos != null) {
				// System.out.println("campos(campos !=null):" + campos);
				unidadVal = (String) campos.get("unidadMedida");
				tags.UNIDAD_MEDIDA = unidadVal;
			} else {
				// System.out.println("campos(campos==null):" + campos);
				tags.UNIDAD_MEDIDA = "***NO EXISTE UNIDAD DE MEDIDA DEFINIDA***";
				unidadVal = tags.UNIDAD_MEDIDA;
			}

			String valDescConcep = "";
			if (lineas[1].trim().length() > 0) {
				valDescConcep = Util.convierte(lineas[1]).trim();
				valDescConcep = valDescConcep.replaceAll("\\.", "");
				valDescConcep = valDescConcep.replaceAll("\\(", "");
				valDescConcep = valDescConcep.replaceAll("\\)", "");
				valDescConcep = valDescConcep.replaceAll("/", "");
			}
			if (valDescConcep.equalsIgnoreCase("sin cargos")) {
				return conceptoEnCeros(fileNames);
			} else {
				// Nuevo Campo AMDA Version 3.3 regimenStr = "\n<cfdi:RegimenFiscal Regimen=\""
				// + regVal + "\" />";
				// System.out.println("Tipo Comprobante en Concepto: " + tags.tipoComprobante);
				String valorUnitarioStr = "";
				String nodoValorUnitarioStr = "";
				try {
					Double valUnit = Double.parseDouble(lineas[2].trim());
					if (tags.tipoComprobante.trim().equalsIgnoreCase("I")
							|| tags.tipoComprobante.trim().equalsIgnoreCase("E")
							|| tags.tipoComprobante.trim().equalsIgnoreCase("N")) {
						// Valor unitario debe ser mayor a 0
						if (valUnit <= 0) {
							valorUnitarioStr = "\" ErrCompValUni001=\"";
							// nodoValorUnitarioStr = "\" valorUnitarioDebeSerMayorDeCero=\"" +
							// valorUnitarioStr ;
							nodoValorUnitarioStr = valorUnitarioStr
									+ "El valor valor del campo ValorUnitario debe ser mayor que cero (0) cuando el tipo de comprobante es Ingreso, Egreso o Nomina";
						} else {
							valorUnitarioStr = lineas[2].trim();
							if (UtilCatalogos.decimalesValidationMsj(valorUnitarioStr, tags.decimalesMoneda)) {
								nodoValorUnitarioStr = "\" ValorUnitario=\"" + valorUnitarioStr;
							} else {
								nodoValorUnitarioStr = "\" ErrCompValUni002=\"" + valorUnitarioStr;
							}
							// nodoValorUnitarioStr = "\" ValorUnitario=\"" + valorUnitarioStr ;
						}
					} else if (tags.tipoComprobante.trim().equalsIgnoreCase("T")) {
						// Valor unitario puede ser mayor o igual a 0
						if (valUnit < 0) {
							valorUnitarioStr = "\" ErrCompValUni003=\"";
							// nodoValorUnitarioStr = "\" valorUnitarioDebeSerMenorDeCero=\"" +
							// valorUnitarioStr ;
							nodoValorUnitarioStr = valorUnitarioStr
									+ "El valor valor del campo ValorUnitario debe ser mayor que cero (0) cuando el tipo de comprobante es Traslado";
						} else {
							valorUnitarioStr = lineas[2].trim();
							if (UtilCatalogos.decimalesValidationMsj(valorUnitarioStr, tags.decimalesMoneda)) {
								nodoValorUnitarioStr = "\" ValorUnitario=\"" + valorUnitarioStr;
							} else {
								nodoValorUnitarioStr = "\" ErrCompValUni002=\"" + valorUnitarioStr;
							}
							// nodoValorUnitarioStr = "\" ValorUnitario=\"" + valorUnitarioStr ;
						}
					} else if (tags.tipoComprobante.trim().equalsIgnoreCase("P")) {
						// Valor unitario debe ser igual a 0
						if (valUnit != 0) {
							valorUnitarioStr = "\" ErrCompValUni004=\"";
							// nodoValorUnitarioStr = "\" valorUnitarioDebeSerCero=\"" + valorUnitarioStr ;
							nodoValorUnitarioStr = valorUnitarioStr
									+ "El valor valor del campo ValorUnitario debe ser mayor que cero (0) cuando el tipo de comprobante es Pago";
						} else {
							valorUnitarioStr = lineas[2].trim();
							if (UtilCatalogos.decimalesValidationMsj(valorUnitarioStr, tags.decimalesMoneda)) {
								nodoValorUnitarioStr = "\" ValorUnitario=\"" + valorUnitarioStr;
							} else {
								nodoValorUnitarioStr = "\" ErrCompValUni002=\"" + valorUnitarioStr;
							}
							// nodoValorUnitarioStr = "\" ValorUnitario=\"" + valorUnitarioStr ;
						}
					} else {
						// El tipo de comprobante no esta definido
						valorUnitarioStr = "tipoDeComprobanteNoDefinido";
						nodoValorUnitarioStr = "\" valorUnitarioNoDefinido=\"" + valorUnitarioStr;
					}
				} catch (NumberFormatException e) {
					valorUnitarioStr = "valorUnitarioIncorrecto";
					nodoValorUnitarioStr = "\" valorUnitarioIncorrecto=\"" + valorUnitarioStr;
				}

				String claveUnidad = "";
				if (unidadVal.length() > 0) {
					// claveUnidad = UtilCatalogos.findValClaveUnidad(tags.mapCatalogos, unidadVal);
					claveUnidad = "E48";
				}

				// Importe V 3.3 AMDA pendiente logica de redondeo
				String valImporte = "";
				String lineImporte = "";
				// Double totalRetAndTraDoubl = 0.00;
				if (lineas[2].trim().length() > 0) {
					//System.out.println("Importe en Concepto: " + lineas[2].trim());
					valImporte = lineas[2].trim();
					try {
						Double valImpCon = Double.parseDouble(valImporte);
						if (tags.tipoComprobante.trim().equalsIgnoreCase("I")
								|| tags.tipoComprobante.trim().equalsIgnoreCase("E")
								|| tags.tipoComprobante.trim().equalsIgnoreCase("N")) {
							// System.out.println("Sumando Conceptos AMDA: " + lineas[2].trim());
							// System.out.println("Valor de Suma Conceptos AMDA: " +
							// tags.totalRetAndTraDoubl);
							tags.totalRetAndTraDoubl = tags.totalRetAndTraDoubl + valImpCon;
							String sumCheckDe = UtilCatalogos.decimales(tags.totalRetAndTraDoubl.toString(),
									tags.decimalesMoneda);
							tags.totalRetAndTraDoubl = Double.parseDouble(sumCheckDe);
							System.out.println("Valor de Suma Conceptos Despues AMDA: " + tags.totalRetAndTraDoubl);
						}

					} catch (NumberFormatException e) {
						System.out
								.println("Importe en Concepto Problema al convertir en Numerico: " + lineas[2].trim());
					}
					if (UtilCatalogos.decimalesValidationMsj(valImporte, tags.decimalesMoneda)) {
						lineImporte = "\" Importe=\"" + valImporte;
					} else {
						lineImporte = "\" ErrConcImport001=\"" + valImporte;
					}
					// valImporte = "\" Importe=\"" + lineas[2].trim();

				}

				// Descuento V 3.3 AMDA este campo es opcional, por definir

				// Elemento Impuestos V3.3 AMDA
				String elementImpuestos = "";
				// Elemento Traslados V3.3 AMDA
				String valorBase = "";
				String claveImp = "";
				String valTipoFactor = "Tasa"; // Por definir de donde tomar el valor AMDA
				String tasaOCuotaStr = "";
				String valImporteImpTras = "";
				if (!valorUnitarioStr.equalsIgnoreCase("valorUnitarioDebeSerMayorDeCero")
						&& !valorUnitarioStr.equalsIgnoreCase("valorUnitarioDebeSerMenorDeCero")
						&& !valorUnitarioStr.equalsIgnoreCase("valorUnitarioDebeSerCero")
						&& !valorUnitarioStr.equalsIgnoreCase("tipoDeComprobanteNoDefinido")
						&& !valorUnitarioStr.equalsIgnoreCase("valorUnitarioIncorrecto")) {
					try {
						double value = Double.parseDouble(valorUnitarioStr);
						valorBase = new BigDecimal(value * 1).toString();
						//System.out.println("ValorBase AMDA : " + valorBase);
					} catch (NumberFormatException e) {
						System.out.println("Catch en ValorBase AMDA");
					}
				}

				if (tags.trasladoImpuestoVal.trim().length() > 0) { // Validando el codigo del Impuesto
					//System.out.println("Valor Impuesto Traslado AMDA : " + tags.trasladoImpuestoVal);
					claveImp = UtilCatalogos.findValClaveImpuesto(tags.mapCatalogos, tags.trasladoImpuestoVal);
					//System.out.println("Valor Clave Impuesto Traslado AMDA : " + claveImp);
				}

				if (valTipoFactor.equalsIgnoreCase("Tasa") || valTipoFactor.equalsIgnoreCase("Cuota")) {
					//System.out.println("Validacion TasaOCuota Traslado AMDA : " + tags.trasladoImpuestoVal + " : "
					//		+ valTipoFactor);
					if (tags.retencionImpuestoVal.trim().length() > 0) {
						if (!tags.trasladoImpuestoVal.trim().equalsIgnoreCase("ISR")) {
							if (tags.trasladoImpuestoVal.trim().length() > 0) {
								// tasaOCuotaStr = "\" TasaOCuota=\"" +
								// UtilCatalogos.findValMaxTasaOCuota(tags.mapCatalogos,
								// tags.trasladoImpuestoVal, valTipoFactor);
								tasaOCuotaStr = "\" TasaOCuota=\""
										+ Util.completeZeroDecimals(UtilCatalogos.findValMaxTasaOCuota(
												tags.mapCatalogos, tags.trasladoImpuestoVal, valTipoFactor), 6);
							} else {
								tasaOCuotaStr = "\" ErrConcImpueTra001=\""
										+ Util.completeZeroDecimals(UtilCatalogos.findValMaxTasaOCuota(
												tags.mapCatalogos, tags.trasladoImpuestoVal, valTipoFactor), 6);
							}
						}
					} else {
						tasaOCuotaStr = "\" ErrConcImpueTra002=\"" + tags.trasladoImpuestoVal;
					}
				}

				if (valTipoFactor.equalsIgnoreCase("Tasa") || valTipoFactor.equalsIgnoreCase("Cuota")) {
					if (tags.trasladoImporteVal.trim().length() > 0) {
						//valImporteImpTras = "\" Importe=\"" + UtilCatalogos.decimales(tags.trasladoImporteVal.trim(), tags.decimalesMoneda)  + "\"";
					} else {
						valImporteImpTras = "\" Importe=\"" + "0.00" + "\"";
					}

				}
				// Base = ValImporte, Importe = Base por porcentajemas Base, descripcion mandar
				// Util.convierte(lineas[1]).trim()
				// Map<String, Object> trasladoDoom ;
				String elementTraslado = "";
				if (lineas[1].trim().length() > 1) {
					Map<String, Object> trasladoDoom = UtilCatalogos.findTraslados(tags.mapCatalogos, valImporte,
							valDescConcep, tags.decimalesMoneda, tags.tipoComprobante);
					elementTraslado = "\n<cfdi:Traslados>" + trasladoDoom.get("valNodoStr") + "\n</cfdi:Traslados>";
					tags.sumTotalImpuestosTras = trasladoDoom.get("sumaTotal").toString();
					tags.sumTraTotalIsr = trasladoDoom.get("sumTotalIsr").toString();
					tags.sumTraTotalIva = trasladoDoom.get("sumTotalIva").toString();
					tags.sumTraTotalIeps = trasladoDoom.get("sumTotalIeps").toString();

					try {
						Double sumTotalIsrDo = Double.parseDouble(tags.sumTraTotalIsr);
						tags.sumTraTotalIsrDou = tags.sumTraTotalIsrDou + sumTotalIsrDo;
						tags.sumTraTotalIsr = tags.sumTraTotalIsrDou.toString();
						Double sumTraTotalIvaDou = Double.parseDouble(tags.sumTraTotalIva);
						tags.sumTraTotalIvaDou = tags.sumTraTotalIvaDou + sumTraTotalIvaDou;
						tags.sumTraTotalIva = tags.sumTraTotalIvaDou.toString();
						Double sumTraTotalIepsDou = Double.parseDouble(tags.sumTraTotalIeps);
						tags.sumTraTotalIepsDou = tags.sumTraTotalIepsDou + sumTraTotalIepsDou;
						tags.sumTraTotalIeps = tags.sumTraTotalIepsDou.toString();
					} catch (NumberFormatException e) {
						System.out.println("Calculando TRASLADO Sumas AMDA Error Numerico");
					}

				} else {
					elementTraslado = "\n<cfdi:Traslados>" + "\n<cfdi:Traslado ErrConConcepTra001=\"" + lineas[1].trim()
							+ "\"" + " />" + "\n</cfdi:Traslados>";
				} // Elemento Retenciones V3.3 AMDA
				String valorBaseRet = "";
				String claveImpRet = "";
				String valTipoFactorRet = "Tasa"; // Por definir de donde tomar el valor AMDA
				String tasaOCuotaStrRet = "";
				String valImporteImpRet = "";
				if (!valorUnitarioStr.equalsIgnoreCase("valorUnitarioDebeSerMayorDeCero")
						&& !valorUnitarioStr.equalsIgnoreCase("valorUnitarioDebeSerMenorDeCero")
						&& !valorUnitarioStr.equalsIgnoreCase("valorUnitarioDebeSerCero")
						&& !valorUnitarioStr.equalsIgnoreCase("tipoDeComprobanteNoDefinido")
						&& !valorUnitarioStr.equalsIgnoreCase("valorUnitarioIncorrecto")) {
					try {
						double value = Double.parseDouble(valorUnitarioStr);
						valorBaseRet = new BigDecimal(value * 1).toString();
						// System.out.println("ValorBase Ret AMDA : " + valorBaseRet);
					} catch (NumberFormatException e) {
						System.out.println("Catch en ValorBase Ret AMDA");
					}
				}

				if (tags.retencionImpuestoVal.trim().length() > 0) { // Validando el codigo del Impuesto
					//System.out.println("Valor Impuesto Ret AMDA : " + tags.retencionImpuestoVal);
					claveImpRet = UtilCatalogos.findValClaveImpuesto(tags.mapCatalogos, tags.retencionImpuestoVal);
					//System.out.println("Valor Clave Impuesto Ret AMDA : " + claveImpRet);
				}

				if (valTipoFactorRet.equalsIgnoreCase("Tasa") || valTipoFactorRet.equalsIgnoreCase("Cuota")) {
					//System.out.println(
					//		"Validacion TasaOCuota Ret AMDA : " + tags.retencionImpuestoVal + " : " + valTipoFactorRet);
					if (tags.retencionImpuestoVal.trim().length() > 0) {
						if (!tags.retencionImpuestoVal.trim().equalsIgnoreCase("ISR")) {
							if (tags.trasladoImpuestoVal.trim().length() > 0) {
								tasaOCuotaStrRet = "\" TasaOCuota=\""
										+ Util.completeZeroDecimals(UtilCatalogos.findValMaxTasaOCuota(
												tags.mapCatalogos, tags.retencionImpuestoVal, valTipoFactorRet), 6);
							} else {
								tasaOCuotaStrRet = "\" ErrConImpRet001=\""
										+ Util.completeZeroDecimals(UtilCatalogos.findValMaxTasaOCuota(
												tags.mapCatalogos, tags.retencionImpuestoVal, valTipoFactorRet), 6);
							}
						}
					} else {
						tasaOCuotaStrRet = "\" ErrConImpRet001=\"" + tags.retencionImpuestoVal;
					}
				}

				if (valTipoFactor.equalsIgnoreCase("Tasa") || valTipoFactor.equalsIgnoreCase("Cuota")) {
					//System.out.println(
					//		"Valor Importe Ret AMDA R : " + tags.retencionImporteVal + " : " + valImporteRetencion);
					if (tags.retencionImporteVal.trim().length() > 0) {
						valImporteImpRet = "\" Importe=\"" + tags.retencionImporteVal.trim() + "\"";
					} else {
						valImporteImpRet = "\" Importe=\"" + "0.00" + "\"";
					}
				}

				String elementRetencion = "";
				if (lineas[1].trim().length() > 1) {
					Map<String, Object> retencionDoom = UtilCatalogos.findRetencion(tags.mapCatalogos, valImporte,
							valDescConcep, tags.decimalesMoneda, tags.tipoComprobante);
					elementRetencion = "\n<cfdi:Retenciones>" + retencionDoom.get("valNodoStr")
							+ "\n</cfdi:Retenciones>";
					tags.sumTotalImpuestosReten = retencionDoom.get("sumaTotal").toString();
					tags.sumRetTotalIsr = retencionDoom.get("sumTotalIsr").toString();
					tags.sumRetTotalIva = retencionDoom.get("sumTotalIva").toString();
					tags.sumRetTotalIeps = retencionDoom.get("sumTotalIeps").toString();

					try {
						Double sumTotalIsrDo = Double.parseDouble(tags.sumTraTotalIsr);
						tags.sumRetTotalIsrDou = tags.sumRetTotalIsrDou + sumTotalIsrDo;
						tags.sumRetTotalIsr = tags.sumRetTotalIsrDou.toString();
						Double sumRetTotalIvaDou = Double.parseDouble(tags.sumRetTotalIva);
						tags.sumRetTotalIvaDou = tags.sumRetTotalIvaDou + sumRetTotalIvaDou;
						tags.sumRetTotalIva = tags.sumRetTotalIvaDou.toString();
						Double sumRetTotalIepsDou = Double.parseDouble(tags.sumRetTotalIeps);
						tags.sumRetTotalIepsDou = tags.sumRetTotalIepsDou + sumRetTotalIepsDou;
						tags.sumRetTotalIeps = tags.sumRetTotalIepsDou.toString();
					} catch (NumberFormatException e) {
						System.out.println("Calculando Retencion Sumas AMDA Error Numerico");
					}

				} else {
					elementRetencion = "\n<cfdi:Retenciones>"
							+ "\n<cfdi:Retencion NoSeEncontroUnConceptoRetencionesParaBuscar=\"" + valorBaseRet + "\""
							+ "\n</cfdi:Retenciones>";
				}
				String claveProdServVal = ""; // Fijo por el momento AMDA
				boolean claveProdServTraslado = false;
				boolean claveProdServRetencion = false;
				if (!UtilCatalogos.findClaveProdServbyDesc(tags.mapCatalogos, "Instituciones bancarias")
						.equalsIgnoreCase("vacio")) {
					claveProdServVal = "ClaveProdServ=\""
							+ UtilCatalogos.findClaveProdServbyDesc(tags.mapCatalogos, "Instituciones bancarias"); // Fijo
																													// 84121500
																													// AMDA

				} else {
					claveProdServVal = "ErrConClavPro001=\"" + "vacio"; // Fijo 84121500 AMDA
				}
				boolean paint = false;
				if (elementTraslado.length() > 35 && elementRetencion.length() > 39) {
					elementImpuestos = "\n<cfdi:Impuestos>" + elementTraslado + elementRetencion
							+ "\n</cfdi:Impuestos>";
					paint = true;
				} else if (elementRetencion.length() > 39 && !paint) {
					elementImpuestos = "\n<cfdi:Impuestos>" +
					// elementTraslado +
							elementRetencion + "\n</cfdi:Impuestos>";
				} else if (elementTraslado.length() > 35 && !paint) {
					elementImpuestos = "\n<cfdi:Impuestos>" + elementTraslado + "\n</cfdi:Impuestos>";
				}
				Double valVal = 0D;
				if(tags.sumTotalImpuestosTras!=null && !tags.sumTotalImpuestosTras.trim().isEmpty()) {
					valVal  = Double.parseDouble(tags.sumTotalImpuestosTras);
				}
				tags.sumTotalImpuestosTrasDou = tags.sumTotalImpuestosTrasDou + valVal;
				
				//agregar complemento terceros para interface CFDOPGEST
				String complementoTerceros = "";
				if(fileNames.equals("CFDOPGEST")){
					complementoTerceros = complementoTerceros();
				}
				
				String nodoConcepto = "\n<cfdi:Concepto " + claveProdServVal + "\" Cantidad=\"" + "1"
						+ "\" ClaveUnidad=\"" + claveUnidad + // Pendiente el valor de ClaveUnidad
						"\" Unidad=\"" + unidadVal + "\" Descripcion=\"" + valDescConcep.toUpperCase()
						+ nodoValorUnitarioStr + lineImporte + "\" " + " >" + elementImpuestos + complementoTerceros + "\n</cfdi:Concepto>";
				// Cambio de estructura AMDA Version 3.3
				return Util.conctatArguments(nodoConcepto.toString()).toString().getBytes("UTF-8");
			}
		} else {
			return formatECB(numberLine);
		}
	}

	public byte[] conceptoEnCeros(String fileNames) throws UnsupportedEncodingException {
		tags.isECBEnCeros = true;
		tags.subtotalDoubleTag = 0.0;
		String claveProdServVal = "";

		if (!UtilCatalogos.findClaveProdServbyDesc(tags.mapCatalogos, "Instituciones bancarias")
				.equalsIgnoreCase("vacio")) {
			claveProdServVal = "ClaveProdServ=\""
					+ UtilCatalogos.findClaveProdServbyDesc(tags.mapCatalogos, "Instituciones bancarias");
		} else {
			claveProdServVal = "ErrConClavPro001=\"" + "vacio";
		}
		
		//agregar complemento terceros para interface CFDOPGEST
		String complementoTerceros = "";
		if(fileNames.equals("CFDOPGEST")){
			complementoTerceros = complementoTerceros();
		}
		
		String nodoConcepto = "<cfdi:Concepto " + claveProdServVal
				+ "\" Cantidad=\"1\" ClaveUnidad=\"E48\" Unidad=\"SERVICIO\" "
				+ "Descripcion=\"SERVICIOS DE FACTURACIÓN\"  ValorUnitario=\"0.01\" Importe=\"0.01\"><cfdi:Impuestos>"
				+ "<cfdi:Traslados>"
				+ "<cfdi:Traslado Base=\"1.00\" Impuesto=\"002\" TipoFactor=\"Tasa\" TasaOCuota=\"0.000000\" Importe=\"0.00\"  />"
				+ "</cfdi:Traslados>" + "</cfdi:Impuestos>" + complementoTerceros + "</cfdi:Concepto>";
		return nodoConcepto.getBytes("UTF-8");
	}

	public String domicilioFiscal(HashMap campos22) {
		if (tags.isEntidadFiscal) {
			tags._Calle = "calle=\"" + Util.isNull(tags.fis.getAddress().getStreet()) + "\" ";
			tags._NoExterior = Util.isNullEmpity(tags.fis.getAddress().getExternalNumber(), "noExterior");
			tags._NoInterior = Util.isNullEmpity(tags.fis.getAddress().getInternalNumber(), "noInterior");
			tags._Colonia = Util.isNullEmpity(tags.fis.getAddress().getNeighborhood(), "colonia");
			tags._Localidad = Util.isNullEmpity("", "localidad");
			tags._Referencia = Util.isNullEmpity(tags.fis.getAddress().getReference(), "referencia");
			tags._Municipio = "municipio=\"" + Util.convierte(tags.fis.getAddress().getRegion()) + "\" ";
			if (tags.fis.getAddress().getState() != null) {
				tags._Estado = "estado=\"" + Util.convierte(tags.fis.getAddress().getState().getName()) + "\" ";
				tags._Pais = " pais=\"" + Util.convierte(tags.fis.getAddress().getState().getCountry().getName())
						+ "\" ";
			} else {
				tags._Estado = "estado=\"\" ";
				tags._Pais = " pais=\"\" ";
			}

			tags._CodigoPostal = "codigoPostal=\"" + tags.fis.getAddress().getZipCode() + "\" ";
		} else {
			tags._Calle = "calle=\"\" ";
			tags._NoExterior = "";
			tags._NoInterior = "";
			tags._Colonia = "";
			tags._Localidad = "";
			tags._Referencia = "";
			tags._Municipio = "municipio=\"\" ";
			tags._Estado = "estado=\"\" ";
			tags._Pais = " pais=\"\" ";
			tags._CodigoPostal = "codigoPostal=\"\" ";
		}

		String regimenStr = "";
		HashMap map = (HashMap) campos22.get(tags.EMISION_RFC);
		//System.out.println("***Buscando campos cfd22 para: " + tags.EMISION_RFC);
		if (map != null) {
			tags.regimenFiscalCode = (String) map.get("regimenFiscalCode");
			// System.out.println("***Buscando campos cfd22 para Regimen Fiscal Code: " +
			// map.get("regimenFiscalCode"));
			// System.out.println("***Buscando campos cfd22 para Regimen Fiscal Codigo: " +
			// tags.fis.getAddress().getZipCode());
			// System.out.println("***Buscando campos cfd22 para Regimen Fiscal Codigo
			// Postal: " + map.get("codPostal"));
			// System.out.println("***Buscando campos cfd22 para Regimen Fiscal
			// tags._CodigoPostal: " + tags._CodigoPostal);
			tags._CodigoPostal = map.get("codPostal").toString();
			String regVal = (String) map.get("regimenFiscal");
			// regimenStr = "\n<cfdi:RegimenFiscal Regimen=\"" + regVal + "\" />";
			String regFisCon = UtilCatalogos.findRegFiscalCode(tags.mapCatalogos, regVal);
			if (!regFisCon.equalsIgnoreCase("vacio")) {
				regimenStr = " RegimenFiscal=\"" + regFisCon + "\" "; // Agregue esto /> para cerrar el nodo de concepto
																		// al regresar AMDA
			} else {
				regimenStr = " ErrEmiRegFis001=\"" + regVal + "\" "; // Agregue esto /> para cerrar el nodo de concepto
																		// al regresar AMDA
			}

			tags.REGIMEN_FISCAL = regVal;
		}

		// return Util.conctatArguments("\n<cfdi:DomicilioFiscal ", tags._Calle,
		// tags._NoExterior, tags._NoInterior, tags._Colonia,
		// tags._Localidad, tags._Referencia, tags._Municipio,
		// tags._Estado, tags._Pais, tags._CodigoPostal, " />" + regimenStr.toString(),
		// tags("", pila)).toString();
		return regimenStr; // Agregue este regreso solo de prueba version 3.3 AMDA
	}

	/**
	 * 
	 * @param linea
	 * @param numberLine
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public byte[] impuestos(String linea, long numberLine) throws UnsupportedEncodingException {
		lineas = linea.split("\\|");
		tags.isImpuestos = true;
		tags.isConceptos = false;

		if (lineas.length >= 3) {

			String valSubTotalDou = "";
			//System.out.println("Validacion Subtotal subtotalDoubleTag AMDA : " + tags.subtotalDoubleTag);
			//System.out.println("Validacion Subtotal totalRetAndTraDoubl AMDA : " + tags.totalRetAndTraDoubl);
			if (tags.tipoComprobante.trim().equalsIgnoreCase("I") || tags.tipoComprobante.trim().equalsIgnoreCase("E")
					|| tags.tipoComprobante.trim().equalsIgnoreCase("N")) {
				//System.out.println("Validando Subtotal con total Conceptos AMDA : ");
				if (!tags.subtotalDoubleTag.equals(tags.totalRetAndTraDoubl) && tags.noExentoT) {
					//valSubTotalDou = " ErrCompSubTot004=\"" + "vacio" + "\" ";
				}
			}

			tags.TOTAL_IMP_RET = lineas[1].trim();
			tags.TOTAL_IMP_TRA = lineas[2].trim();
			String totalImpRetLine = "";
			// Double totalImpRet = 0.00;
			// Double tagSumTotalImpuestosRetenDoub = 0.00;
			if (!Util.isNullEmpty(lineas[1].trim())) {
				// try{
				// totalImpRet = Double.parseDouble(tags.TOTAL_IMP_RET);
				// if(tags.sumTotalImpuestosReten.length() > 0){
				// tagSumTotalImpuestosRetenDoub =
				// Double.parseDouble(tags.sumTotalImpuestosReten);
				// if(totalImpRet > tagSumTotalImpuestosRetenDoub || totalImpRet <
				// tagSumTotalImpuestosRetenDoub){
				// totalImpRetLine = "\"
				// ElValorDelCampoTotalImpuestosRetenidosDebeSerIgualALaSumaDeLosImportesRegistradosEnElElementoHijoRetencion=\""
				// + tags.TOTAL_IMP_RET + "\" ";
				// }else{
				//
				// }
				// }
				//
				// }catch(NumberFormatException e){
				//
				// }
				if (UtilCatalogos.decimalesValidationMsj(tags.TOTAL_IMP_RET, tags.decimalesMoneda)) {
					totalImpRetLine = " TotalImpuestosRetenidos=\"" + tags.TOTAL_IMP_RET + "\" ";
					tags.atributoTotalImpuestosReten = true;
				} else {
					totalImpRetLine = " ElValorDelCampoTotalImpuestosRetenidosDebeTenerHastaLaCantidadDeDecimalesQueSoporteLaMoneda=\""
							+ tags.TOTAL_IMP_RET + "\" ";
					tags.atributoTotalImpuestosReten = false;
				}
			} else {
				// tags.atributoTotalImpuestosReten = false;
				totalImpRetLine = " TotalImpuestosRetenidos=\"" + UtilCatalogos.decimales("0.00", tags.decimalesMoneda)
						+ "\" ";
				tags.atributoTotalImpuestosReten = true;

			}

			String totalImpTraLine = "";
			Double totalImpTra = 0.00;
			if (!Util.isNullEmpty(lineas[2].trim())) {
				if (UtilCatalogos.decimalesValidationMsj(tags.TOTAL_IMP_TRA, tags.decimalesMoneda)) {
					totalImpTraLine = " TotalImpuestosTrasladados=\"" + tags.TOTAL_IMP_TRA + "\" ";
					tags.atributoTotalImpuestosTras = true;
				} else {
					totalImpTraLine = " ElValorDelCampoTotalImpuestosTrasladadosDebeTenerHastaLaCantidadDeDecimalesQueSoporteLaMoneda=\""
							+ tags.TOTAL_IMP_TRA + "\" ";
					tags.atributoTotalImpuestosTras = false;
				}
			} else {
				tags.atributoTotalImpuestosTras = false;
			}
			
			
			//BigDecimal retImp = new BigDecimal(tags.TOTAL_IMP_TRA);
			BigDecimal retImp;
			
			if(!Util.isNullEmpty(tags.TOTAL_IMP_RET))
				retImp = new BigDecimal(tags.TOTAL_IMP_RET);
			else
				retImp = new BigDecimal("0");
			
			//retImp = new BigDecimal(tags.TOTAL_IMP_RET);
			if (retImp.compareTo(new BigDecimal("0")  ) ==  0) {
				totalImpRetLine = "";
				tags.atributoTotalImpuestosReten = true;
			} else 
				tags.atributoTotalImpuestosReten = false;

			// if(tags.tipoComprobante.equalsIgnoreCase("T") ||
			// tags.tipoComprobante.equalsIgnoreCase("P")){
			//
			// }else{
			//
			// }

			return Util.conctatArguments(tags("", pila), "\n<cfdi:Impuestos ", totalImpRetLine, totalImpTraLine,
					valSubTotalDou,
					// ">",
					// elementRetencion,
					// elementTraslado,
					">").toString().getBytes("UTF-8");

			// return Util
			// .conctatArguments(
			// tags("", pila),
			// "\n<cfdi:Impuestos ",
			// isNullEmpity(lineas[1], "TotalImpuestosRetenidos"),
			// isNullEmpity(lineas[2], "TotalImpuestosTrasladados"),
			//// ">",
			//// elementRetencion,
			//// elementTraslado,
			// ">").toString().getBytes("UTF-8");
		} else {
			return formatECB(numberLine);
		}
	}

	public byte[] traslados(String linea, long numberLine) throws UnsupportedEncodingException {
		lineas = linea.split("\\|");
		if (lineas.length >= 4) {

			// Elemento Traslados V3.3 AMDA
			String claveImp = "";
			String valTipoFactor = "Tasa"; // Por definir de donde tomar el valor AMDA
			String tasaOCuotaStr = "";
			String valImporteImpTras = "";
			String impuestoLine = "";

			if (lineas[1].trim().length() > 0) { // Validando el codigo del Impuesto
				//System.out.println("Valor Impuesto Traslado AMDA : " + lineas[1].trim());
				claveImp = UtilCatalogos.findValClaveImpuesto(tags.mapCatalogos, lineas[1].trim());
				//System.out.println("Valor Clave Impuesto Traslado AMDA : " + claveImp);
				if (tags.atributoTotalImpuestosTras) {
					if (!claveImp.equalsIgnoreCase("vacio")) {
						impuestoLine = " Impuesto=\"" + claveImp;
					} else {
						impuestoLine = " ErrTraImp001=\"" + claveImp;
					}
				} else {
					impuestoLine = " DebeExistirElCampoTotalImpuestosTraslados=\"" + claveImp;
				}

			}

			String tasaOCuotaResult = "";
			if (valTipoFactor.equalsIgnoreCase("Tasa") || valTipoFactor.equalsIgnoreCase("Cuota")) {
				//System.out.println("Validacion TasaOCuota Traslado AMDA : " + lineas[1].trim() + " : " + valTipoFactor);
				if (!lineas[1].trim().equalsIgnoreCase("ISR")) {

					// tasaOCuotaStr = "\" TasaOCuota=\"" +
					// UtilCatalogos.findValMaxTasaOCuota(tags.mapCatalogos,
					// tags.trasladoImpuestoVal, valTipoFactor);

					// tasaOCuotaStr = "\" TasaOCuota=\"" +
					// Util.completeZeroDecimals(UtilCatalogos.findValMaxTasaOCuotaTraslado(tags.mapCatalogos,
					// lineas[1].trim(), valTipoFactor), 6);

					tasaOCuotaResult = UtilCatalogos.findValMaxTasaOCuotaTraslado(tags.mapCatalogos, lineas[1].trim(),
							valTipoFactor);
					if (!tasaOCuotaResult.equalsIgnoreCase("vacio")) {
						tasaOCuotaStr = "\" TasaOCuota=\"" + Util.completeZeroDecimals(tasaOCuotaResult, 6);
					} else {
						tasaOCuotaStr = "\" ErrTraImp002=\"" + tasaOCuotaResult;
					}

				}

				//System.out.println("Valor TasaOCuota Traslado AMDA : " + tasaOCuotaStr);
			}

			if (valTipoFactor.equalsIgnoreCase("Tasa") || valTipoFactor.equalsIgnoreCase("Cuota")) {
				//System.out.println("Valor Importe AMDA T : " + lineas[3].trim() + " : " + valImporteTraslado);
				//System.out.println("Total Importe Traslado Validando total AMDA T : " + tags.TOTAL_IMP_TRA);
				if (lineas[3].trim().length() > 0) {
					// valImporteImpTras = "\" Importe=\"" +lineas[3].trim() + "\"";
					Double totImpTra = 0.00;
					Double importeDou = 0.00;
					Double valConepto = 0.00;
					if (claveImp.equalsIgnoreCase("001")) {
						System.out.println("Valor Suma Impuesto Traslado ISR AMDA : " + tags.sumTraTotalIsr);
						if (tags.sumTraTotalIsr.trim().length() > 0) {
							try {
								valConepto = valConepto + Double.parseDouble(tags.sumTraTotalIsr);
							} catch (NumberFormatException e) {
								System.out.println("Valor Suma Impuesto Traslado ISR no es numerico al parecer AMDA : "
										+ tags.sumTraTotalIsr);
							}
						}
					} else if (claveImp.equalsIgnoreCase("002")) {
						System.out.println("Valor Suma Impuesto Traslado IVA AMDA : " + tags.sumTraTotalIva);
						if (tags.sumTraTotalIva.trim().length() > 0) {
							try {
								valConepto = valConepto + Double.parseDouble(tags.sumTraTotalIva);
							} catch (NumberFormatException e) {
								System.out.println("Valor Suma Impuesto Traslado IVA no es numerico al parecer AMDA : "
										+ tags.sumTraTotalIva);
							}
						}
					} else if (claveImp.equalsIgnoreCase("003")) {
						System.out.println("Valor Suma Impuesto Traslado IEPS AMDA : " + tags.sumTraTotalIeps);
						if (tags.sumTraTotalIeps.trim().length() > 0) {
							try {
								valConepto = valConepto + Double.parseDouble(tags.sumTraTotalIeps);
							} catch (NumberFormatException e) {
								System.out.println("Valor Suma Impuesto Traslado IEPS no es numerico al parecer AMDA : "
										+ tags.sumTraTotalIeps);
							}
						}
					}
					boolean valid = false;
					try {
						totImpTra = Double.parseDouble(tags.TOTAL_IMP_TRA);
						importeDou = Double.parseDouble(lineas[3].trim());
						double sumtotalTraDou = tags.sumTraTotalIepsDou + tags.sumTraTotalIvaDou
								+ tags.sumTraTotalIsrDou;
						System.out.println("Valor SUMMMM1 Traslados : " + sumtotalTraDou);
						// System.out.println("Valor SUMMMM Traslados : " + valConepto);
						System.out.println("Valor SUMMMM2 Traslados : " + importeDou);
						System.out.println(
								"Valor SUMMMM sumTotalImpuestosTrasDou Traslados : " + tags.sumTotalImpuestosTrasDou);
						if (!(importeDou > tags.sumTotalImpuestosTrasDou)
								&& !(importeDou < tags.sumTotalImpuestosTrasDou)) { // ImporteDou:9 Linea 3, Importe del
																					// Traslado
							valid = true;
							System.out.println("Importes TRUE TASLADOS : ");
						}
						if (totImpTra > importeDou || totImpTra < importeDou) {
							valImporteImpTras = "\" ErrTraImp003=\"" + lineas[3].trim() + "\" ";
						} else if (!valid) {
							valImporteImpTras = "\" ErrImpTraImporte001=\"" + lineas[3].trim() + "\" ";
						} else {
							if (UtilCatalogos.decimalesValidationMsj(lineas[3].trim(), tags.decimalesMoneda)) {
								valImporteImpTras = "\" Importe=\"" + lineas[3].trim() + "\" ";
							} else {
								valImporteImpTras = "\" ErrTraImp004=\"" + lineas[3].trim() + "\" ";
							}
						}

					} catch (NumberFormatException e) {
						System.out.println(
								"Total Importe Traslado Validando total AMDA T Error en Convertido a numerico : "
										+ tags.TOTAL_IMP_TRA);
						valImporteImpTras = "\" ErrTraImp003=\"" + lineas[3].trim() + "\" ";
					}

				} else {
					valImporteImpTras = "\" ErrTraImp004=\"" + lineas[3].trim() + "\" ";
				}
			}

			// String elementTraslado = "\n<cfdi:Traslados>" +
			// "\n<cfdi:Traslado Impuesto=\"" + claveImp +
			// "\" TipoFactor=\"" + valTipoFactor + // Por definir de donde tomar el valor
			// AMDA
			// tasaOCuotaStr +
			// valImporteImpTras + // Por definir como se relaciona el importe
			// " />" +
			// "\n</cfdi:Traslados>";
			// System.out.println("Elemento Traslado Impuestos AMDA : " + elementTraslado);

			// System.out.println("Asignando sumTotalImpuestosTras RESETEO: " +
			// tags.sumTotalImpuestosTras);
			tags.sumTotalImpuestosTrasDou = 0.00;
			// System.out.println("Asignando sumTotalImpuestosTras DESP RESETEO: " +
			// tags.sumTotalImpuestosTras);
			// System.out.println("Asignando Numero De Conceptos RESETEO: " +
			// tags.numeroConceptosFac);
			tags.numeroConceptosFac = 0;
			// System.out.println("Asignando Numero De Conceptos RESETEO DESPUES: " +
			// tags.numeroConceptosFac);
			// System.out.println("Asignando SumTotales RESETEO: " + tags.sumTraTotalIepsDou
			// + " : " + tags.sumTraTotalIvaDou + " : " + tags.sumTraTotalIsrDou);
			double sumtotalTraDou = tags.sumTraTotalIepsDou + tags.sumTraTotalIvaDou + tags.sumTraTotalIsrDou;
			// System.out.println("Asignando sumtotalTraDou Val en Reseteo: " +
			// sumtotalTraDou);
			tags.sumTraTotalIepsDou = 0.00;
			tags.sumTraTotalIvaDou = 0.00;
			tags.sumTraTotalIsrDou = 0.00;
			// System.out.println("Asignando sumTraTotalIepsDou RESETEO DESPUES: " +
			// tags.sumTraTotalIepsDou);
			// System.out.println("Asignando sumTraTotalIvaDou RESETEO DESPUES: " +
			// tags.sumTraTotalIvaDou);
			// System.out.println("Asignando sumTraTotalIsrDou RESETEO DESPUES: " +
			// tags.sumTraTotalIsrDou);
			// System.out.println("Validacion Subtotal totalRetAndTraDoubl AMDA RESETEO : "
			// + tags.totalRetAndTraDoubl);
			tags.totalRetAndTraDoubl = 0.00;
			// System.out.println("Validacion Subtotal totalRetAndTraDoubl AMDA Despues
			// RESETEO : " + tags.totalRetAndTraDoubl);

			return Util.conctatArguments(// "\n<cfdi:Traslados>" ,
					"\n<cfdi:Traslado", impuestoLine, "\" TipoFactor=\"", valTipoFactor, // Por definir de donde tomar
																							// el valor AMDA
					tasaOCuotaStr, valImporteImpTras, // Por definir como se relaciona el importe
					" />").toString().getBytes("UTF-8");

			// return Util
			// .conctatArguments(//"\n<cfdi:Traslados>" ,
			// "\n<cfdi:Traslado Impuesto=\"" , claveImp ,
			// "\" TipoFactor=\"" , valTipoFactor , // Por definir de donde tomar el valor
			// AMDA
			// tasaOCuotaStr ,
			// valImporteImpTras , // Por definir como se relaciona el importe
			// " />" )
			// .toString().getBytes("UTF-8");

			// return Util
			// .conctatArguments("\n<cfdi:Traslado Impuesto=\"",
			// lineas[1].trim(), "\" Tasa=\"", lineas[2].trim(),
			// "\" Importe=\"", lineas[3].trim(),
			// "\"/>")
			// .toString().getBytes("UTF-8");
		} else {
			return formatECB(numberLine);
		}
	}

	/**
	 * 
	 * @param linea
	 * @param numberLine
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public byte[] retenciones(String linea, long numberLine) throws UnsupportedEncodingException {
		lineas = linea.split("\\|");
		if (lineas.length >= 3) {

			// Elemento Retenciones V 3.3 AMDA
			String claveImpRet = "";
			String impuestoLine = "";
			if (lineas[1].trim().length() > 0) { // Validando el codigo del Impuesto
				//System.out.println("Valor Impuesto Ret AMDA : " + lineas[1].trim());
				claveImpRet = UtilCatalogos.findValClaveImpuesto(tags.mapCatalogos, lineas[1].trim());
				//System.out.println("Valor Clave Impuesto Ret AMDA : " + claveImpRet);
				if (!claveImpRet.equalsIgnoreCase("vacio")) {
					impuestoLine = " Impuesto=\"" + claveImpRet;
				} else {
					impuestoLine = " ErrRetImp002=\"" + claveImpRet;
				}
			} else {
				impuestoLine = " ErrRetImp002=\"" + "";
			}

			// String elementRetencion = "\n<cfdi:Retenciones>" +
			// "\n<cfdi:Retencion Impuesto=\"" + claveImpRet +
			// "\" Importe=\"" + lineas[2].trim() + "\"" +
			// "/>" +
			// "\n</cfdi:Retenciones>";
			// System.out.println("Elemento Retenciones Impuestos AMDA : " +
			// elementRetencion);
			String importeLine = "";
			if (tags.atributoTotalImpuestosReten) {
				if (lineas[2].trim().length() > 0) {

					Double totImpRet = 0.00;
					Double importeDou = 0.00;
					Double valConepto = 0.00;

					if (claveImpRet.equalsIgnoreCase("001")) {
						System.out.println("Valor Suma Impuesto Retencion ISR AMDA : " + tags.sumRetTotalIsr);
						if (tags.sumRetTotalIsr.trim().length() > 0) {
							try {
								valConepto = valConepto + Double.parseDouble(tags.sumRetTotalIsr);
							} catch (NumberFormatException e) {
								System.out.println("Valor Suma Impuesto Retencion ISR no es numerico al parecer AMDA : "
										+ tags.sumRetTotalIsr);
							}
						}
					} else if (claveImpRet.equalsIgnoreCase("002")) {
						System.out.println("Valor Suma Impuesto Retencion IVA AMDA : " + tags.sumRetTotalIva);
						if (tags.sumRetTotalIva.trim().length() > 0) {
							try {
								valConepto = valConepto + Double.parseDouble(tags.sumRetTotalIva);
							} catch (NumberFormatException e) {
								System.out.println("Valor Suma Impuesto Retencion IVA no es numerico al parecer AMDA : "
										+ tags.sumRetTotalIva);
							}
						}
					} else if (claveImpRet.equalsIgnoreCase("003")) {
						System.out.println("Valor Suma Impuesto Retencion IEPS AMDA : " + tags.sumRetTotalIeps);
						if (tags.sumRetTotalIeps.trim().length() > 0) {
							try {
								valConepto = valConepto + Double.parseDouble(tags.sumRetTotalIeps);
							} catch (NumberFormatException e) {
								System.out
										.println("Valor Suma Impuesto Retencion IEPS no es numerico al parecer AMDA : "
												+ tags.sumRetTotalIeps);
							}
						}
					}
					boolean valid = false;
					try {
						System.out.println("Valor Total Imp Retenidoss : " + tags.TOTAL_IMP_RET);
						totImpRet = Double.parseDouble(tags.TOTAL_IMP_RET);
						System.out.println("Valor Imp Retenidoss : " + lineas[2].trim());
						importeDou = Double.parseDouble(lineas[2].trim());
						System.out.println("Valor SUMMMM Retenidoss : " + valConepto);
						if (!(valConepto > importeDou) && !(valConepto < importeDou)) {
							valid = true;
							System.out.println("Importes TRUE RETENCIONES : ");
						}
						// if(totImpRet > importeDou || totImpRet < importeDou){
						// importeLine = "\" ErrRetImp001=\"" + lineas[2].trim();
						// }else if(!valid){
						if (!valid) {
							importeLine = "\" ErrImpRetImporte001=\"" + lineas[2].trim();
						} else {
							if (UtilCatalogos.decimalesValidationMsj(lineas[2].trim(), tags.decimalesMoneda)) {
								importeLine = "\" Importe=\"" + lineas[2].trim();
							} else {
								importeLine = "\" ElValorDelCampoImporteCorrespondienteARetencionDebeTenerLaCantidadDeDecimalesQueSoportaLaMoneda=\""
										+ lineas[2].trim();
							}
						}

					} catch (NumberFormatException e) {
						System.out.println(
								"Total Importe Traslado Validando total AMDA T Error en Convertido a numerico : "
										+ tags.TOTAL_IMP_RET);
						importeLine = "\" ElValorDelCampoTotalImpuestosRetenidosDebeSerIgualALaSumaDeLosImportesRegistradosEnElElementoHijoRetencion=\""
								+ lineas[2].trim();
					}

				} else {
					importeLine = "\" ElValorDelCampoImporteCorrespondienteARetencionDebeTenerLaCantidadDeDecimalesQueSoportaLaMoneda=\""
							+ lineas[2].trim();
				}

			} else {
				importeLine = "\" DebeExistirElAtributoTotalImpuestosRetenidos=\"" + lineas[2].trim();
			}

			// System.out.println("Asignando sumTotalImpuestosTras RESETEO: " +
			// tags.sumTotalImpuestosTras);
			// tags.sumTotalImpuestosTrasDou = 0.00;
			// System.out.println("Asignando sumTotalImpuestosTras DESP RESETEO: " +
			// tags.sumTotalImpuestosTras);
			// System.out.println("Asignando Numero De Conceptos RESETEO: " +
			// tags.numeroConceptosFac);
			// tags.numeroConceptosFac = 0;
			// System.out.println("Asignando Numero De Conceptos RESETEO DESPUES: " +
			// tags.numeroConceptosFac);
			// System.out.println("Asignando SumTotales RESETEO: " + tags.sumTraTotalIepsDou
			// + " : " + tags.sumTraTotalIvaDou + " : " + tags.sumTraTotalIsrDou);
			// double sumtotalTraDou = tags.sumTraTotalIepsDou + tags.sumTraTotalIvaDou +
			// tags.sumTraTotalIsrDou;
			// System.out.println("Asignando sumtotalTraDou Val en Reseteo: " +
			// sumtotalTraDou);
			// tags.sumTraTotalIepsDou = 0.00;
			// tags.sumTraTotalIvaDou = 0.00;
			// tags.sumTraTotalIsrDou = 0.00;
			// System.out.println("Asignando sumTraTotalIepsDou RESETEO DESPUES: " +
			// tags.sumTraTotalIepsDou);
			// System.out.println("Asignando sumTraTotalIvaDou RESETEO DESPUES: " +
			// tags.sumTraTotalIvaDou);
			// System.out.println("Asignando sumTraTotalIsrDou RESETEO DESPUES: " +
			// tags.sumTraTotalIsrDou);
			// System.out.println("Validacion Subtotal totalRetAndTraDoubl AMDA RESETEO : "
			// + tags.totalRetAndTraDoubl);
			// tags.totalRetAndTraDoubl = 0.00;
			// System.out.println("Validacion Subtotal totalRetAndTraDoubl AMDA Despues
			// RESETEO : " + tags.totalRetAndTraDoubl);

			return Util.conctatArguments("\n<cfdi:Retencion", impuestoLine, importeLine, "\"/>").toString()
					.getBytes("UTF-8");

			// return Util
			// .conctatArguments("\n<cfdi:Retencion Impuesto=\"", claveImpRet,
			// importeLine, "\"/>").toString().getBytes("UTF-8");
			// return Util
			// .conctatArguments("\n<cfdi:Retencion Impuesto=\"", claveImpRet,
			// "\" Importe=\"", lineas[2].trim(), "\"/>").toString().getBytes("UTF-8");
		} else {
			return formatECB(numberLine);
		}
	}

	/**
	 * 
	 * @param linea
	 * @param numberLine
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public byte[] complemento(String linea, long numberLine) throws UnsupportedEncodingException {
		lineas = linea.split("\\|");
		if (lineas.length >= 5) {
			String nombreCliente = "";

			if (!lineas[2].trim().equals("")) {
				nombreCliente = "nombreCliente=\"" + Util.convierte(lineas[2].trim()) + "\" ";
			}
			return Util
					.conctatArguments("\n<Santander:EstadoDeCuentaBancario version=\"1.0\" ", "numeroCuenta=\"",
							lineas[1].trim(), "\" ", nombreCliente, "periodo=\"", lineas[3].trim(), "\">")
					.toString().getBytes("UTF-8");
		} else {
			return formatECB(numberLine);
		}
	}

	/*
	 * Metodo para agregar el domicilio del receptor a la addenda
	 */
	public byte[] domicilioReceptor() throws UnsupportedEncodingException {
		return Util.conctatArguments("\n<as:DomicilioReceptor ", tags._Calle, tags._NoExterior, tags._NoInterior,
				tags._Colonia, tags._Localidad, tags._Referencia, tags._Municipio, tags._Estado, tags._Pais,
				tags._CodigoPostal, "/>").toString().getBytes("UTF-8");
	}

	/*
	 * Metodo para agregar el domicilio del emisor a la addenda
	 */
	public byte[] domicilioEmisor() throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		if (tags.fis.getAddress() != null) {
			if (tags.fis.getAddress().getStreet() != null) {
				sb.append(Util.isNullEmpity(tags.fis.getAddress().getStreet().toUpperCase(), "Calle"));
			}
			if (tags.fis.getAddress().getExternalNumber() != null) {
				sb.append(Util.isNullEmpity(tags.fis.getAddress().getExternalNumber(), "NoExterior"));
			}
			if (tags.fis.getAddress().getInternalNumber() != null) {
				sb.append(Util.isNullEmpity(tags.fis.getAddress().getInternalNumber(), "NoInterior"));
			}
			if (tags.fis.getAddress().getNeighborhood() != null) {
				sb.append(Util.isNullEmpity(tags.fis.getAddress().getNeighborhood().toUpperCase(), "Colonia"));
			}
			if (tags.fis.getAddress().getReference() != null) {
				sb.append(Util.isNullEmpity(tags.fis.getAddress().getReference(), "Referencia"));
			}
			if (tags.fis.getAddress().getRegion() != null) {
				sb.append(Util.isNullEmpity(tags.fis.getAddress().getRegion().toUpperCase(), "Municipio"));
			}
			if (tags.fis.getAddress().getState() != null) {
				if (tags.fis.getAddress().getState().getName() != null) {
					sb.append(Util.isNullEmpity(tags.fis.getAddress().getState().getName().toUpperCase(), "Estado"));
				}
				if (tags.fis.getAddress().getState().getCountry() != null) {
					if (tags.fis.getAddress().getState().getCountry().getName() != null) {
						sb.append(Util.isNullEmpity(
								tags.fis.getAddress().getState().getCountry().getName().toUpperCase(), "Pais"));
					}
				}
			}
			if (tags.fis.getAddress().getZipCode() != null) {
				sb.append(Util.isNullEmpity(tags.fis.getAddress().getZipCode(), "CodigoPostal"));
			}
			if (tags.fis.getAddress().getZipCode() != null) {
				sb.append(Util.isNullEmpity(tags.fis.getAddress().getCity(), "Ciudad"));
			}

		}
		return Util.conctatArguments("\n<as:DomicilioEmisor ", sb.toString(), "/>").toString().getBytes("UTF-8");
	}

	/**
	 * 
	 * @param linea
	 * @return flg
	 * @throws PatternSyntaxException
	 */
	public boolean validarRFC(String rfc) throws PatternSyntaxException {
		// TODO Auto-generated method stub
		// Patron del
		// RFC--->>>[A-Z,Ñ,&]{3,4}[0-9]{2}[0-1][0-9][0-3][0-9][A-Z,0-9]{2}[0-9,A]

		Pattern p = Pattern.compile("[A-Z,Ñ,&]{3,4}[0-9]{2}[0-1][0-9][0-3][0-9][A-Z,0-9]{2}[0-9,A]");
		Matcher m = p.matcher(rfc);

		if (!m.find()) {
			// RFC no valido
			return false;
		} else {
			// RFC valido
			return true;
		}
	}

	/**
	 * 
	 * @param linea
	 * @param numberLine
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public byte[] movimeinto(String linea, long numberLine) throws UnsupportedEncodingException {

		//System.out.println("length: " + lineas.length);
		lineas = linea.split("\\|");
		if (lineas.length >= 7) {
			Calendar c = Calendar.getInstance();

			String[] date = lineas[1].trim().split("-");
			String fechaCal = "";
			try {
				c.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]), 0, 0, 0);
				fechaCal = "fecha=\"" + Util.convertirFecha(c.getTime()) + "\"";
			} catch (Exception e) {
				e.printStackTrace();
			}

			String rfcEnajenante = lineas[4];

			// Validar el RFC
			boolean flgRfcOk;
			flgRfcOk = validarRFC(rfcEnajenante);

			if ((rfcEnajenante != null) && (rfcEnajenante.trim().length() > 0) && flgRfcOk) {

				return Util
						.conctatArguments("\n<Santander:MovimientoECBFiscal ", fechaCal, " descripcion=\"",
								Util.convierte(lineas[3].trim()), "\"", " RFCenajenante=\"",
								Util.convierte(lineas[4].trim()), "\"", " Importe=\"", lineas[5].trim(), "\"/>")
						.toString().getBytes("UTF-8");
			} else {
				/*
				 * StringBuffer sb = new StringBuffer(); sb =
				 * Util.conctatArguments("\n<ecb:MovimientoECB ", fechaCal, " descripcion=\"",
				 * Util.convierte(lineas[3].trim()), "\"", " importe=\"", lineas[5].trim(),
				 * "\"/>"); this.lstMovimientosECB.add(sb);
				 * 
				 * return "".getBytes("UTF-8");
				 */

				return Util
						.conctatArguments("\n<Santander:MovimientoECB ", fechaCal, " descripcion=\"",
								Util.convierte(lineas[3].trim()), "\"", " importe=\"", lineas[5].trim(), "\"/>")
						.toString().getBytes("UTF-8");

			}
		} else {
			return formatECB(numberLine);
		}

	}

	/**
	 * 
	 * @param linea
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public byte[] domicilio(String linea, long numberLine) throws UnsupportedEncodingException {
		lineas = linea.split("\\|");
		if (lineas.length >= 11) {
			tags._Calle = Util.isNullEmpity(lineas[1].trim(), "Calle");
			tags._NoExterior = Util.isNullEmpity(lineas[2].trim(), "NoExterior");
			tags._NoInterior = Util.isNullEmpity(lineas[3].trim(), "NoInterior");
			tags._Colonia = Util.isNullEmpity(lineas[4].trim(), "Colonia");
			tags._Localidad = Util.isNullEmpity(lineas[5].trim(), "Localidad");
			tags._Referencia = Util.isNullEmpity(lineas[6].trim(), "Referencia");
			tags._Municipio = Util.isNullEmpity(lineas[7].trim(), "Municipio");
			tags._Estado = Util.isNullEmpity(lineas[8].trim(), "Estado");
			tags._Pais = " Pais=\"" + lineas[9].trim().toUpperCase() + "\" ";
			tags._CodigoPostal = lineas.length >= 11 ? Util.isNullEmpity(lineas[10].trim(), "CodigoPostal") : "";
			tags("", pila).toString();
			// return Util
			// .conctatArguments("\n<cfdi:Domicilio ", tags._Calle,
			// tags._NoExterior, tags._NoInterior, tags._Colonia,
			// tags._Localidad, tags._Referencia, tags._Municipio,
			// tags._Estado, tags._Pais, tags._CodigoPostal,
			// " />", tags("", pila)).toString().getBytes("UTF-8");
			return "".getBytes("UTF-8");
		} else {
			return formatECB(numberLine);
		}
	}

	
	public void getInfoCfdiRelacionado(String linea) throws UnsupportedEncodingException {
		lineas = linea.split("\\|");
		if (lineas.length >= 3) {
			tags.claveTipoRelacion = lineas[1];
			for (int i = 2; i < lineas.length - 1; i++) {
				String currUUID = lineas[i];
				tags.uuidsTipoRelacion.add(currUUID);
			}
		}
	}

	public ByteArrayOutputStream cfdiRelacionado(ByteArrayOutputStream out) throws UnsupportedEncodingException {
		StringBuffer result = new StringBuffer();
		String regExUUID = "[a-f0-9A-F]{8}-[a-f0-9A-F]{4}-[a-f0-9A-F]{4}-[a-f0-9A-F]{4}-[a-f0-9A-F]{12}";
		if ((tags.claveTipoRelacion == null || tags.claveTipoRelacion.isEmpty()) && tags.uuidsTipoRelacion.isEmpty()
				&& tags.tipoComprobante.equalsIgnoreCase("E") && true) {
			if (tags.mapCatalogos.get("CFDIRelacionadoGenerico") != null
					&& !tags.mapCatalogos.get("CFDIRelacionadoGenerico").isEmpty()) {
				CatalogosDom valoresCFDIRelGen = tags.mapCatalogos.get("CFDIRelacionadoGenerico").get(0);
				tags.claveTipoRelacion = valoresCFDIRelGen.getVal1();
				tags.uuidsTipoRelacion.add(valoresCFDIRelGen.getVal2());
			} 
		}

		if (tags.claveTipoRelacion != null && !tags.claveTipoRelacion.isEmpty()
				&& tags.tipoComprobante.equalsIgnoreCase("E")) {
			String claveTipoRelacion = tags.claveTipoRelacion;
			boolean existeTipoRelacion = UtilCatalogos.existClaveInTipoRelacion(tags.mapCatalogos, claveTipoRelacion);
			if (existeTipoRelacion) {
				result.append("<cfdi:CfdiRelacionados TipoRelacion=\"" + claveTipoRelacion + "\">");
				for (String currUUID : tags.uuidsTipoRelacion) {
					if (!currUUID.isEmpty() && currUUID.matches(regExUUID)) {
						result.append("<cfdi:CfdiRelacionado UUID=\"" + currUUID.toUpperCase() + "\"/>");
					} else {
						result.append("<cfdi:CfdiRelacionado ErrCFDIRel002=\""
								+ (currUUID.isEmpty() ? "Vacio" : currUUID) + "\"/>");
					}
				}
			} else {

				result.append("<cfdi:CfdiRelacionados ErrCFDIRel002=\""
						+ (tags.claveTipoRelacion.isEmpty() ? "Vacio" : tags.claveTipoRelacion) + "\">");
			}
			result.append("</cfdi:CfdiRelacionados>");
			tags.claveTipoRelacion = "";
			tags.uuidsTipoRelacion.clear();
		} else {
			tags.claveTipoRelacion = "";
			tags.uuidsTipoRelacion.clear();
			result.append("");
		}
		String xml = out.toString("UTF-8");
		if (xml.indexOf("<cfdi:Emisor") != -1) {
			String strCfdiRelacion = "";
			strCfdiRelacion = result.toString() + "<cfdi:Emisor";
			xml = xml.replace("<cfdi:Emisor", strCfdiRelacion);
		}
		return UtilCatalogos.convertStringToOutpuStream(xml);
	}

	public TagsXML getTags() {
		return tags;
	}

	public void setTags(TagsXML tags) {
		this.tags = tags;
	}

	public Stack<String> getPila() {
		return pila;
	}

	public void setPila(Stack<String> pila) {
		this.pila = pila;
	}

	public List<String> getDescriptionFormat() {
		return descriptionFormat;
	}

	public void setDescriptionFormat(List<String> descriptionFormat) {
		this.descriptionFormat = descriptionFormat;
	}

	public byte[] formatECB(long numberLine) {
		tags.isFormat = true;
		descriptionFormat.add("" + numberLine);
		return "".getBytes();
	}

	public List<StringBuffer> getLstMovimientosECB() {
		return lstMovimientosECB;
	}

	public void setLstMovimientosECB(List<StringBuffer> lstMovimientosECB) {
		this.lstMovimientosECB = lstMovimientosECB;
	}

	public String getValImporteRetencion() {
		return valImporteRetencion;
	}

	public void setValImporteRetencion(String valImporteRetencion) {
		this.valImporteRetencion = valImporteRetencion;
	}

	public String getValImporteTraslado() {
		return valImporteTraslado;
	}

	public void setValImporteTraslado(String valImporteTraslado) {
		this.valImporteTraslado = valImporteTraslado;
	}
	
	
	
	public List<String> getErrFormat() {
		return errFormat;
	}


	public void setErrFormat(List<String> errFormat) {
		this.errFormat = errFormat;
	}
	
	


	public XMLPagos getPagosTags() {
		return pagosTags;
	}


	public void setPagosTags(XMLPagos pagosTags) {
		this.pagosTags = pagosTags;
	}


	private String complementoTerceros(){
		StringBuilder result = new StringBuilder();
		String attrName = "";
		if (tags.fis != null){
			if (tags.fis.getFiscalName() != null) {
				String valNombre = tags.fis.getFiscalName().replaceAll("\\.", "");
				valNombre = valNombre.replaceAll("\\(", "");
				valNombre = valNombre.replaceAll("\\)", "");
				valNombre = valNombre.replace("/", "").toUpperCase();
				
				attrName = " nombre=\"" + valNombre + "\"";
			} else {
				attrName = " nombre=\"" + "" + "\"";
			}
			result.append("\n<cfdi:ComplementoConcepto>");
			//terceros start
			result.append("\n<terceros:PorCuentadeTerceros xmlns:terceros=\"http://www.sat.gob.mx/terceros\" version=\"1.1\" ");
			result.append("rfc=\"").append(tags.EMISION_RFC).append("\" ");
			result.append(attrName).append(">");
			//<terceros:InformacionFiscalTercero
			result.append("\n<terceros:InformacionFiscalTercero ");
			result.append("calle=\"" + Util.isNull(tags.fis.getAddress().getStreet()) + "\" ");
			result.append(Util.isNullEmpity(tags.fis.getAddress().getExternalNumber(), "noExterior"));
			result.append(Util.isNullEmpity(tags.fis.getAddress().getInternalNumber(), "noInterior"));
			result.append(Util.isNullEmpity(tags.fis.getAddress().getNeighborhood(), "colonia"));
			result.append(Util.isNullEmpity("", "localidad"));
			result.append("municipio=\"" + Util.convierte(tags.fis.getAddress().getRegion()) + "\" ");
			if (tags.fis.getAddress().getState() != null) {
				result.append("estado=\"" + Util.convierte(tags.fis.getAddress().getState().getName()) + "\" ");
				result.append(" pais=\"" + Util.convierte(tags.fis.getAddress().getState().getCountry().getName()) + "\" ");
			} else {
				result.append("estado=\"\" ");
				result.append(" pais=\"\" ");
			}
			result.append("codigoPostal=\"" + tags.fis.getAddress().getZipCode() + "\"/>");
			//terceros:impuestos
			result.append("\n<terceros:Impuestos>");
			result.append("\n<terceros:Retenciones>");
			result.append("\n<terceros:Retencion impuesto=\"IVA\" importe=\"0.00\"/>");
			result.append("\n</terceros:Retenciones>");
			result.append("\n<terceros:Traslados>");
			result.append("\n<terceros:Traslado impuesto=\"IVA\" tasa=\"0\" importe=\"0.00\"/>");
			result.append("\n</terceros:Traslados>");
			result.append("</terceros:Impuestos>");
			//terceros end
			result.append("\n</terceros:PorCuentadeTerceros>");
			
			result.append("\n</cfdi:ComplementoConcepto>");
		}
		
		return result.toString();
	}

}
