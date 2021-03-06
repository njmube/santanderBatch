package com.interfactura.firmalocal.main;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.interfactura.firmalocal.controllers.MassiveDivisasDepuraController;

public class MassiveDivisasDepura {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try 
		{
			
			AbstractApplicationContext context = new ClassPathXmlApplicationContext("app-config.xml", MassiveDivisasDepura.class);
			BeanFactory factory = (BeanFactory) context;
			context.registerShutdownHook();
						
			MassiveDivisasDepuraController massiveDivisasDepura = (MassiveDivisasDepuraController) factory.getBean(MassiveDivisasDepuraController.class);
			String result = "";			
			massiveDivisasDepura.depura();
					
			context.close();
			System.out.println(result);
			System.out.println("Fin del procesamiento Identifica Peticiones Massive Report");
			System.exit(0);
		} 
		catch (Throwable e) 
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
