package cmad.project.simulator.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cmad.project.simulator.entity.SyslogMessages;
import cmad.project.simulator.util.SyslogHibernateUtil;

public class SyslogSimulatorService {

	public void writeSyslogMessagesToDB(Map<String, String> properties, File syslogFile) throws Exception {

		int retryCount = 0;
		boolean isConnected = false;
		Session session = null;
		while (!isConnected && retryCount < 5) {
			try {
				SyslogHibernateUtil.setSessionFactory(properties);
				session = SyslogHibernateUtil.getSession();
				isConnected = true;
			} catch (Exception exe) {
				if(retryCount >= 5) {
					throw new Exception("Uable to connect to mysql database even after retries. Exiting", exe);
				}
				retryCount++;
				System.out.println("Unable to connect to mysql for the retry count" + retryCount + "");
				Thread.sleep(5000);
			}
		}
		
		long interval = Long.parseLong(properties.get("syslog.event.write.interval"));
		int messagesCount = Integer.parseInt(properties.get("syslog.event.write.message.count"));
		String totalMessages = properties.get("syslog.event.write.total.messages.count");
		int intervalCount = 0;
		try {
			while (intervalCount * messagesCount <= Integer.parseInt(totalMessages)) {
				List<String> lines = readSyslogMessages(syslogFile, messagesCount);
				session = SyslogHibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				for (String readLine : lines) {
					String[] parsedStrings = readLine.split(":");
					if (parsedStrings.length != 3) {
						throw new Exception(
								"One/all Syslog messages are not in the expected format IP Address:LEVEL:Message");
					}
					SyslogMessages syslogMessage = new SyslogMessages();
					syslogMessage.setTimeStamp(System.currentTimeMillis());
					syslogMessage.setIpAddress(parsedStrings[0]);
					syslogMessage.setEventType(parsedStrings[1]);
					syslogMessage.setMessage(parsedStrings[2]);

					session.save(syslogMessage);
					
				}
				tx.commit();
				intervalCount++;
				Thread.sleep(interval * 1000);
			}
		} catch (Exception exe) {
			throw exe;

		} finally {
			session.close();
		}

	}

	private List<String> readSyslogMessages(File syslogFile, int messageCount) throws IOException {

		List<String> messages = new ArrayList<>();
		BufferedReader syslogFileReader = new BufferedReader(new FileReader(syslogFile));
		String line = null;
		while (messageCount > 0) {
			line = syslogFileReader.readLine();
			if (line == null) {
				syslogFileReader.close();
				syslogFileReader = new BufferedReader(new FileReader(syslogFile));

				continue;
			}
			if (line.startsWith("#")) {
				continue;
			}
			if (line.equals("")) {
				continue;
			}
			messages.add(line);

			messageCount--;

		}
		return messages;
	}

}
