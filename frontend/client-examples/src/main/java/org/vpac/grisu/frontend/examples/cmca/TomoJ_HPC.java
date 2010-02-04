package org.vpac.grisu.frontend.examples.cmca;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.MountPoint;
import org.vpac.security.light.plainProxy.LocalProxy;

import au.org.arcs.jcommons.configuration.CommonArcsProperties;




public class TomoJ_HPC extends JPanel
{
	private static JTabbedPane pane;
	private static JPanel tomoPane, jobPane, login, recon;
	private static TextField user, sangle, iangle,nangle, iter, coeff, thick;
	private static JPasswordField pass;
	private static JComboBox idp, mode;
	private static JButton loginB, loginRB, brw1, brw2, submit, clear, loginA;
	//private static JLabel in, out;
	private static String in,out;
	private static JCheckBox norm, align, resin;
	private static TextArea status;
	private static Box bot;
	private static float[] para;

	private static ServiceInterface si;
	private static int mp;
	final private static String DATALOC = "ngdata.ivec.org";
	private static File angleFile, javaFile, shellFile;
	//final private static String javaName = "tomoj.js";
	//final private static String shellName = "image.sh";
	final private static String wDir = System.getProperty("user.dir");
	final private static String fSep = System.getProperty("file.separator");
	private static FileManager fm;
	private static JobObject job;


	private static boolean loggedIn = false;
	private static String grisuPath, subPath, jobName;

	private static boolean buildPath()
	{
		List<MountPoint> listMP = si.df().getMountpoints();
		if (listMP.size()==0)
		{
			status.append("No VO for this user. Apply for one before you log in.\n");
			return false;
		}
		else
		{
			for (int i=0; i<listMP.size();i++)
			{
				String tempS = listMP.get(i).getAlias();
				//System.out.println(tempS);
				if (tempS.indexOf(DATALOC)!=-1)
				{
					mp = i;
					i = listMP.size();
				}
			}
			grisuPath = listMP.get(mp).getRootUrl() + "/imagej/";
			subPath = grisuPath.substring(grisuPath.indexOf(DATALOC)+DATALOC.length());
			status.append("Full data path: " + grisuPath +"\n");
			status.append("Internal path: " + subPath +"\n");
			return true;
		}
	}

	//-------------------------------- BUILDING SCRIPTS -----------------------
	private static void buildScripts()
	{
		createangle();
		status.append ("--> Angel file saved as angle.txt\n");
		createJava();
		status.append ("--> JavaScript file saved as tomoj.js\n");
		createShell();
		status.append ("--> Shell file saved as imagej.sh\n");

	}

	private static void createangle()
	{
		angleFile = new File(wDir + fSep + "angle.txt");
		if (angleFile.exists()) {
			angleFile.delete();
		}
		try
		{
			if (angleFile.createNewFile())
			{
				BufferedWriter wr = new BufferedWriter (new FileWriter (angleFile, true));
				for (float i=0.0f; i<para[2]; i++)
				{
					//System.out.print(para[0]);
					wr.write(""+para[0]);
					wr.newLine();
					para[0]=para[0] + para[1];
				}
				wr.flush();
				wr.close();
			}
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(TomoJ_HPC.recon, "Cant create angle file");
		}

	}

	//-------------------------------------- LOG IN FUNCTIONS -------------------------

	private static void createJava()
	{
		javaFile = new File(wDir + fSep + "tomoj.js");
		if (javaFile.exists()) {
			javaFile.delete();
		}
		try
		{
			if (javaFile.createNewFile())
			{
				BufferedWriter wr = new BufferedWriter (new FileWriter (javaFile, true));

				wr.write("IJ.runPlugIn(\"TomoJ_\",\"");
				if (!norm.isSelected()) {
					wr.write("nonormalisation ");
				}
				if (align.isSelected()) {
					wr.write("automaticalignment ");
				}
				wr.write ("loadangles angle.txt " + mode.getSelectedItem().toString() + " " + (int) para[3] + " " + para[4] + " " + (int) para[5]);
				if (resin.isSelected()) {
					wr.write (" 1\");");
				} else {
					wr.write (" 0\");");
				}
				wr.newLine();
				wr.write("IJ.doCommand(\"Quit\");");
				wr.newLine();
				wr.flush();
				wr.close();
			}
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(TomoJ_HPC.recon, "Cant create javascript file");
		}
	}

