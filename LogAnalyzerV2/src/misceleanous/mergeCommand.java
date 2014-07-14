package misceleanous;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.lang3.SystemUtils;

public class mergeCommand {

	/*
	 * private static String OS = null;
	 * 
	 * public static String getOsName() { if (OS == null) { OS =
	 * System.getProperty("os.name"); } return OS; }
	 * 
	 * public static boolean isWindows() { return
	 * getOsName().toLowerCase().startsWith("windows"); }
	 * 
	 * public static boolean isLinux(){ return
	 * getOsName().toLowerCase().startsWith("linux"); }
	 */

	public static void main(String[] args) {

		executeCommand();

	}

	private static String executeCommand() {

		String winCommand = "ipconfig";
		String unixCommand = "cat GUI.* > mergedLogs.txt";

		Process process = null;

		StringBuffer output = new StringBuffer();

		try {
			if (SystemUtils.IS_OS_WINDOWS) {
				ProcessBuilder pb1 = new ProcessBuilder(winCommand);
				pb1.redirectErrorStream(true);
				process = pb1.start();

			} else {
				ProcessBuilder pb2 = new ProcessBuilder(unixCommand);
				pb2.redirectErrorStream(true);
				process = pb2.start();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				line = reader.readLine();
				System.out.println(line);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}
}