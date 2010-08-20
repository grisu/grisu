package org.vpac.grisu.frontend.view.swing.files;

import java.util.List;

import org.vpac.grisu.model.files.GlazedFile;

import ca.odell.glazedlists.TextFilterator;

public class GrisuFileFilter implements TextFilterator<GlazedFile> {

	public void getFilterStrings(List<String> baseList, GlazedFile file) {

		baseList.add(file.getName());

	}

}
