package org.vpac.grisu.client.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.aegis.AegisContext;
import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.vpac.grisu.client.control.cxf.ClientAuthInterceptor;
import org.vpac.grisu.control.CXFServiceInterface;
import org.vpac.grisu.control.Environment;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.ServiceInterfaceCreator;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;



public class CXFServiceInterfaceCreator implements ServiceInterfaceCreator{

	public static String TRUST_FILE_NAME = Environment.GRISU_DIRECTORY + File.separator + "truststore.jks";

	public ServiceInterface create(String interfaceUrl,
				       String username, char[] password, String myProxyServer, String myProxyPort, Object[] otherOptions) throws ServiceInterfaceException{
		return create(interfaceUrl, username, new String(password), myProxyServer, Integer.parseInt(myProxyPort));
		
	}

	/** 
	    configures secure connection parameters.
	 **/
	public CXFServiceInterfaceCreator() throws ServiceInterfaceException{
		try {
			if (!(new File(Environment.GRISU_DIRECTORY, "truststore.jks").exists())){
				InputStream ts = CXFServiceInterfaceCreator.class.getResourceAsStream("/truststore.jks");
				IOUtils.copy(ts, new FileOutputStream(TRUST_FILE_NAME));
			}
		}
		catch (IOException ex){
			throw new ServiceInterfaceException("cannot copy SSL certificate store into grisu home directory. Does "+
							    Environment.GRISU_DIRECTORY+ " exist?",ex);
		}
		System.setProperty("javax.net.ssl.trustStore", TRUST_FILE_NAME);
	}

	public  ServiceInterface create(String serviceInterfaceUrl,
						String username, String password, 
						String myproxyServer, int myproxyPort){
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		AegisDatabinding aDB = new AegisDatabinding();
		AegisContext acontext = new AegisContext();
		HashSet<java.lang.Class<?>> classes = new HashSet<java.lang.Class<?>>();
		classes.add(DocumentImpl.class);
		acontext.setRootClasses(classes);
		HashMap<Class<?>, String> classMap = new HashMap<Class<?>, String>();
		classMap.put(DocumentImpl.class,"org.w3c.dom.Document");
		acontext.setBeanImplementationMap(classMap);
		acontext.setReadXsiTypes(true);
		acontext.setWriteXsiTypes(true);

		aDB.setAegisContext(acontext);


		factory.setServiceClass(CXFServiceInterface.class);
		factory.setAddress(serviceInterfaceUrl);
		factory.getServiceFactory().setDataBinding(aDB);

		factory.setServiceName(new QName("http://serviceInterfaces.control.grisu.vpac.org/","grisu"));
		factory.setWsdlLocation(serviceInterfaceUrl +"?wsdl");

		factory.getInInterceptors().add(new LoggingInInterceptor());
		factory.getOutInterceptors().add(new LoggingOutInterceptor());
		factory.getOutInterceptors().add(new ClientAuthInterceptor(username,password,
								      myproxyServer,myproxyPort));
		ServiceInterface service = (ServiceInterface)factory.create();
		/* Service s = aDB.getService();
		
		   s.put("org.w3c.dom.Document.implementation", "org.apache.xerces.dom.DocumentImpl"); */
		return service;
	} 

        public boolean canHandleUrl(String url) {
                if (url != null) {
                        return url.startsWith("http");
                } else {
                        return false;
                }
        }

	/*	public static void main(String[] args) throws Exception{
		ServiceInterface service = create("http://globus.ceres.auckland.ac.nz/grisu-cxf-1.0.0/services/grisu",
						  "yhal003","secret","myproxy.arcs.org.au",443);
		
		String[] sls = service.getAllSubmissionLocations();
		System.out.println(service.calculateRelativeJobDirectory("freak1"));
		 for (String sl:sls){
			 System.out.println(sl);
		 }
		 System.out.println(service.ls("gsiftp://ng2.auckland.ac.nz/home/grid-bestgrid/C_NZ_O_BeSTGRID_OU_The_University_of_Auckland_CN_Yuriy_Halytskyy",1,true));
		 service.login("yhal003","".toCharArray());

		 } */
}