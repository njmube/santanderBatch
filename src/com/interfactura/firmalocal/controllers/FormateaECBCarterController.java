package com.interfactura.firmalocal.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Controller;

@Controller
public class FormateaECBCarterController {

	public static String PathECBEntrada = "/planCFD/procesos/Interfactura/interfaces/";
	public static String PathECBSalida = "/salidas/CFDProcesados/";
	public static String PathECBCatalogos = "/planCFD/procesos/Interfactura/interfaces/";

	public static String carterConceptsFileName = "carterConceptos.TXT";
	public static String filesExtension = ".TXT";

	BigDecimal totalMnOriginal;
	BigDecimal newTotalMn;
	
	BigDecimal totalConceptsA;
	BigDecimal ivaA;
	BigDecimal tasa;
	BigDecimal ivaMnOriginal;
	BigDecimal ivaB;
	BigDecimal montoConceptosGrav;

	StringBuilder fileBlockOne;
	StringBuilder fileBlockTwo;

	StringBuilder lineSixSb;

	String firstLine = null;
	List<String> lineSixList = null;

	List<String> carterConceptList = null;
	
	//rodolform
        BigDecimal UDIVal = BigDecimal.ZERO;

	public FormateaECBCarterController() {

	}

	public boolean processECBTxtFile(String fileName, String timeStamp) {
            System.out.println("Inicia Formatea CARTER - " + fileName);
            boolean result = true;
           
            try {
                FileInputStream fileToProcess = null;
                DataInputStream in = null;
                BufferedReader br = null;

                FileOutputStream fos = null;
                OutputStreamWriter osw = null;
                Writer fileWriter = null;

    //			FileOutputStream fosControl = null;
    //			OutputStreamWriter oswControl = null;
    //			Writer fileWriterControl = null;

                File outputFile;
                //File outputControlFile;   
                
                //cambio rodolfor
                File inputFileUDI = new File(PathECBEntrada + "UDI" + fileName.substring(fileName.length() - 8) + filesExtension );
                FileInputStream fileToProcessUDI;
                DataInputStream inUDI;
                BufferedReader brUDI;               

                //leer el archivo de UDI
                if (inputFileUDI.exists()) {
                    fileToProcessUDI = new FileInputStream(inputFileUDI);
                    inUDI = new DataInputStream(fileToProcessUDI);
                    brUDI = new BufferedReader(new InputStreamReader(inUDI, "UTF-8"));
                    String strLineUDI;

                    while ((strLineUDI = brUDI.readLine()) != null) {
                        strLineUDI = strLineUDI.trim();                           
                        UDIVal = new BigDecimal(strLineUDI);                        
                    }
                    System.out.println("Valor UDI - " + UDIVal);
                   
                    fileToProcessUDI.close();
                    inUDI.close();
                    brUDI.close();                    
                }

                File inputFile = new File(PathECBEntrada + fileName + filesExtension);
                if (inputFile.exists()) {
                    fileToProcess = new FileInputStream(PathECBEntrada + fileName + filesExtension);
                    in = new DataInputStream(fileToProcess);
                    br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    String strLine;

                    loadCarterConceptList();

                    outputFile = new File(PathECBEntrada + "GENERATED_" + fileName + filesExtension);
                    //outputControlFile = new File(PathECBSalida + "CONTROL_" + fileName + filesExtension);

                    fos = new FileOutputStream(outputFile);
                    osw = new OutputStreamWriter(fos, "UTF-8");
                    fileWriter = new BufferedWriter(osw);

    //				fosControl = new FileOutputStream(outputControlFile);
    //				oswControl = new OutputStreamWriter(fosControl, "UTF-8");
    //				fileWriterControl = new BufferedWriter(oswControl);

                    fileBlockOne = new StringBuilder();
                    fileBlockTwo = new StringBuilder();
                    lineSixSb = new StringBuilder();

                    newTotalMn = BigDecimal.ZERO;
                    totalMnOriginal = BigDecimal.ZERO;
                    
                    totalConceptsA = BigDecimal.ZERO;
                    ivaA = BigDecimal.ZERO;
                    tasa = BigDecimal.ZERO;
                    ivaMnOriginal = BigDecimal.ZERO;
                    ivaB = BigDecimal.ZERO;
                    montoConceptosGrav = BigDecimal.ZERO;
                    

                    firstLine = null;

                    lineSixList = new ArrayList<String>();

                    boolean firstLoop = true;
                    BigInteger ecbCount = BigInteger.ZERO;
                    BigInteger ecbWritten = BigInteger.ZERO;
                    StringBuilder ecbError = new StringBuilder();
                    String numCta = "NumeroDefault";
                    while ((strLine = br.readLine()) != null) {
                        strLine = strLine.trim();
                        
                        if (!strLine.equals("")) {
                            String[] arrayValues = strLine.split("\\|");
                            int lineNum = Integer.parseInt(arrayValues[0]);

                            if (lineNum == 1) {// linea 1

                                if (!firstLoop) {
                                    boolean exception = false;
                                    String ecbBakup = firstLine + "\n" 
                                            + fileBlockOne.toString() 
                                            + lineSixSb.toString()
                                            + fileBlockTwo.toString();
                                    
                                    try{
                                        firstLine = FormateaECBIvaController.truncateExcangeFromFirstLine(firstLine);
                                    }catch(Exception e){
                                        ecbError.append("-error:Error al convertir tipo de cambio a dos decimales\n");
                                    }
                                    
                                    if (ecbError.toString().isEmpty()) {
                                        if(tasa.compareTo(BigDecimal.ZERO) != 0){
                                            try{
                                                //calcular iva conceptos fuera de la lista
                                                BigDecimal ivaPaso0 = (totalMnOriginal.multiply(tasa)).divide(new BigDecimal(100));
                                                ivaPaso0  = ivaPaso0.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                                
                                                if(ivaPaso0.compareTo(ivaMnOriginal) != 0){
                                                    ivaA = totalConceptsA.multiply(tasa).divide(new BigDecimal(100));
                                                    ivaA = ivaA.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                                    //calcular ivaB
                                                    ivaB = ivaMnOriginal.subtract(ivaA);
                                                    //calcular monto de conceptos gravados
                                                    montoConceptosGrav = (ivaB.multiply(new BigDecimal(100))).divide(tasa);
                                                    
                                                    BigDecimal montoConceptosGravRounded = montoConceptosGrav
                                                            .setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                                    BigDecimal newTotal = (montoConceptosGravRounded.add(totalConceptsA))
                                                            .setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                                    
                                                    
                                                    if (totalMnOriginal.compareTo(newTotal) != 0) {
                                                        //cambiar montos de conceptos informados
                                                        lineSixSb = processSixLines(lineSixList, totalMnOriginal, montoConceptosGrav);
                                                    }
                                                }
                                            }catch(Exception e){
                                                System.out.println(ecbCount.toString() + "---Excepcion al hacer calculos en ECB numero de cuenta: "
                                                        + numCta);
                                                exception = true;
                                            }
                                        }
                                    } else {
                                        System.out.println(ecbCount.toString() + "---Errores en ECB numero de cuenta: " + numCta);
                                        System.out.println(ecbError.toString());
                                    }
                                    
                                    if(!exception){

                                        //rodolform
                                        System.out.println("firstLine - " + firstLine);
                                        System.out.println("fileBlockOne - " + fileBlockOne);
                                        System.out.println("lineSixSb - " + lineSixSb);
                                        System.out.println("fileBlockTwo - " + fileBlockTwo);                                      

                                        String txtUDI = "|UDI|";
                                        String txtMXV = "|MXV|";
                                        if(fileBlockOne.toString().contains(txtUDI) )
                                        {
                                            //reemplazar UDI por MVX
                                            int indexOfUDI = fileBlockOne.indexOf(txtUDI);
                                            System.out.println("indexOfUDI - " + indexOfUDI);
                                            fileBlockOne.replace(indexOfUDI, indexOfUDI+5,txtMXV);
                                            System.out.println("fileBlockOne reemplazo - " + fileBlockOne);

                                            //reemplazar el 0.00 por el tipo de cambio traido del archivo de UDIYYYYMMDD
                                            //donde la frcha debe ser igual a la del archivo fuente                                           
                                            String[] arrayFirstLine = firstLine.split("\\|");                                         
                                            firstLine  = "01|"
                                                + arrayFirstLine[1].toString() + "|"
                                                + arrayFirstLine[2].toString() + "|"
                                                + arrayFirstLine[3].toString() + "|"
                                                + arrayFirstLine[4].toString() + "|"
                                                + arrayFirstLine[5].toString() + "|"
                                                + arrayFirstLine[6].toString() + "|"
                                                + arrayFirstLine[7].toString() + "|"  
                                                + arrayFirstLine[8].toString() + "|"               
                                                + UDIVal;

                                                System.out.println("firstLine reemplazo - " + firstLine);
                                        }
                                        //fin rodolform

                                        fileWriter.write(firstLine + "\n" 
                                                + fileBlockOne.toString() 
                                                + lineSixSb.toString()
                                                + fileBlockTwo.toString());
                                    }else{
                                        fileWriter.write(ecbBakup);
                                    }

                                    ecbWritten = ecbWritten.add(BigInteger.ONE);
                                    resetECB();
                                }

                                ecbCount = ecbCount.add(BigInteger.ONE);
                                ecbError = new StringBuilder();
                                firstLine = strLine;
                                
                                try {
                                    totalMnOriginal = new BigDecimal(arrayValues[5].trim());
                                } catch (Exception e) {
                                    ecbError.append("-error: no se pudo leer el subtotal\n");
                                }
                                try {
                                    ivaMnOriginal = new BigDecimal(arrayValues[6].trim());
                                } catch (Exception e) {
                                    ecbError.append("-error: no se pudo leer el iva informado en linea 1\n");
                                }
                                try{
                                    numCta = arrayValues[2].trim();
                                }catch(Exception e){
                                    numCta = "NumeroDefault";
                                    ecbError.append("-error: no se pudo leer el numero de cuenta\n");
                                }

                            } else if (lineNum > 1 && lineNum < 6) {// lineas 2 a 5
                                fileBlockOne.append(strLine + "\n");
                            } else if (lineNum == 6) {// linea 6
                                lineSixSb.append(strLine + "\n");
                                lineSixList.add(strLine);
                                try{
                                    if(!listContains(carterConceptList, arrayValues[1].trim())){
                                        totalConceptsA = totalConceptsA.add(new BigDecimal(arrayValues[2].trim()));
                                    }
                                }catch(Exception e){
                                    ecbError.append("-error: no se pudo hacer la suma de importes de conceptos\n");
                                }
                            } else if (lineNum > 6) {// lineas 7 a 11
                                if(lineNum == 9){
                                    try {
                                        if (arrayValues[1].equalsIgnoreCase("IVA")) {
                                            tasa = new BigDecimal(arrayValues[2].trim());
                                        }
                                    } catch (Exception e) {
                                        ecbError.append("-error: No se pudo leer el valor de tasa\n");
                                    }
                                }
                                fileBlockTwo.append(strLine + "\n");
                            }
                        }
                        firstLoop = false;
                    }
                    if (ecbWritten.compareTo(ecbCount) != 0) {//escribir ultimo ecb
                        System.out.println("Escribiendo ultimo ECB");

                        boolean exception = false;
                        String ecbBakup = firstLine + "\n" 
                                + fileBlockOne.toString() 
                                + lineSixSb.toString()
                                + fileBlockTwo.toString();
                        
                        try{
                            firstLine = FormateaECBIvaController.truncateExcangeFromFirstLine(firstLine);
                        }catch(Exception e){
                            ecbError.append("-error:Error al convertir tipo de cambio a dos decimales\n");
                        }
                        
                        if (ecbError.toString().isEmpty()) {
                            if(tasa.compareTo(BigDecimal.ZERO) != 0){
                                try{
                                    //calcular iva conceptos fuera de la lista
                                    BigDecimal ivaPaso0 = (totalMnOriginal.multiply(tasa)).divide(new BigDecimal(100));
                                    ivaPaso0  = ivaPaso0.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                    
                                    if(ivaPaso0.compareTo(ivaMnOriginal) != 0){
                                        ivaA = totalConceptsA.multiply(tasa).divide(new BigDecimal(100));
                                        ivaA = ivaA.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                        //calcular ivaB
                                        ivaB = ivaMnOriginal.subtract(ivaA);
                                        //calcular monto de conceptos gravados
                                        montoConceptosGrav = (ivaB.multiply(new BigDecimal(100))).divide(tasa);
                                        
                                        BigDecimal montoConceptosGravRounded = montoConceptosGrav
                                                .setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                        BigDecimal newTotal = (montoConceptosGravRounded.add(totalConceptsA))
                                                .setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                        
                                        
                                        if (totalMnOriginal.compareTo(newTotal) != 0) {
                                            //cambiar montos de conceptos informados
                                            lineSixSb = processSixLines(lineSixList, totalMnOriginal, montoConceptosGrav);
                                        }
                                    }
                                }catch(Exception e){
                                    System.out.println(ecbCount.toString() + "---Excepcion al hacer calculos en ECB numero de cuenta: "
                                            + numCta);
                                    exception = true;
                                }
                            }
                        } else {
                            System.out.println(ecbCount.toString() + "---Errores en ECB numero de cuenta: " + numCta);
                            System.out.println(ecbError.toString());
                        }
                        
                        if(!exception){

                            //rodolform
                            System.out.println("firstLine - " + firstLine);
                            System.out.println("fileBlockOne - " + fileBlockOne);
                            System.out.println("lineSixSb - " + lineSixSb);
                            System.out.println("fileBlockTwo - " + fileBlockTwo);                                      

                            String txtUDI = "|UDI|";
                            String txtMXV = "|MXV|";
                            if(fileBlockOne.toString().contains(txtUDI) )
                            {
                                //reemplazar UDI por MVX
                                int indexOfUDI = fileBlockOne.indexOf(txtUDI);
                                System.out.println("indexOfUDI - " + indexOfUDI);
                                fileBlockOne.replace(indexOfUDI, indexOfUDI+5,txtMXV);
                                System.out.println("fileBlockOne reemplazo - " + fileBlockOne);

                                //reemplazar el 0.00 por el tipo de cambio traido del archivo de UDIYYYYMMDD
                                //donde la frcha debe ser igual a la del archivo fuente                                           
                                String[] arrayFirstLine = firstLine.split("\\|");                                         
                                firstLine  = "01|"
                                    + arrayFirstLine[1].toString() + "|"
                                    + arrayFirstLine[2].toString() + "|"
                                    + arrayFirstLine[3].toString() + "|"
                                    + arrayFirstLine[4].toString() + "|"
                                    + arrayFirstLine[5].toString() + "|"
                                    + arrayFirstLine[6].toString() + "|"
                                    + arrayFirstLine[7].toString() + "|"  
                                    + arrayFirstLine[8].toString() + "|"               
                                    + UDIVal;

                                    System.out.println("firstLine reemplazo - " + firstLine);
                            }
                            //fin rodolform

                            fileWriter.write(firstLine + "\n" 
                                    + fileBlockOne.toString() 
                                    + lineSixSb.toString()
                                    + fileBlockTwo.toString());
                        }else{
                            fileWriter.write(ecbBakup);
                        }

                        ecbWritten = ecbWritten.add(BigInteger.ONE);
                        resetECB();
                        
                    }                   

                    fileWriter.close();
                    //fileWriterControl.close();
                    br.close();
                    
                    File movedFile = new File(PathECBSalida + fileName + "ORIGINAL_" + timeStamp + filesExtension);
                    if (FormateaECBPampaController.moveFile(inputFile, movedFile)) {// mover archivo original
                        // renombrar archivo generado
                        if (FormateaECBPampaController.moveFile(outputFile,
                                new File(PathECBEntrada + fileName + filesExtension))) {
                            result = true;
                        } else {
                            System.out.println("No se pudo renombrar el archivo generado");
                            result = false;
                        }
                    } else {
                        System.out.println("No se pudo mover el archivo original");
                        result = false;
                    }

                } else {
                    System.out.println("No se encontro el archivo de entrada: " + PathECBEntrada + fileName + filesExtension);
                    result = false;
                }
                if (!result) {
                    File delete = new File(PathECBEntrada + "GENERATED_" + fileName + filesExtension);
                    if (delete.exists()) {
                        delete.delete();
                    }
                }
                return result;
            } catch (Exception e) {
                File delete = new File(PathECBEntrada + "GENERATED_" + fileName + filesExtension);
                if (delete.exists()) {
                    delete.delete();
                }
                e.printStackTrace();
                System.out.println("Exception formateaECBCarter:" + e.getMessage());
                return false;
            }
        }
	private StringBuilder processSixLines(List<String> sixLines, BigDecimal subTotalOriginal, BigDecimal subTotalNuevo){
		StringBuilder result = new StringBuilder();
		
		for(String line : sixLines){
			String[] lineArray = line.split("\\|");
			String newLine = line;
			if(listContains(carterConceptList, lineArray[1])){
				
				BigDecimal importeOriginal = new BigDecimal(lineArray[2]);
				
				
				MathContext mathC = MathContext.DECIMAL64;
				BigDecimal division = importeOriginal.divide(subTotalOriginal, mathC);
				
				BigDecimal nuevoImporte = subTotalNuevo.multiply(division);
				
				nuevoImporte = 	nuevoImporte.setScale(2, BigDecimal.ROUND_HALF_EVEN);
				
				BigDecimal montoExento = importeOriginal.subtract(nuevoImporte);
				montoExento=montoExento.setScale(2, BigDecimal.ROUND_HALF_EVEN);
				
				newLine = "06|"
						+ lineArray[1] + "|"
						+ nuevoImporte.toString() + "\n"
						+"06|"
						+ lineArray[1] + " EXENTO" + "|"
						+ montoExento.toString();
			}
			result.append(newLine);
			result.append("\n");
		}
		return result;
	}

