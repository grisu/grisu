package org.vpac.grisu.frontend.view.swing.files;

import java.util.Comparator;

public class GlazedFileComparator implements Comparator<GlazedFile> {

	public int compare(GlazedFile arg0, GlazedFile arg1) {
		
		if ( arg0.isMarkedAsParent() ) {
			return -1;
		}
		
		if ( arg0.getType().equals(arg1.getType()) ) {
			return arg0.getName().compareToIgnoreCase(arg1.getName());
		} else {
			
			return arg0.getType().compareTo(arg1.getType());
		}
		
	}
	


}
