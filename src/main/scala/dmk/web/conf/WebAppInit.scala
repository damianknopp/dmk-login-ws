package dmk.web.conf

import java.util.Set
import org.eclipse.jetty.servlets.DoSFilter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.servlet.ServletContainerInitializer
import javax.servlet.ServletContext
import javax.servlet.FilterRegistration


class WebAppInit extends ServletContainerInitializer{
  val logger: Logger = LoggerFactory.getLogger(classOf[WebAppInit])

  def onStartup(c: Set[Class[_]], ctx: ServletContext): Unit = {
    logger.debug("webappinit")
    val names = ctx.getServletNames()
    while(names.hasMoreElements()){
    	logger.debug(names.nextElement())
    }
    
    val fReg: FilterRegistration.Dynamic = ctx.addFilter("DoSFilter", classOf[org.eclipse.jetty.servlets.DoSFilter])
    fReg.setInitParameter("maxRequestsPerSec", "10")

  }
  
}
