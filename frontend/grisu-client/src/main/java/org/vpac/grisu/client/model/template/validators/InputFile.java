package org.vpac.grisu.client.model.template.validators;

import java.net.URISyntaxException;

import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;

public class InputFile extends TemplateNodeValidator {

	public InputFile(TemplateNode node) {
		super(node);
	}
	
	@Override
	public void validate() throws TemplateValidateException {

		
		try {
			String multiplicity = templateNode.getMultiplicity();

			if ( templateNode.getValue() == null || "".equals(templateNode.getValue()) || "dummyfile".equals(templateNode.getValue()) ) {
				if ( multiplicity == null || multiplicity.equals("1") ) {
					throw new TemplateValidateException("No input file specified.");
				} else {
					return;
				}
			}

			
			GrisuFileObject file = templateNode.getTemplate().getEnvironmentManager().getFileManager().getFileObject(templateNode.getValue());
			if ( file == null ) {
				throw new TemplateValidateException("Input file does not exist."); 
			}
			if ( file.exists() ) {
				return;
			} else {
				throw new TemplateValidateException("Input file does not exist."); 
			}
		} catch (URISyntaxException e) {
			throw new TemplateValidateException("Problem retrieving file object: "+e);
		}

		
	}

}
