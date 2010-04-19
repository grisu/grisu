package org.vpac.grisu.frontend.view.swing.jobcreation.filters;

public class InputFileFilter implements Filter {
	
	private String pattern;
	
	public InputFileFilter() {
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.frontend.view.swing.jobcreation.filters.Filter#config(java.lang.String[])
	 */
	public void config(String[] config) {
		this.pattern = config[0];
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.frontend.view.swing.jobcreation.filters.Filter#filter(java.lang.String, java.lang.String)
	 */
	public String filter(String value, String changedFilename) {
		return value.replaceAll(pattern, changedFilename);
	}

}
