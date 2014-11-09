package dmk.web.rest

import java.io.FileNotFoundException
import java.util.Properties
import scala.collection.JavaConversions._
import scala.xml.Elem
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import dmk.deusexmachina.service.PortalLoginServiceImpl
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/nc/login/status")
class LoginStatusResource{

  @Path("/user/{name}/hello")
  @GET
  @Produces(Array(MediaType.APPLICATION_JSON))
  def sayHello(@PathParam("name") name: String): String = {
    return "hello " + name
  }
  
  @Path("/user/{name}")
  @GET
  @Produces(Array(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON))
  def checkLoginStatus(@PathParam("name") name: String): String = {
    if(name.length() > 40){
      throw new IllegalArgumentException("name too long")
    }
    
    val safeName = name.trim()  
    val homePage = LoginStatusResource.portalLoginService.loginHomePage()
    val namesToCheck = Array(name)
    val notLoggedIn = LoginStatusResource.portalLoginService.checkUsersStatus(homePage, namesToCheck)
//    val notLoggedIn = namesToCheck.intersect(users)
    val loggedIn = namesToCheck.diff(notLoggedIn)
    namesToCheck.map(println _)
    loggedIn.map(println _)

    val msg = 
 <Response>
		{ loggedIn.map(n => <status>{n} might be logged in today</status>) }
		{ notLoggedIn.map(n => <status>{n} is not logged in today</status>) }
</Response>
				return msg.toString()
  }
  
//  @Path("/user/{name}/update")
//  @POST
//  @Produces(Array(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON))
//  def updateLoginStatus(@PathParam("name") name: String): String = {
//  }
}

object LoginStatusResource{
  val logger: Logger = LoggerFactory.getLogger(classOf[LoginStatusResource])

  lazy val portalLoginService = initPortalLoginService()
  
  def initPortalLoginService() = {
    val service = new PortalLoginServiceImpl()
    val envConf : EnvironmentStringPBEConfig =  new EnvironmentStringPBEConfig()
		envConf.setAlgorithm("PBEWithMD5AndDES")
		val envName = "DEUSEXMACHINA_ENCRYPTION_PASSWORD"
		var pass = System.getProperty(envName)
		envConf.setPasswordCharArray(pass.toCharArray())
		System.setProperty(envName, "")
		pass = null
		val standardPBEStringEncryptor = new StandardPBEStringEncryptor()
		standardPBEStringEncryptor.setConfig(envConf)
		
		val props = new Properties()
    val propFileName = "dmk-login-ws.properties"
    var inputStream = getClass().getClassLoader().getResourceAsStream(propFileName)
    if(inputStream == null){
      inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propFileName)
      if(inputStream == null)
        inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(propFileName)
      if(inputStream == null)
        throw new FileNotFoundException(f"property file $propFileName was not found!")
      
    }
    props.load(inputStream)
    
    val portalUser = props.getProperty("dmk.service.portal.login.portalUser")
    var portalPass = props.getProperty("dmk.service.portal.login.portalPass")
    if( portalPass.startsWith("ENC(") && portalPass.endsWith(")") ){
      portalPass = portalPass.substring(4, portalPass.length - 1)
    }
    portalPass = standardPBEStringEncryptor.decrypt(portalPass)
    
    service.setUser(portalUser)
    service.setPass(portalPass)
    service
  }
}