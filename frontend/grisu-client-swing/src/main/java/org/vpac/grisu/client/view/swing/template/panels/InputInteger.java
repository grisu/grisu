package org.vpac.grisu.client.view.swing.template.panels;

import javax.swing.JPanel;

public class InputInteger extends AbstractInputPanel {

	private String renderMode = null;
	
	/**
	 * Create the panel
	 */
	public InputInteger() {
		super();
		//
	}

	@Override
	protected void buttonPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String genericButtonText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ComponentHolder getComponentHolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void preparePanel() {
		try {
			renderMode = this.templateNode.getOtherProperties().get("render");
		} catch (RuntimeException e1) {
			// fallback
			renderMode = COMBOBOX_PANEL;
		}
		if (renderMode == null)
			renderMode = COMBOBOX_PANEL;
	}

	@Override
	protected void setupComponent() {
		// TODO Auto-generated method stub
		
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
