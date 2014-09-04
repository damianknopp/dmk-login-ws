package dmk.web.conf

import javax.servlet.annotation.WebListener
import javax.servlet.ServletContextListener
import javax.servlet.ServletContextEvent
import org.slf4j.LoggerFactory
import org.slf4j.Logger

@WebListener 
class TestServletContextListener extends ServletContextListener { 
  val logger: Logger = LoggerFactory.getLogger(classOf[TestServletContextListener])

  def contextInitialized(sce: ServletContextEvent): Unit = { 
    logger.debug("contextIntialized")
//		  sce.getServletContext().addServlet(servletName, servlet)
  } 

  def contextDestroyed(sce: ServletContextEvent): Unit = { 
	logger.debug("contextDestoryed")
  } 
}