package grisu.frontend.view.swing.files;

import grisu.model.files.GlazedFile;

import java.util.List;


import ca.odell.glazedlists.TextFilterator;

public class GrisuFileFilter implements TextFilterator<GlazedFile> {

	public void getFilterStrings(List<String> baseList, GlazedFile file) {

		baseList.add(file.getName());

	}

}
