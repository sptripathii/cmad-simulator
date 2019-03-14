package cmad.project.simulator.util;

import java.text.MessageFormat;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import cmad.project.simulator.entity.SyslogMessages;


public class SyslogHibernateUtil {
	
	private static SessionFactory sessionFactory;
	
	public static void setSessionFactory(Map<String,String> properties) {
		
		String url = MessageFormat.format(properties.get("connection.url"), properties.get("connection.dbname"));
		Configuration conf = new Configuration().configure();
		conf.getProperties().setProperty("hibernate.connection.url", url);
		conf.getProperties().setProperty("hibernate.connection.username", properties.get("connection.username"));
		conf.getProperties().setProperty("hibernate.connection.password", properties.get("connection.password"));
		conf.getProperties().setProperty("hibernate.connection.driver_class", properties.get("connection.driver_class"));
		
	    sessionFactory = conf.addAnnotatedClass(SyslogMessages.class).buildSessionFactory();
		
		
	}
	
	public static Session getSession() {
		return sessionFactory.getCurrentSession();
	}

}
