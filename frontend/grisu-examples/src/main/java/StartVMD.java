import java.io.IOException;


public class StartVMD {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String vmdPath = "/home/markus/local/opt/vmd/vmd";
		String dcdArgument = "test";
		String psfArgument = "test";
		
		try {
			Runtime.getRuntime().exec(
					new String[] {vmdPath,
							"-dcd",
							"\"" + dcdArgument + "\"",
							"-psf",
							"\"" + psfArgument + "\"" });
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
