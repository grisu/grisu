package grisu.backend.utils;

import grisu.jcommons.utils.JsdlHelpers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Just some helper methods to calculate/modify jsdl files.
 * 
 * @author markus
 * 
 */
public final class JsdlModifier {

	static final Logger myLogger = LoggerFactory.getLogger(JsdlModifier.class
			.getName());

	/**
	 * Calculates a new jsdl description with re-calculated mountpoint urls with
	 * urls that are relative to "real" filesystems on grid resources. For
	 * example, if you've got a mountpoint
	 * "gsiftp://ng2.vpac.org/home/grid-admin/<your-dn>" which is the used for
	 * the job testjob that has got the jobdirectory
	 * "gsiftp://ng2.vpac.org/home/grid-admin/<your-dn>/grisu-job-dir/testjob"
	 * then your userexecutionhostfs would be
	 * "gsiftp://ng2.vpac.org/home/grid-admin/<your-dn>" but the working
	 * directory for your job would actually be "<your-dn>/grisu-jobs/testjob"
	 * because it is relative to the filesystem (in our case to the
	 * homedirectory of the local user which is used to run the job.
	 * 
	 * I know, this is all very confusing. Took me ages to write that part...
	 * 
	 * @param jsdl_old
	 *            the original jsdl description
	 * @param clusterRootUrl
	 *            the root of the cluster filesystem (in the above example, that
	 *            would be "gsiftp://ng2.vpac.org/home/grid-admin")
	 * @param absolutePath
	 *            whether the filesystem is specified absolute or not
	 * @return the new jsdl description
	 * @throws Exception
	 */
	public static Document recalculateFileSystems(final Document jsdl_old,
			final String clusterRootUrl, final boolean absolutePath)
			throws Exception {

		final Document jsdl_new = (Document) jsdl_old.cloneNode(true);

		final Element userFSElement = JsdlHelpers
				.getUserExecutionHostFSElement(jsdl_new);
		final String executionHostFileSystemUrl = JsdlHelpers
				.getUserExecutionHostFs(jsdl_new);

		if (!executionHostFileSystemUrl.startsWith(clusterRootUrl)) {
			throw new Exception(
					"Can't exchange user with local execution filesystem: not the same fs roots:\n"
							+ "executionHostFs: " + executionHostFileSystemUrl
							+ "\nclusterRootUrl: " + clusterRootUrl);
		}

		final Element clusterFSElement = JsdlHelpers
				.addOrRetrieveExistingFileSystemElement(jsdl_new,
						JsdlHelpers.LOCAL_EXECUTION_HOST_FILESYSTEM,
						clusterRootUrl);

		final List<Element> elementsWithURLs = JsdlHelpers
				.getElementsWithFileSystemNameAttribute(jsdl_new,
						JsdlHelpers.USER_EXECUTION_HOST_FILESYSTEM);

		for (final Element elementWithURL : elementsWithURLs) {

			myLogger.debug("Changing element: " + elementWithURL.getTagName());

			String url = null;
			if (executionHostFileSystemUrl.endsWith("/")) {
				url = executionHostFileSystemUrl
						+ elementWithURL.getTextContent();
			} else {
				url = executionHostFileSystemUrl + "/"
						+ elementWithURL.getTextContent();
			}

			// probably redundant
			if (!url.startsWith(clusterRootUrl)) {
				throw new Exception(
						"Can't exchange user with local execution filesystem: not the same fs roots.");
			}

			String relativeOrAbsoluteUrl = null;
			if (clusterRootUrl.endsWith("/")) {
				relativeOrAbsoluteUrl = url.substring(clusterRootUrl.length());
			} else {
				relativeOrAbsoluteUrl = url
						.substring(clusterRootUrl.length() + 1);
			}
			if (absolutePath) {
				elementWithURL.setTextContent("/" + relativeOrAbsoluteUrl);
			} else {
				elementWithURL.setTextContent(relativeOrAbsoluteUrl);
			}
			elementWithURL.setAttribute("filesystemName",
					JsdlHelpers.LOCAL_EXECUTION_HOST_FILESYSTEM);

		}

		// TODO change stagein elements

		return jsdl_new;
	}

	private JsdlModifier() {
	}

}
