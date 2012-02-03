package grisu.model.dto;

import grisu.grin.model.resources.Directory;
import grisu.grin.model.resources.Queue;
import grisu.jcommons.interfaces.InformationManager;
import grisu.model.MountPoint;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A wrapper that holds a list of {@link DtoSubmissionLocationInfo} objects.
 * 
 * @author Markus Binsteiner
 * 
 */
@XmlRootElement(name = "submissionlocations")
public class DtoSubmissionLocations {

	private static boolean checkWhetherSubLocIsActuallyAvailable(
			InformationManager im, Collection<MountPoint> mps, String subLoc) {

		final Set<Directory> filesystems = im
				.getStagingFileSystemForSubmissionLocation(subLoc);

		for (final MountPoint mp : mps) {

			for (final Directory fs : filesystems) {
				if (mp.getRootUrl()
						.startsWith(fs.getUrl().replace(":2811", ""))) {
					return true;
				}
			}

		}

		return false;

	}

	public static DtoSubmissionLocations createSubmissionLocationsInfo(
			Collection<String> submissionLocations) {

		final DtoSubmissionLocations result = new DtoSubmissionLocations();

		final List<DtoSubmissionLocationInfo> subLocs = new LinkedList<DtoSubmissionLocationInfo>();
		for (final String subLoc : submissionLocations) {
			final DtoSubmissionLocationInfo temp = new DtoSubmissionLocationInfo();
			temp.setSubmissionLocation(subLoc);
			subLocs.add(temp);
		}

		result.setAllSubmissionLocations(subLocs);

		return result;
	}

	public static DtoSubmissionLocations createSubmissionLocationsInfo(
			String[] submissionLocations) {

		final DtoSubmissionLocations result = new DtoSubmissionLocations();

		final List<DtoSubmissionLocationInfo> subLocs = new LinkedList<DtoSubmissionLocationInfo>();
		for (final String subLoc : submissionLocations) {
			final DtoSubmissionLocationInfo temp = new DtoSubmissionLocationInfo();
			temp.setSubmissionLocation(subLoc);
			subLocs.add(temp);
		}

		result.setAllSubmissionLocations(subLocs);

		return result;

	}

	public static DtoSubmissionLocations createSubmissionLocationsInfoFromQueues(
			Collection<Queue> queues) {

		final DtoSubmissionLocations result = new DtoSubmissionLocations();

		final List<DtoSubmissionLocationInfo> subLocs = new LinkedList<DtoSubmissionLocationInfo>();
		for (final Queue q : queues) {
			final DtoSubmissionLocationInfo temp = new DtoSubmissionLocationInfo();
			temp.setSubmissionLocation(q.toString());
			subLocs.add(temp);
		}

		result.setAllSubmissionLocations(subLocs);

		return result;
	}

	/**
	 * The list of submission location objects.
	 */
	private List<DtoSubmissionLocationInfo> allSubmissionLocations = new LinkedList<DtoSubmissionLocationInfo>();

	public String[] asSubmissionLocationStrings() {

		final String[] result = new String[allSubmissionLocations.size()];

		for (int i = 0; i < result.length; i++) {
			result[i] = getAllSubmissionLocations().get(i)
					.getSubmissionLocation();
		}
		return result;
	}

	@XmlElement(name = "submissionlocation")
	public List<DtoSubmissionLocationInfo> getAllSubmissionLocations() {
		return allSubmissionLocations;
	}

	public void removeUnuseableSubmissionLocations(InformationManager im,
			Collection<MountPoint> mps) {

		final Iterator<DtoSubmissionLocationInfo> i = allSubmissionLocations
				.iterator();
		while (i.hasNext()) {
			if (!checkWhetherSubLocIsActuallyAvailable(im, mps, i.next()
					.getSubmissionLocation())) {
				i.remove();
			}
		}

	}

	public void setAllSubmissionLocations(
			List<DtoSubmissionLocationInfo> allSubmissionLocations) {
		this.allSubmissionLocations = allSubmissionLocations;
	}

}
