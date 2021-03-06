
package com.interfactura.firmalocal.xml.timbradonat;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "WebService1Soap", targetNamespace = "http://tempuri.org/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface WebService1Soap {


    /**
     * 
     * @param cfDv3
     * @param solotimbre
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "GeneraTimbreDonat", action = "http://tempuri.org/GeneraTimbreDonat")
    @WebResult(name = "GeneraTimbreDonatResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GeneraTimbreDonat", targetNamespace = "http://tempuri.org/", className = "com.interfactura.firmalocal.xml.timbradonat.GeneraTimbreDonat")
    @ResponseWrapper(localName = "GeneraTimbreDonatResponse", targetNamespace = "http://tempuri.org/", className = "com.interfactura.firmalocal.xml.timbradonat.GeneraTimbreDonatResponse")
    public String generaTimbre(
        @WebParam(name = "CFDv3", targetNamespace = "http://tempuri.org/")
        String cfDv3,
        @WebParam(name = "nombreInterfaz", targetNamespace = "http://tempuri.org/")
        String nombreInterfaz,
        @WebParam(name = "numeroProceso", targetNamespace = "http://tempuri.org/")
        int numeroProceso,
        @WebParam(name = "intentoConexion", targetNamespace = "http://tempuri.org/")
        int intentoConexion,
        @WebParam(name = "tipoFormato", targetNamespace = "http://tempuri.org/")
        int tipoFormato,
        @WebParam(name = "periodo", targetNamespace = "http://tempuri.org/")
        String periodo,
        @WebParam(name = "nombreAplicativo", targetNamespace = "http://tempuri.org/")
        String nombreAplicativo);

    
}
