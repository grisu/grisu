

package org.vpac.grisu.js.model.utils;

import java.util.ArrayList;

import org.w3c.dom.Element;

public class ElementsNotValidException extends Exception {

	public ArrayList<Element> invalidElements = null;
	
	public ElementsNotValidException(ArrayList<Element> invalidElements) {
		super("Some elements are not valid input.");
		this.invalidElements = invalidElements;
	}

	public String getMessage() {
		StringBuffer message = new StringBuffer("Could not validate these elements:\n");
		
		for ( Element element : invalidElements ) {
			message.append("Element: "+element.getAttribute("title")+" of type: "+element.getAttribute("template")+" and value: "+element.getTextContent()+".\n");
		}
		
		return message.toString();
	}

	public ArrayList<Element> getInvalidElements() {
		return invalidElements;
	}


}
