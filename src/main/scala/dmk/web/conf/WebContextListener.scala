package dmk.web.conf

import javax.servlet.annotation.WebListener
import javax.servlet.ServletContextListener
import javax.servlet.ServletContextEvent
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import java.util.logging.LogManager
import org.slf4j.bridge.SLF4JBridgeHandler

@WebListener 
class TestServletContextListener extends ServletContextListener { 
  val logger: Logger = LoggerFactory.getLogger(classOf[TestServletContextListener])

  def contextInitialized(sce: ServletContextEvent): Unit = { 
    logger.debug("contextIntialized")
    TestServletContextListener.registerJulToSlfBridge()
  } 

  def contextDestroyed(sce: ServletContextEvent): Unit = { 
	  logger.debug("contextDestoryed")
  } 
}

object TestServletContextListener{
  
  //jul to slf4j bridge because of jersey
  def registerJulToSlfBridge(){ 
    LogManager.getLogManager().reset();
	  SLF4JBridgeHandler.install();
  }
	  
}