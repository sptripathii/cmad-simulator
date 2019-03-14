package cmad.project.simulator;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import cmad.project.simulator.service.SyslogSimulatorService;

/**
 * Hello world!
 *
 */
public class App {
	private static Map<String, String> simulatorConfigurationMap = new HashMap<>();

	private static File syslogFile;

	public static void main(String[] args) {
		try {

			File f = new File("src/main/resources/simulator-configuration.properties");

			if (f.exists() && f.isFile()) {
				loadPropertyFile(f);
			} else {
				System.out.println("Invalid file path .Existing...");
				return;
			}
			syslogFile = new File("src/main/resources/syslog.txt");

			SyslogSimulatorService service = new SyslogSimulatorService();
			service.writeSyslogMessagesToDB(simulatorConfigurationMap, syslogFile);
		} catch (Exception exe) {
			System.out.println("Errror encounted");
			exe.printStackTrace();

		}

	}

	private static void loadPropertyFile(File propertyFile) throws Exception {
		Properties prop = new Properties();
		prop.load(new FileInputStream(propertyFile));

		for (Object key : prop.keySet()) {
			simulatorConfigurationMap.put((String) key, prop.getProperty((String) key));
		}

	}
}