	//----------------------------CREATE JOBS --------------
	private static void createJob()
	{
		job = new JobObject (si);
		job.setUniqueJobname("ImageJ");
		jobName = job.getJobname();

		job.setApplication("ImageJ");

		job.setSubmissionLocation("normal:ng2.ivec.org");

		// set the commandline that needs to be executed
		job.setCommandline("sh " + subPath + "imagej.sh");

		try
		{
			job.createJob("/ARCS/CMCA");
			job.submitJob();
			status.append ("Job submission done.\n");
		}
		catch (Exception e)
		{
			status.append ("Submit job falied.\n");
			JOptionPane.showMessageDialog(TomoJ_HPC.recon, "Error creating job");
		}
	}

	//---------------------------------MAIN SCRIPTS ------------------------
	private static void createPaneAndShow()
	{
		JFrame frame = new JFrame("TomoJ HPC v1.0");

		TomoJ_HPC tomo = new TomoJ_HPC();
		tomo.setOpaque(true);
		frame.setContentPane (tomo);
		frame.setSize(1100,550);
		frame.setVisible(true);
	}

	private static void createShell()
	{
		shellFile = new File(wDir + fSep + "imagej.sh");
		if (shellFile.exists()) {
			shellFile.delete();
		}
		try
		{
			if (shellFile.createNewFile())
			{
				BufferedWriter wr = new BufferedWriter (new FileWriter (shellFile, true));
				wr.write("Xvfb :16 &");
				wr.newLine();
				wr.write("export DISPLAY=:16");
				wr.newLine();
				wr.write("module load ImageJ");
				wr.newLine();
				wr.write("cp "+subPath + "angle.txt .");
				wr.newLine();
				wr.write("cp "+subPath + "tomoj.js .");
				wr.newLine();
				wr.write("cp "+subPath + "imagej.sh .");
				wr.newLine();
				wr.write("ImageJ " +subPath + in.substring(in.lastIndexOf(fSep.charAt(0))+1) + " -macro tomoj.js");
				wr.newLine();
				wr.flush();
				wr.close();
			}
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(TomoJ_HPC.recon, "Cant create shell file");
		}
	}

	private static boolean isFloat(TextField c)
	{
		String pattern = new String ("^[0123456789]{1,10}\\.[0123456789]{1,10}$");
		c.setText(c.getText().trim());
		if (c.getText().matches(pattern)) {
			return true;
		} else {
			return false;
		}
	}
	private static boolean isInt(TextField c)
	{
		String pattern = new String ("^[0123456789]{1,10}$");
		c.setText(c.getText().trim());
		if (c.getText().matches(pattern)) {
			return true;
		} else {
			return false;
		}
	}

	public static void main (String[] args)
	{

		createPaneAndShow();
		preLogin();
	}

	private static void postLogin()
	{
		int temp = login.getComponentCount();
		for (int i=0; i<temp;i++)
		{
			login.getComponent(i).setEnabled(false);
		}
		//login.setEnabled (false);
		status.append("User has logged in.\n");
		if (buildPath())
		{
			loggedIn = true;
			pass.setText("");
			recon.setVisible(true);
			bot.setVisible(true);
		}

	}

	private static void preLogin()
	{
		LoginManager.initEnvironment();
		try{
			user.setText(CommonArcsProperties.getDefault().getLastShibUsername());
			if (LocalProxy.validGridProxyExists(120))
			{
				status.append("Current proxy exists. ");
				status.append("Logging in.....\n");
				si = LoginManager.login();
				postLogin();
			}
		}
		catch (Exception le)
		{
			status.append("Failed to login. Check your connection, username and password. \n");
		}
	}

	//--------------------------------UPLOADING FILES TO SERVER ----------------------------------
	private static void uploadFiles()
	{
		fm = new FileManager (si);

		try
		{
			File tempF = new File (wDir + fSep + "angle.txt");
			//System.out.println(grisuPath);
			//System.out.println(wDir + fSep + "angle.txt");
			fm.uploadFileToDirectory(tempF,grisuPath,true);
			status.append ("--> angel.txt uploaded.\n");

			tempF = new File (wDir + fSep + "tomoj.js");
			//fm.uploadFileToDirectory(wDir + fSep + "tomoj.js",grisuPath,true);
			fm.uploadFileToDirectory(tempF,grisuPath,true);
			status.append ("--> tomoj.js uploaded.\n");

			tempF  = new File (wDir + fSep + "imagej.sh");
			//fm.uploadFileToDirectory(wDir + fSep + "imagej.sh",grisuPath,true);
			fm.uploadFileToDirectory(tempF,grisuPath,true);
			status.append ("--> imagej.sh uploaded.\n");

			tempF = new File (in);
			//fm.uploadFileToDirectory(in,grisuPath,true);
			fm.uploadFileToDirectory(tempF,grisuPath,true);
			status.append ("--> Input file uploaded.\n");
			status.append ("Upload done.\n");
		}
		catch (Exception e)
		{
			System.out.print (e);
			status.append ("Cancel upload.\n");
			JOptionPane.showMessageDialog(TomoJ_HPC.recon, "Error uploading files");
		}
	}

