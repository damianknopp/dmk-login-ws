package dmk.web.filter

import org.slf4j.LoggerFactory
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

@WebFilter(urlPatterns = Array("/*"))
class LoggerFilter extends Filter {
  val logger = LoggerFactory.getLogger(classOf[LoggerFilter])

  def init(filterConfig: FilterConfig): Unit = {
    logger.debug("LoggerFilter init")
  }

  def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain): Unit = {
    logger.trace("-- LoggerFilter inspecting request params:")
    val paramNames = req.getParameterNames()
    while(paramNames.hasMoreElements()){
      val ele = paramNames.nextElement()
      logger.trace(ele)
    }
    chain.doFilter(req, res)
  }

  def destroy(): Unit = {
  }
}