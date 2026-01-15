package br.com.iatapp.config.init;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import br.com.iatapp.helper.DataHelper;


@WebListener
public class AppListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent servletInit) {
		String appInitDateTime = DataHelper.getCurrentLocalDateTimeStamp();
		System.out.println("Ottap Stater Template init at: " + appInitDateTime);
		ServletContext servletContext = servletInit.getServletContext();
		servletContext.setAttribute("AppInitDateTime", appInitDateTime);
		servletContext.setAttribute("AppCurrentYear", DataHelper.getCurrentYear());
		
		// session timeout listener
		servletContext.addListener(new SessionListener());
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		String appDestroyDateTime = DataHelper.getCurrentLocalDateTimeStamp();
		System.out.println("Ottap Stater Template destroy at: " + appDestroyDateTime);
	}
	
}
