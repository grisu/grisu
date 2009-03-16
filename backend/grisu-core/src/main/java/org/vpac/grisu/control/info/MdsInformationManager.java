package org.vpac.grisu.control.info;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.apache.log4j.Logger;

import au.edu.sapac.grid.mds.QueryClient;
import au.org.arcs.mds.GridInfoInterface;
import org.vpac.grisu.control.Environment;

/**
 * Mds information manager that can uses Gersons mds infosystems library (http://projects.arcs.org.au/trac/infosystems/) to
 * calculate stuff. It doesn't cache any thing but always asks the library.
 * 
 * @author markus
 * @deprecated Use {@link CachedMdsInformationManager} instead if possible
 */
public class MdsInformationManager implements InformationManager {
	
	static final Logger myLogger = Logger.getLogger(MdsInformationManager.class
			.getName());
	
	GridInfoInterface client = null;
	
	// cacheing stuff
	private String[] allGridSites = null;
	private Map<String, String> allHosts = null;
	private Map<String, String[]> allSubmissionQueuesPerApplication = new HashMap<String, String[]>();
//	private Map<String, String[]> 
	
	//private Map<String, String>
	
	public MdsInformationManager() {
		client = new QueryClient(Environment.getGrisuDirectory().toString());
	}
	
	public String[] getAllSites() {
		if ( allGridSites == null ) {
			allGridSites = client.getSitesOnGrid();
		}
		return allGridSites;
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.control.info.InformationManager#getSiteForHostOrUrl(java.lang.String)
	 */
	public String getSiteForHostOrUrl(String host_or_url) {

		return getAllHosts().get(host_or_url);
//		String site = client.getSiteForHost(getHost(host_or_url));
//		return site;
	}
	

	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.control.info.InformationManager#getAllSubmissionQueues(java.lang.String)
	 */
	public String[] getAllSubmissionLocationsForApplication(String application) {

		if ( allSubmissionQueuesPerApplication.get(application) == null ) {
			// get all sites with installed application/version
			String[] sites = client.getSitesWithCode(application);
			String[] subLocs = getContactStringsForSitesWithApplication(sites, application);
			allSubmissionQueuesPerApplication.put(application, subLocs);
		}
    	
    	return allSubmissionQueuesPerApplication.get(application);
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.control.info.InformationManager#getDataLocationsForVO(java.lang.String)
	 */
	public Map<String, String[]> getDataLocationsForVO(String fqan) {
		Map<String, String[]> dataLocationsMap = new HashMap<String, String[]>();
		
		
		/*String[] storageElements = client.getStorageElementsForSite(site);	
		if (storageElements != null) {
			for (int j = 0; j < storageElements.length; j++) {
				String[] gridFTPServers = client.getGridFTPServersForStorageElementAtSite
					(site, storageElements[j]);
				String[] dataDirs = client.getDataDir(site, storageElements[j], fqan);
				
				for (int k = 0; k < gridFTPServers.length; k++) {
					if (dataDirs != null && dataDirs.length > 0) {
						dataLocationsMap.put(gridFTPServers[k], dataDirs);
					}
				}
			}
		}*/
		
		
		// alternative way of searching dataLocations is..
		
		// get all the storage elements for VO
		// for each storage element
		//    get gridftp server
		//    get data directories
		
		
		// another alternative is to use java-xml binding (xmlbeans!)
		
		it.infn.cnaf.forge.glueschema.spec.v12.r2.StorageElementType[] storageElements = 
			((QueryClient)client).getStorageElementsForVO(fqan);
		if (storageElements != null) {
			for (int j = 0; j < storageElements.length; j++) {
				it.infn.cnaf.forge.glueschema.spec.v12.r2.SEAccessProtocolType[] accessProtocols = 
					storageElements[j].getAccessProtocolArray();
				
				
				// now get the storageAreas that VO fqan has access to
				it.infn.cnaf.forge.glueschema.spec.v12.r2.StorageAreaType[] allStorageAreas =
					storageElements[j].getStorageAreaArray();
				
				java.util.List<String> voStorageAreaList = 
					new java.util.ArrayList<String>();
				
				for (int k = 0; k < allStorageAreas.length; k++) {
					String[] aclTypes =
						allStorageAreas[k].getACL().getRuleArray();
					
					for (int l = 0; l < aclTypes.length; l++) {
						if (aclTypes[l].equalsIgnoreCase(fqan)) {
							voStorageAreaList.add(allStorageAreas[k].getPath());
						}
					}
				}
				
				for (int k = 0; k < accessProtocols.length; k++) {
					if (!voStorageAreaList.isEmpty()) {
						dataLocationsMap.put(accessProtocols[k].getEndpoint(), 
								(String[])voStorageAreaList.toArray(new String[0]));
					}
				}
				
			}
		}
		
		
		
		return dataLocationsMap;
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.control.info.InformationManager#getAllSubmissionQueues()
	 */
	public String[] getAllSubmissionLocations() {

    	// get all sites    	
    	String[] sites = client.getSitesOnGrid();
    	
    	return getContactStringsForSites(sites);
	}
	
	public String[] getAllSubmissionLocationsForVO(String fqan) {
		String[] sites = ((QueryClient)client).getSitesForVO(fqan);
		return getContactStringsForSites(sites, fqan);
	}

	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.control.info.InformationManager#getAllSubmissionQueues(java.lang.String, java.lang.String)
	 */
	public String[] getAllSubmissionLocations(String application, 
			String version) {
    	
    	// get all sites with installed application/version
    	String[] sites = client.getSitesWithAVersionOfACode(application, version);

    	return getContactStringsForSitesWithApplication(sites, application, version);
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.control.info.InformationManager#getVersionsOfApplicationOnSite(java.lang.String, java.lang.String)
	 */
	public String[] getVersionsOfApplicationOnSite(String application, String site) {
		
		return client.getVersionsOfCodeAtSite(site, application);
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.control.info.InformationManager#getAllApplicationsOnGrid()
	 */
	public String[] getAllApplicationsOnGrid() {
		return client.getCodesOnGrid();
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.control.info.InformationManager#getAllApplicationsAtSite(java.lang.String)
	 */
	public String[] getAllApplicationsAtSite(String site) {
		return client.getCodesAtSite(site);
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.control.info.InformationManager#getAllVersionsOfApplicationOnGrid(java.lang.String)
	 */
	public String[] getAllVersionsOfApplicationOnGrid(String application) {
		return client.getVersionsOfCodeOnGrid(application);
	}
	
	/* (non-Javadoc)
	 * @see org.vpac.grisu.control.info.InformationManager#getApplicationDetails(java.lang.String, java.lang.String, java.lang.String)
	 */
	public Map<String, String> getApplicationDetails(String application,
			String version, String site) {
		Map<String, String> codeDetails = new HashMap<String, String>();

		codeDetails.put("Module", client.getModuleNameOfCodeAtSite(
				site, application, version));
		codeDetails.put("SerialAvail", Boolean.toString(client.isSerialAvailForCodeAtSite(
				site, application, version)));
		codeDetails.put("ParallelAvail", Boolean.toString(client.isParallelAvailForCodeAtSite(
				site, application, version)));
		String[] executables = client.getExeNameOfCodeAtSite(
				site, application, version);
		StringBuffer exeStrBuff = new StringBuffer();
		for (int i = 0; i < executables.length; i++) {
			exeStrBuff.append(executables[i]);
			if (i < executables.length - 1)
				exeStrBuff.append(",");
		}				
		codeDetails.put("Executables", exeStrBuff.toString());
		return codeDetails;
	}
	
	public String[] getStagingFileSystemForSubmissionLocation(String subLoc) {
		// subLoc = queuename@cluster:contactstring#JobManager
		String queueName = subLoc.substring(0, subLoc.indexOf(":"));
		String contactString = "";
		if (subLoc.indexOf("#") > 0) {
			contactString = subLoc.substring(subLoc.indexOf(":") + 1, subLoc.indexOf("#"));
		}
		else {
			contactString = subLoc.substring(subLoc.indexOf(":") + 1);
		}
		
		// get site name for contact string
//		GridInfoInterface client = new QueryClient();
		String siteName = client.getSiteForHost(contactString);		
		
		String[] result = client.getGridFTPServersForQueueAtSite(siteName, queueName);
		return result;
	}

	
	
	private String[] getContactStringsForSitesWithApplication(String[] sites, 
			String application) {
		List<String> list = new ArrayList<String>();
		
		for (int i = 0; sites != null && i < sites.length; i++) {
    		
    		// once site names are found, find all the queues for each of the 
    		// sites
    		String[] queues = client.getQueueNamesForCodeAtSite(sites[i], application);
    		
    		// TODO: this method and the one below can probably be merged into one
    		
    		for (int j = 0; queues != null && j < queues.length; j++) {
    			
    			// once queues are found, find all the contact string for 
    			// all the queues
    			String[] contactStrings = 
    				client.getContactStringOfQueueAtSite(sites[i], queues[j]);
    			String jobManager = client.getJobManagerOfQueueAtSite(sites[i], queues[j]);
    			
    			for (int k = 0; contactStrings != null 
    					&& k < contactStrings.length; k++) {
    				String hostname = contactStrings[k].substring(
    						contactStrings[k].indexOf("https://") != 0 ? 0 : 8,
    								contactStrings[k].indexOf(":8443"));
        			if (jobManager != null) {
        				if (jobManager.toLowerCase().indexOf("pbs") < 0)        			
        					list.add(queues[j] + ":" + hostname + "#" + jobManager);
        				else
        					list.add(queues[j] + ":" + hostname);
        			}
        			
    			}    			
    		}
    	}
    	return list.toArray(new String[list.size()]);
	}
	
	private String[] getContactStringsForSitesWithApplication(String[] sites, 
			String application, String version) {
		List<String> list = new ArrayList<String>();
		
		for (int i = 0; sites != null && i < sites.length; i++) {
    		
    		// once site names are found, find all the queues for each of the 
    		// sites
    		String[] queues = client.getQueueNamesForCodeAtSite(sites[i], application, version);
    		
    		for (int j = 0; queues != null && j < queues.length; j++) {
    			
    			// once queues are found, find all the contact string for 
    			// all the queues
    			String[] contactStrings = 
    				client.getContactStringOfQueueAtSite(sites[i], queues[j]);
    			String jobManager = client.getJobManagerOfQueueAtSite(sites[i], queues[j]);
    			
    			for (int k = 0; contactStrings != null 
    					&& k < contactStrings.length; k++) {
    				String hostname = contactStrings[k].substring(
    						contactStrings[k].indexOf("https://") != 0 ? 0 : 8,
    								contactStrings[k].indexOf(":8443"));
        			if (jobManager != null) {
        				if (jobManager.toLowerCase().indexOf("pbs") < 0)        			
        					list.add(queues[j] + ":" + hostname + "#" + jobManager);
        				else
        					list.add(queues[j] + ":" + hostname);
        			}
        			
    			}    			
    		}
    	}
    	return list.toArray(new String[list.size()]);
	}
	
	private String[] getContactStringsForSites(String[] sites) {
		return getContactStringsForSites(sites, null);
		
	}
	
	private String[] getContactStringsForSites(String[] sites, String fqan) {

		List<String> list = new ArrayList<String>();
		
		for (int i = 0; sites != null && i < sites.length; i++) {
    		
    		// once site names are found, find all the queues for each of the 
    		// sites
    		String[] queues = null;
    		
    		if (fqan == null) {
    			queues = client.getQueueNamesAtSite(sites[i]);
    		} else {
    			queues = client.getQueueNamesAtSite(sites[i], fqan);
    		}
    		
    		for (int j = 0; queues != null && j < queues.length; j++) {
    			
    			// once queues are found, find all the contact string for 
    			// all the queues
    			String[] contactStrings = 
    				client.getContactStringOfQueueAtSite(sites[i], queues[j]);
    			String jobManager = client.getJobManagerOfQueueAtSite(sites[i], queues[j]);
    			
    			for (int k = 0; contactStrings != null 
    					&& k < contactStrings.length; k++) {
    				String hostname = contactStrings[k].substring(
    						contactStrings[k].indexOf("https://") != 0 ? 0 : 8,
    								contactStrings[k].indexOf(":8443"));
    				if (jobManager != null) {
        				if (jobManager.toLowerCase().indexOf("pbs") < 0)        			
        					list.add(queues[j] + ":" + hostname + "#" + jobManager);
        				else
        					list.add(queues[j] + ":" + hostname);
        			}
    			}    			
    		}
    	}
    	return list.toArray(new String[list.size()]);
	}
	
	private Set<String> getComputeHostsForSite(String site) {
		Set<String> set = new HashSet<String>();
		
		String[] queues = client.getQueueNamesAtSite(site);
		
		for (int j = 0; queues != null && j < queues.length; j++) {
			
			// once queues are found, find all the contact string for 
			// all the queues
			String[] contactStrings = 
				client.getContactStringOfQueueAtSite(site, queues[j]);
			
			for (int k = 0; contactStrings != null 
					&& k < contactStrings.length; k++) {
				// remove protocol and port number from the string
				String hostname = contactStrings[k].substring(
						contactStrings[k].indexOf("https://") != 0 ? 0 : 8,
								contactStrings[k].indexOf(":8443"));
				set.add(hostname);
			}    			
		}
		return set;	
	}
	
	private Set<String> getDataHostsForSite(String site) {
		Set<String> set = new HashSet<String>();
		
		String[] dataHosts = client.getGridFTPServersAtSite(site);
		
		for (int j = 0; dataHosts !=  null && j < dataHosts.length; j++) {
			
			// remove protocol and port number from the string
			String hostname = dataHosts[j].substring(
					dataHosts[j].indexOf("://")+3, dataHosts[j].lastIndexOf(":"));
			set.add(hostname);			
		}
		return set;		
	}
	
	private static String getHost(String host_or_url) {
		// dodgy, I know
		URI url;
		try {
			url = new URI(host_or_url);
			if ( url.getHost() != null ) {
				host_or_url = url.getHost();
			}
		} catch (URISyntaxException e) {
			// doesn't matter
		}

		return host_or_url;
	}

	public Map<String, String> getAllHosts() {
		
		if ( allHosts == null ) {
		
		myLogger.debug("Starting getting all hosts from mds");
		
		allHosts = new HashMap<String, String>();
		String[] sites = getAllSites();
		for (int i = 0; sites != null && i < sites.length; i++) {
			Set<String> hosts = getComputeHostsForSite(sites[i]);
			hosts.addAll(getDataHostsForSite(sites[i]));
			
			for (String host : hosts) {
				allHosts.put(host, sites[i]);				
			}
		}
		}
		myLogger.debug("Finished getting all hosts from mds");
		return allHosts;
	}

}
