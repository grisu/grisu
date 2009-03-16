package org.vpac.grisu.client.model.template.modules;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.eventStuff.SubmissionObjectListener;
import org.vpac.grisu.client.control.exceptions.SubmissionLocationException;
import org.vpac.grisu.client.control.utils.FqanEvent;
import org.vpac.grisu.client.control.utils.FqanListener;
import org.vpac.grisu.client.control.utils.MountPointEvent;
import org.vpac.grisu.client.control.utils.MountPointsListener;
import org.vpac.grisu.client.model.ApplicationObject;
import org.vpac.grisu.client.model.NoMDSApplicationObject;
import org.vpac.grisu.client.model.SubmissionLocation;
import org.vpac.grisu.client.model.SubmissionObject;
import org.vpac.grisu.client.model.VersionObject;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.nodes.DefaultTemplateNodeValueSetter;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeValueSetter;
import org.vpac.grisu.control.JobCreationException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.js.model.utils.JsdlHelpers;

/**
 * @author Markus Binsteiner
 *
 */
public class CommonWithMemory extends Common implements FqanListener, MountPointsListener, SubmissionObjectHolder {

	static final Logger myLogger = Logger.getLogger(Common.class.getName());
	
	public static final String MEMORY_NAME = "MinMem";
	
	public static final String[] MODULES_USED_MEMORY = new String[] { JOBNAME_NAME,
		WALLTIME_NAME, CPUS_NAME, HOSTNAME_NAME, EXECUTIONFILESYSTEM_NAME,
		EMAIL_ADDRESS_NAME, MODULE_NAME, MEMORY_NAME };
	
	private DefaultTemplateNodeValueSetter memorySetter = new DefaultTemplateNodeValueSetter();

	public CommonWithMemory(JsdlTemplate template) {
		super(template);
	}
	
	public void initializeTemplateNodes(Map<String, TemplateNode> templateNodes) {
		
		initValueSetters(templateNodes);
		
		templateNodes.get(MEMORY_NAME).setTemplateNodeValueSetter(memorySetter);
		
	}
	
	public void setMemory(long memoryinbytes) {
		String memory = new Long(memoryinbytes).toString();
		templateNodes.get(MEMORY_NAME).getTemplateNodeValueSetter()
				.setExternalSetValue(memory);
	}
	
	public String[] getTemplateNodeNamesThisModuleClaimsResponsibleFor() {
		return MODULES_USED_MEMORY;
	}
}
