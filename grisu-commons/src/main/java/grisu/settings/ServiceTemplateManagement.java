package grisu.settings;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * Helps to manage all available jsdl templates that are stored in
 * $HOME/.grisu/templates for a grisu client or
 * $HOME_OF_TOMCAT_USER/.grisu/templates_available.
 * 
 * @author Markus Binsteiner
 * 
 */
public final class ServiceTemplateManagement {

	static final Logger myLogger = LoggerFactory
			.getLogger(ServiceTemplateManagement.class);

	/**
	 * Checks the $HOME_OF_TOMCAT_USER/.grisu/templates_available directories
	 * for xml files and returns a list of all of the filenames (without
	 * .xml-extension).
	 * 
	 * @return the list of all available application templates
	 */
	public static String[] getAllAvailableApplications() {

//        final File[] templates = new File(
//                Environment.getAvailableTemplatesDirectory()).listFiles();

        final Collection<File> templates = getAllTemplateFiles();

        final Set<String> allAvalableTemplates = new TreeSet<String>();

        if (templates == null) {
            return new String[]{};
        }

        for (final File file : templates) {
            if (file.getName().endsWith(".template")) {
                allAvalableTemplates.add(file.getName().substring(0,
                        file.getName().lastIndexOf(".template")));
            }
        }

        return allAvalableTemplates.toArray(new String[allAvalableTemplates
                .size()]);
    }

    public static Collection<File> getAllTemplateFiles() {
        final Collection<File> templates = FileUtils.listFiles(new File(Environment.getAvailableTemplatesDirectory()),new String[]{".template"}, true);
        return templates;
    }

	public static String getTemplate(String name) {

        if (StringUtils.isBlank(name)) {
            return null;
        }
        File templateFile = null;
        for (File tf : getAllTemplateFiles() ) {
            if (name.equals(FilenameUtils.getBaseName(tf.getAbsolutePath()))) {
                templateFile = tf;
                break;
            }
        }

        if ( templateFile == null ){
            return null;
        }


		String temp;
		try {
			temp = FileUtils.readFileToString(templateFile);
		} catch (final IOException e) {
			return null;
		}

		return temp;
	}

	// /**
	// * Loads the jsdl template from the .grisu/templates_available directory
	// * into a {@link Document}.
	// *
	// * @param name
	// * the name of the jsdl template
	// * @return the template as xml Document or null if it could not be
	// * found/loaded
	// */
	// public static Document getAvailableTemplate(final String name) {
	//
	// Document jsdl_template = null;
	// try {
	// jsdl_template = SeveralXMLHelpers.fromString(FileHelpers
	// .readFromFile(new File(Environment
	// .getAvailableTemplatesDirectory()
	// + File.separator + name + ".xml")));
	// } catch (Exception e) {
	// myLogger
	// .error("Could not find/load jsdl template for application: "
	// + name + ": " + e.getMessage());
	// return null;
	// }
	// return jsdl_template;
	// }

	private ServiceTemplateManagement() {
	}

}