	private void resetECB() {
		fileBlockOne = new StringBuilder();
		fileBlockTwo = new StringBuilder();

		newTotalMn = BigDecimal.ZERO;
		totalMnOriginal = BigDecimal.ZERO;
		
		totalConceptsA = BigDecimal.ZERO;
		ivaA = BigDecimal.ZERO;
		tasa = BigDecimal.ZERO;
		ivaMnOriginal = BigDecimal.ZERO;
		ivaB = BigDecimal.ZERO;
		montoConceptosGrav = BigDecimal.ZERO;

		lineSixSb = new StringBuilder();
		lineSixList = new ArrayList<String>();
	}

	private void loadCarterConceptList() throws Exception {
		FileInputStream fis = new FileInputStream(PathECBCatalogos + carterConceptsFileName);
		DataInputStream dis = new DataInputStream(fis);
		BufferedReader bfr = new BufferedReader(new InputStreamReader(dis, "UTF-8"));
		String carterConceptLine = null;
		carterConceptList = new ArrayList<String>();

		while ((carterConceptLine = bfr.readLine()) != null) {
			if(!carterConceptLine.trim().isEmpty())
				carterConceptList.add(carterConceptLine.trim());
		}
		bfr.close();
	}
	
	private boolean listContains(List<String> list, String value){
		for(String val : list){
			if (val.equalsIgnoreCase(value)) return true;
		}
		return false;
	}
}
