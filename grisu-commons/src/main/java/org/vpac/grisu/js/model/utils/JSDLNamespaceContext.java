

package org.vpac.grisu.js.model.utils;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;



/**
 * A helper class to parse the jsdl document properly.
 * 
 * @author Markus Binsteiner
 *
 */
public class JSDLNamespaceContext implements NamespaceContext {
	  
	  public JSDLNamespaceContext () {

	  }
	  
	  public String getNamespaceURI (String prefix) {
	    if (prefix.equals("jsdl")) {
	      return "http://schemas.ggf.org/jsdl/2005/11/jsdl";
	    }
	    else if ( prefix.equals("jsdl-posix")) {
	    	return "http://schemas.ggf.org/jsdl/2005/11/jsdl-posix";
	    }
	    else if (prefix.equals(XMLConstants.XML_NS_PREFIX)) {
	      return XMLConstants.XML_NS_URI;
	    }
	    else if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
	      return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
	    }
	    else {
	      return XMLConstants.NULL_NS_URI;
	    }
	  }
	  
	  public String getPrefix (String namespaceURI) {
	    if (namespaceURI.equals("http://schemas.ggf.org/jsdl/2005/11/jsdl")) {
	      return "jsdl";
	    }
	    else if (namespaceURI.equals("http://schemas.ggf.org/jsdl/2005/11/jsdl-posix")) {
	    	return "jsdl-posix";
	    }
	    else if (namespaceURI.equals(XMLConstants.XML_NS_URI)) {
	      return XMLConstants.XML_NS_PREFIX;
	    }
	    else if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
	      return XMLConstants.XMLNS_ATTRIBUTE;
	    }
	    else {
	      return null;
	    }
	  }
	  
	  public Iterator getPrefixes (String namespaceURI) {
	    // not implemented for the example
	    return null;
	  }
	  
	}

