import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.exceptions.JobSubmissionException;
import org.vpac.grisu.client.control.exceptions.SubmissionLocationException;
import org.vpac.grisu.client.control.generic.GenericJobWrapper;
import org.vpac.grisu.client.control.login.LoginHelpers;
import org.vpac.grisu.client.model.SubmissionLocation;
import org.vpac.grisu.client.model.login.LoginParams;

public class Vlad {

	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//
//		SimpleCommandlineClient scc = new SimpleCommandlineClient();
//		// TODO Auto-generated method stub
//		LoginParams loginParams = new LoginParams(
//				"https://ngportal.vpac.org/grisu-ws/services/grisu", null,
//				null, "myproxy.arcs.org.au", "7512");
//		ServiceInterface serviceInterface = null;
//		try {
//			char[] password = scc
//					.readPasswordInput("Please enter your myproxy password");
//			System.out.println("Making login...");
//			serviceInterface = LoginHelpers.login(password, loginParams);
//			System.out.println("Made login");
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			System.exit(1);
//		}
//
//		EnvironmentManager em = new EnvironmentManager(serviceInterface);
//		// EnvironmentManager em = scc.login();
//
//		// File file = new
//		// File("D:\\Java\\Programs\\JMolEditor-grisu\\baseTemplate.xml");
//		// Document doc = SeveralXMLHelpers.loadXMLFile(file);
//
//		GenericJobWrapper job = new GenericJobWrapper(em);
//		// GenericJobWrapper job = new GenericJobWrapper(em,doc);
//		// we need to know which application we want to run
//		job.initialize("java");
//
//		job.useMds(false);
//
//		// now we need to set a jobname. this will be the handle to access it
//		// later. the user has to choose
//		// a unique one, otherwise the jobsubmission will fail
//		// String jobname =
//		// scc.getUniqueJobname(job.getCurrentlyExistingJobnames());
//
//		Calendar cal = new GregorianCalendar();
//		// Get the components of the time
//		int hour12 = cal.get(Calendar.HOUR); // 0..11
//		int hour24 = cal.get(Calendar.HOUR_OF_DAY) * 1000 * 60 * 60; // 0..23
//		int min = cal.get(Calendar.MINUTE) * 1000 * 60; // 0..59
//		int sec = cal.get(Calendar.SECOND) * 1000; // 0..59
//		int ms = cal.get(Calendar.MILLISECOND);
//
//		String jobname = "test-" + String.valueOf(hour24 + min + sec + ms);
//		System.out.println("Job name: " + jobname);
//		job.setJobname(jobname);
//
//		// now ask which VO the user wants to use. the environment manager knows
//		// all about them.
//		String[] vos = em.getAvailableFqans();
//		// String vo = (String) scc.displayChoices("Please select the VO you
//		// want to use
//		// for this job:", vos);
//		System.out.print("Available VOs:");
//		for (int i = 0; i < vos.length; i++) {
//			System.out.print(" " + vos[i]);
//		}
//		System.out.println();
//		String vo = "None";
//		try {
//			job.setVO(vo);
//
//		} catch (JobCreationException e) {
//			// this only happens when the application is not set
//			scc
//					.print("For some reason the application could not be set successfully. Exiting...\n");
//			System.exit(1);
//
//		}
//
//		Set<String> sites = job.getCurrentlyAvailableSites();
//		Object[] s = sites.toArray();
//		System.out.print("Available Sites:");
//		for (int i = 0; i < s.length; i++) {
//			System.out.print(" " + s[i].toString());
//		}
//		System.out.print("\n");
//
//		// now we need to specify where we want to submit the job to. The list
//		// of possible submission
//		// location changes according to which VO is set.
//		SubmissionLocation[] possibleSubmissionLocations = job
//				.getSubmissionLocations().toArray(new SubmissionLocation[] {});
//		System.out.print("Submission locations:");
//		for (int i = 0; i < possibleSubmissionLocations.length; i++) {
//			System.out.print(" " + possibleSubmissionLocations[i].getLocation()
//					+ " " + possibleSubmissionLocations[i].getSite());
//		}
//		System.out.println();
//
//		// SubmissionLocation selectedSubmissionLocation = (SubmissionLocation)
//		// scc.displayChoices(
//		// "Please select where you want to submit the job to:",
//		// possibleSubmissionLocations);
//		SubmissionLocation selectedSubmissionLocation = new SubmissionLocation(
//				"normal:ng2.apac.edu.au", em);
//
//		try {
//			job.setSubmissionLocation(selectedSubmissionLocation);
//
//		} catch (SubmissionLocationException e) {
//			// submission location not available. this should not happen because
//			// we used a value that was
//			// returned to us earlier
//			scc
//					.print("Could not set submission location because it is not available for this application and fqan.\n");
//			System.exit(1);
//
//		} catch (JobCreationException e) {
//			// this only happens when the application is not set
//			scc
//					.print("For some reason the application could not be set successfully. Exiting...\n");
//			System.exit(1);
//		} catch (Exception ex) {
//			scc.print(ex.getMessage());
//			System.err.println(ex.getMessage());
//			System.exit(1);
//		}
//
//		// ok. now we need a few more values like cpus, walltime, email address
//		// int cpus = scc.readIntegerInput("Please specify the no of cpus you
//		// want to
//		// use: ");
//		int cpus = 1;
//		job.setNumberOfCpus(cpus);
//
//		// int walltime = scc.readIntegerInput("Please specify the walltime in
//		// seconds:
//		// ");
//		int walltime = 60;
//		job.setWalltimeInSeconds(walltime);
//
//		scc
//				.print("If you want to get notified when the job finishes, please enter your email address here.\n");
//		// String email = scc.readStringInput("Otherwise, just press enter: ");
//		String email = "vvv900@sf.anu.edu.au";
//		if (email != null && !"".equals(email)) {
//			job.setEmailAddress(email);
//		}
//
//		// --- Files to stage in
//
//		try {
//			job.addInputFile("/home/markus/alanine.sssh");
//			job.addInputFile("/home/markus/alanine.gjf");
//			// job.setStdout("stdout.txt");
//			// job.setStderr("stderr.txt");
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			System.exit(1);
//		}
//
//		// almost done. now we need to get the rest of the commandline
//		String commandline = null;
//		// commandline = scc.readStringInput("Please provide the commandline you
//		// want to
//		// run:\n");
//		commandline = "/bin/bash --login alanine.sh";
//
//		try {
//			job.setCommandLine(commandline);
//
//		} catch (JobCreationException e) {
//			// shouldn't happen
//			scc.print("Template not initialized. Exiting...\n");
//			System.exit(1);
//		}
//
//		// and specify a module (if needed)
//		String module = null;
//
//		// module = scc.readStringInput( "Please enter the module to load (or
//		// press
//		// enter if none): ");
//		// module = module.trim();
//		if (module != null && !"".equals(module)) {
//			try {
//				job.setModule(module);
//
//			} catch (JobCreationException e1) {
//				// shouldn't happen
//				scc.print("Job not initialized. Exiting...\n");
//				System.exit(1);
//
//			}
//		}
//		// now we would have to specify input files. I'll document that later
//		// basically, for local files, you do something like
//		// job.addInputFile("/home/markus/test.txt");
//
//		// start the submission. the submission has got it's own thread, so we
//		// need
//		// to connet a listener
//		job.addJsdlTemplateListener(scc);
//		try {
//			scc.print("Submitting job.");
//			job.submit();
//			scc.print("Submission started...");
//
//		} catch (JobSubmissionException e) {
//			scc.print("Couldn't submit job: " + e.getLocalizedMessage() + "\n");
//			scc.print("Exiting...\n");
//			System.exit(1);
//
//		}
////		job.waitForSubmissionToFinish();
//		scc.print("Submission finished.");
//
//	}

}
