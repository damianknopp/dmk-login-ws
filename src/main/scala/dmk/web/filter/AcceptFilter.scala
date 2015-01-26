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
class AcceptFilter extends Filter {
  val logger = LoggerFactory.getLogger(classOf[AcceptFilter])

  def init(filterConfig: FilterConfig): Unit = {
    logger.debug("AcceptFilter init")
  }

  def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain): Unit = {
    logger.debug("inspecting for Accept param")
    val accept = req.getParameter("accept")
    if (accept != null && !accept.isEmpty()) {
      logger.debug("wrapping request for accept param = " + accept)
      val httpReq: HttpServletRequest = req.asInstanceOf[HttpServletRequest]
      val reqWrapper = new HttpServletRequestWrapper(httpReq){
        override def getHeader(name: String): String = {
            logger.debug("inspecting header " + name)
            if("accept".equalsIgnoreCase(name)){
              val param = this.getParameter(name)
              if(param != null && !param.isEmpty()){
                return param
              }
            }
            return super.getHeader(name)
          }
        }
      return chain.doFilter(reqWrapper, res)
    }
    return chain.doFilter(req, res)
  }

  def destroy(): Unit = {
  }
}