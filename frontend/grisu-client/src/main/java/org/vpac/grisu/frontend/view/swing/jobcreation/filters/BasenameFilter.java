package org.vpac.grisu.frontend.view.swing.jobcreation.filters;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.engine.FilterDefinition;
import org.vpac.grisu.utils.FileHelpers;

public class BasenameFilter implements Filter {

	public void config(String[] config) {
		// nothing to do
	}

	public String filter(String value, String input) {
		
		//TODO windows
		
		return FileHelpers.getFilename(value);
	}


}
