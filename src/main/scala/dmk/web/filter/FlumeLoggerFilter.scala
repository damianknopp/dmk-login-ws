package dmk.web.filter

import java.nio.charset.Charset
import org.apache.flume.Event
import org.apache.flume.EventDeliveryException
import org.apache.flume.api.RpcClient
import org.apache.flume.api.RpcClientFactory
import org.apache.flume.event.EventBuilder
import org.slf4j.LoggerFactory
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import java.util.Properties

@WebFilter(urlPatterns = Array("/*"))
class FlumeLoggerFilter extends Filter {
  val logger = LoggerFactory.getLogger(classOf[FlumeLoggerFilter])

  val hostname = "127.0.0.1"
  val port = 41414
  var client: RpcClient = null
  
  // Setup properties for the load balancing
  val loadBalanceProps = new Properties()
  loadBalanceProps.put("client.type", "default_loadbalance")
  // List of hosts (space-separated list of user-chosen host aliases)
  loadBalanceProps.put("hosts", "h1 h2")
  // host/port pair for each host alias
  val host1 = "localhost:41414"
  val host2 = "localhost:41415"
  loadBalanceProps.put("hosts.h1", host1)
  loadBalanceProps.put("hosts.h2", host2)
  loadBalanceProps.put("host-selector", "round_robin")
  loadBalanceProps.put("backoff", "true")
  loadBalanceProps.put("maxBackoff", "10000") 
  // Defaults 0, which effectively, becomes 30000 ms
  
  def init(filterConfig: FilterConfig): Unit = {
    logger.debug("FlumeLoggerFilter init")
    
    client = RpcClientFactory.getInstance(loadBalanceProps)
  }

  def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain): Unit = {
    logger.debug("-- FlumeLoggerFilter inspecting request params:")

    val localIp = req.getLocalAddr()
    val localPort = req.getLocalPort()
    val srcIp = req.getRemoteAddr()
    val srcPort = req.getRemotePort()
    val protocol = req.getProtocol()
    val url = req.asInstanceOf[HttpServletRequest].getRequestURL()
    
    val delim = ","
    val sb = new StringBuilder()
    sb.append(localIp).append(delim).append(localPort).append(delim)
    sb.append(srcIp).append(delim).append(srcPort).append(delim)
    sb.append(protocol).append(delim).append(url)
    
    sendFlumeEvent(sb.toString())
    
    chain.doFilter(req, res)
  }

  
  def sendFlumeEvent(data: String): Unit = {
    val event: Event = EventBuilder.withBody(data, Charset.forName("UTF-8"));
    try {
      client.append(event);
    } catch {
      case ex: EventDeliveryException => {
        client.close()
        client = null
        client = RpcClientFactory.getDefaultInstance(hostname, port)
      }
     }
  }
  
  def destroy(): Unit = {
    client.close()
  }
}