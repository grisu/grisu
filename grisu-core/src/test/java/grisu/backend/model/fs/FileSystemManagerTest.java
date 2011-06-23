package grisu.backend.model.fs;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.frontend.control.clientexceptions.FileTransactionException;
import grisu.frontend.control.login.LoginManager;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.MountPoint;
import grisu.model.UserEnvironmentManager;
import grisu.model.dto.DtoStringList;
import grith.jgrith.plainProxy.LocalProxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileSystemManagerTest {

	public static final String TEST_TARGET_DIR = "unit_tests";

	public static final File TEST_DIR_INPUT = new File(
			System.getProperty("java.io.tmpdir"), "grisu-tests-input");
	public static final File TEST_DIR_OUTPUT = new File(
			System.getProperty("java.io.tmpdir"), "grisu-tests-output");

	public static final String TEST_FILE_0_NAME = "test0.txt";

	public static final File INPUT_TEST_FILE_0 = new File(TEST_DIR_INPUT,
			TEST_FILE_0_NAME);

	public static final File OUTPUT_TEST_FILE_0 = new File(TEST_DIR_OUTPUT,
			TEST_FILE_0_NAME);

	private static final String alias = "BeSTGRID";
	// private static final String alias = "Local";
	private static final String vo = "/nz/nesi";

	private static ServiceInterface si = null;

	private static FileManager fm = null;
	private static UserEnvironmentManager uem = null;

	private static MountPoint mp = null;
	private static MountPoint mp2 = null;

	private static String targetDirUrl = null;
	private static String targetFileUrl = null;

	private static String targetDirUrl2 = null;
	private static String targetFileUrl2 = null;

	public static void deleteTestFiles() throws RemoteFileSystemException {
		fm.deleteFile(targetFileUrl);
		fm.deleteFile(targetFileUrl2);
	}

	public static void downloadAndAssert(String url) {

		try {
			fm.downloadUrl(url, TEST_DIR_OUTPUT, true);

			assertTrue(FileUtils.contentEquals(INPUT_TEST_FILE_0,
					OUTPUT_TEST_FILE_0));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		System.out.println("Copying input files...");
		if (!TEST_DIR_INPUT.exists()) {
			TEST_DIR_INPUT.mkdirs();
		}

		InputStream in = FileSystemManagerTest.class.getResourceAsStream("/"
				+ TEST_FILE_0_NAME);
		IOUtils.copy(in,
				new FileOutputStream(
						FileSystemManagerTest.INPUT_TEST_FILE_0));
		in.close();

		System.out.println("Creating proxy...");

		String dir = System.getProperty("user.home") + File.separator
				+ ".globus" + File.separator;
		LocalProxy.gridProxyInit(dir + "testcert.pem", dir + "testkey.pem",
				"".toCharArray(),
				12);

		System.out.println("Logging in...");
		si = LoginManager.login(alias);

		fm = GrisuRegistryManager.getDefault(si).getFileManager();
		uem = GrisuRegistryManager.getDefault(si).getUserEnvironmentManager();

		MountPoint[] mps = uem.getMountPoints();

		mp = mps[0];
		mp2 = mps[1];

		targetDirUrl = mp.getRootUrl() + "/" + TEST_TARGET_DIR;
		targetFileUrl = targetDirUrl + "/" + TEST_FILE_0_NAME;

		targetDirUrl2 = mp2.getRootUrl() + "/" + TEST_TARGET_DIR;
		targetFileUrl2 = targetDirUrl2 + "/" + TEST_FILE_0_NAME;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		System.out.println("Delete files...");
		deleteTestFiles();
		FileUtils.deleteQuietly(OUTPUT_TEST_FILE_0);

		System.out.println("Logging out...");
		System.out.println(si.logout());

		System.out.println("Deleting input files...");
		FileUtils.deleteDirectory(TEST_DIR_INPUT);

	}

	public static void uploadTo(String targetFileUrl) throws RemoteFileSystemException {
		DataHandler dh = FileManager.createDataHandler(INPUT_TEST_FILE_0);
		si.upload(dh, targetFileUrl);
	}

	@Before
	public void setUp() throws Exception {
		deleteTestFiles();
		FileUtils.deleteQuietly(OUTPUT_TEST_FILE_0);
	}

	@Test
	public void testCopyFile() throws RemoteFileSystemException,
	FileTransactionException {

		uploadTo(targetFileUrl);

		si.cp(DtoStringList.fromSingleString(targetFileUrl), targetDirUrl2,
				false, true);

		downloadAndAssert(targetFileUrl2);

	}

	@Test
	public void testCreateFolder() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteFile() {
		fail("Not yet implemented");
	}

	@Test
	public void testDownload() {
		fail("Not yet implemented");
	}

	@Test
	public void testFileExists() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFileSize() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFolderListing() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetInputStream() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetOutputStream() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsFolder() {
		fail("Not yet implemented");
	}

	@Test
	public void testLastModified() {
		fail("Not yet implemented");
	}

	@Test
	public void testMountFileSystem() {
		fail("Not yet implemented");
	}

	@Test
	public void testResolveFileSystemHomeDirectory() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpload() throws RemoteFileSystemException, IOException,
	FileTransactionException {

		DataHandler dh = FileManager.createDataHandler(INPUT_TEST_FILE_0);
		si.upload(dh, targetFileUrl);

		downloadAndAssert(targetFileUrl);

	}

	@Test
	public void testUploadFileToMultipleLocations() {
		fail("Not yet implemented");
	}

}