	//------------------------------------ VERIFY FUNCTIONS ----------------------
	private static boolean verifyParameter()
	{
		if ((in==null) || (out ==null) || (sangle.getText()=="") || (nangle.getText()=="") || (iangle.getText()=="") || (iter.getText()=="") || (coeff.getText()=="") || (thick.getText()=="")) {
			return false;
		}
		para = new float [6];
		//verify float values
		if (isFloat(sangle) && isFloat(iangle) && isFloat(coeff))
		{
			if (isInt (nangle) && isInt (iter)  && isInt(thick))
			{
				try
				{
					para[0] = Float.parseFloat(sangle.getText());
					para[1] = Float.parseFloat(iangle.getText());
					para[2] = Float.parseFloat(nangle.getText());
					para[3] = Float.parseFloat(iter.getText());
					para[4] = Float.parseFloat(coeff.getText());
					para[5] = Float.parseFloat(thick.getText());
					return true;
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(TomoJ_HPC.recon,"Failed to convert parameters to float");
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	private final FileNameExtensionFilter filter = new FileNameExtensionFilter("TIF Images","tif");

	public TomoJ_HPC()
	{
		super (new BorderLayout());
		pane = new JTabbedPane();

		//tab panes
		tomoPane = new JPanel();
		jobPane = new JPanel();


		//----------------------- PANES belong to TOMOJ pane -----------

		//********* Log In pane
		login = createPanel("Log In");
		//login.setLayout(new BoxLayout(login,BoxLayout.PAGE_AXIS));
		login.setLayout(new GridLayout(4,2));

		login.add (new JLabel("Idp",JLabel.LEFT));
		String[] listId = {"iVEC","Others"};
		idp = new JComboBox (listId);
		login.add(idp);

		login.add(new JLabel("UserName",JLabel.LEFT));
		user = new TextField(7);
		login.add(user);

		login.add(new JLabel("Password", JLabel.LEFT));
		pass = new JPasswordField(7);
		login.add(pass);

		loginB = new JButton ("Log in");
		//loginB.setHorizontalAlignment(JButton.RIGHT);
		login.add(loginB);
		loginB.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				//System.out.print("Test");
				login(true);
				//status.append("Logging in.....\n");
			}
		});

		loginRB = new JButton ("Reset");
		//loginRB.setHorizontalAlignment(JButton.LEFT);
		login.add(loginRB);


		//*************** TomoJ Reconstruction Pane
		recon = createPanel ("Reconstruction options");
		recon.setLayout(new BoxLayout(recon,BoxLayout.X_AXIS));


		//~~~~~~~~~~~~~~~~ TomoJ pane left
		JPanel reconLeft = createPanel("Misc");
		reconLeft.setLayout (new GridLayout(7,2));

		reconLeft.add(new JLabel("Input *",JLabel.LEFT));
		brw1 = new JButton ("Browse");
		reconLeft.add(brw1);
		brw1.addActionListener (new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fileChoose = new JFileChooser();
				fileChoose.setFileFilter(filter);
				int returnVal = fileChoose.showOpenDialog(TomoJ_HPC.recon);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File tempF = fileChoose.getSelectedFile();
					if (verifyFile (tempF))
					{
						in = tempF.getAbsolutePath();
						status.append("Input file: ");
						status.append(tempF.getAbsolutePath() + "\n");
					} else {
						JOptionPane.showMessageDialog (TomoJ_HPC.recon,"Input file is invalid.");
					}
				}
			}
		});

		reconLeft.add(new JLabel("Output *",JLabel.LEFT));
		brw2 = new JButton ("Browse");
		reconLeft.add(brw2);
		brw2.addActionListener (new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fileChoose = new JFileChooser();
				//fileChoose.setFileFilter(filter);
				fileChoose.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fileChoose.showOpenDialog(TomoJ_HPC.recon);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File tempF = fileChoose.getSelectedFile();
					if (verifyDir (tempF))
					{
						out = tempF.getAbsolutePath();
						status.append("Output dir: ");
						status.append(tempF.getAbsolutePath() + "\n");
					} else {
						JOptionPane.showMessageDialog (TomoJ_HPC.recon,"Output dir is invalid.");
					}
				}
			}
		});

		//in = new JLabel();
		//in.setVisible(false);
		//reconLeft.add(in);

		//out = new JLabel();
		//out.setVisible(false);
		//reconLeft.add(out);

		norm = new JCheckBox ("Normailisation");
		reconLeft.add(norm);

		align = new JCheckBox ("Auto. Alignment");
		reconLeft.add(align);

		reconLeft.add(new JLabel("Start angle *", JLabel.LEFT));
		sangle = new TextField("0.0", 5);
		reconLeft.add(sangle);

		reconLeft.add(new JLabel("Increment angle *",JLabel.LEFT));
		iangle = new TextField("1.0",5);
		reconLeft.add(iangle);

		reconLeft.add(new JLabel("No of Images *",JLabel.LEFT));
		nangle = new TextField("1",5);
		reconLeft.add(nangle);


		//~~~~~~~~~~~~~~~~~ TomoJ pane right
		JPanel reconRight = createPanel("Reconstruction");
		reconRight.setLayout(new GridLayout(5,2));

		reconRight.add(new JLabel("Algorithm",JLabel.LEFT));
		String[] listAl = {"sirt","art"};
		mode = new JComboBox(listAl);
		reconRight.add(mode);

		reconRight.add(new JLabel("No. Iteration *", JLabel.LEFT));
		iter = new TextField("0",5);
		reconRight.add(iter);

		reconRight.add(new JLabel("Relax. Coeff *",JLabel.LEFT));
		coeff = new TextField ("0.0",5);
		reconRight.add(coeff);

		reconRight.add(new JLabel("Volume Thicknes *",JLabel.LEFT));
		thick = new TextField("0.0",5);
		reconRight.add(thick);

		resin = new JCheckBox("ResinOrCryo");
		reconRight.add(resin);

		reconRight.add (new JLabel("Fields marked with * are compulsory", JLabel.CENTER));


		//add left and right panels to recon panel
		recon.add(reconLeft);
		recon.add(reconRight);


		//***************** Bottom panel containing submit button and status box
		bot = Box.createHorizontalBox();

		submit = new JButton("Submit");
		clear = new JButton("Clear");
		bot.setSize(800,10);
		bot.add(submit);
		submit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				if (verifyParameter())
				{
					status.append ("Building scripts....\n");
					buildScripts();

					status.append ("Uploading Files to HPC....\n");
					uploadFiles();

					status.append ("Create job .....\n");
					createJob();
					//status.append("Job has been submitted.\n");

				}
				else
				{
					JOptionPane.showMessageDialog(TomoJ_HPC.recon,"Some parameters missing.");
				}
			}
		});

		bot.add(clear);

		status = new TextArea ("",10,80,TextArea.SCROLLBARS_BOTH);
		status.setEditable(false);

		//status.setLineWrap(true);

		//set visible to FALSE
		recon.setVisible(false);
		bot.setVisible(false);

		//add panes to TOMOpane
		tomoPane.add(login);
		tomoPane.add(recon);
		tomoPane.add(bot);
		tomoPane.add(status);

		//add tabs to pane
		pane.addTab("TomoJ Options", tomoPane);
		pane.addTab("Job Monitoring",jobPane);

		//add tabbed pane to TomoJ
		add (pane, BorderLayout.CENTER);


	}


	private JPanel createPanel (String title)
	{
		JPanel temp = new JPanel();
		temp.setBorder(BorderFactory.createTitledBorder(title));
		//temp.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
		return temp;

	}



	private void login(boolean save)
	{
		if (user.getText()=="") {
			JOptionPane.showMessageDialog(this,"Check username");
		} else if (pass.getPassword().length==0) {
			JOptionPane.showMessageDialog(this,"Check password");
		} else
		{
			status.append("Logging in....\n");
			try
			{
				si = LoginManager.shiblogin(user.getText(), pass.getPassword(), idp.getSelectedItem().toString(), save);
				postLogin();
			}
			catch (Exception e)
			{
				status.append("Failed to login. Check your connection, username and password. \n");
			}
		}
	}


	private boolean verifyDir(File inF)
	{
		if (!inF.exists() || !inF.isDirectory()) {
			return false;
		} else {
			return true;
		}
	}

	//-------------------------------------- FILE FUNCTIONS -------------------
	private boolean verifyFile(File inF)
	{
		if (!inF.exists() || !inF.isFile()) {
			return false;
		} else {
			return true;
		}
	}
}