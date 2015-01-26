package dmk.web.rest

import java.io.FileNotFoundException
import java.util.HashMap
import java.util.Properties
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import dmk.deusexmachina.service.PortalLoginServiceImpl
import dmk.web.rest.model.LoginStatusResponse
import javax.ws.rs.Consumes
import javax.ws.rs.FormParam
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MultivaluedMap

@Path("/nc/login/status")
class LoginStatusResource {
  val logger: Logger = LoggerFactory.getLogger(classOf[LoginStatusResource])

  @Path("/user/{name}/hello")
  @GET
  @Produces(Array(MediaType.APPLICATION_JSON))
  def sayHello(@PathParam("name") name: String): String = {
    Thread.sleep(1000L)
    return "hello " + name
  }

  @Path("/user/{name}")
  @GET
  @Produces(Array(MediaType.APPLICATION_JSON))
  def checkStatusForName(@PathParam("name") name: String): LoginStatusResponse = {
    if (name.length() > 40) {
      throw new IllegalArgumentException("name too long")
    }
    val safeName = name.trim()
    return this.checkLogin(Array(safeName))
  }
  
  @Path("/users")
  @POST
  @Consumes(Array(MediaType.APPLICATION_FORM_URLENCODED))
  @Produces(Array(MediaType.APPLICATION_JSON))
  def checkStatusForNames(@FormParam("names") userNames: String): LoginStatusResponse = {
    logger.debug(userNames)
    val userArray = userNames.split(",")
    val users = userArray.map{ (user: String) =>
      if (user.length() > 40) {
      	throw new IllegalArgumentException("name too long")
      }
      user.trim()
    }
    return this.checkLogin(users)
  }

  def checkLogin(namesToCheck: Array[String]): LoginStatusResponse = {
    val homePage = LoginStatusResource.portalLoginService.loginHomePage()
    val notLoggedIn = LoginStatusResource.portalLoginService.checkUsersStatus(homePage, namesToCheck)
//    val notLoggedIn = namesToCheck
    val notLoggedInUniq = notLoggedIn.toSet
    val loggedIn = namesToCheck.toSet.diff(notLoggedInUniq)
    logger.info("found loggedin " + loggedIn.toList)
    val resp = new LoginStatusResponse(loggedIn.toArray, notLoggedInUniq.toArray)
    resp
  }

  //  @Path("/user/{name}/update")
  //  @POST
  //  @Produces(Array(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON))
  //  def updateLoginStatus(@PathParam("name") name: String): String = {
  //  }
}

object LoginStatusResource {
  val logger: Logger = LoggerFactory.getLogger(classOf[LoginStatusResource])

  lazy val portalLoginService = initPortalLoginService()

  def initPortalLoginService() = {
    val service = new PortalLoginServiceImpl()
    val envConf: EnvironmentStringPBEConfig = new EnvironmentStringPBEConfig()
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
    if (inputStream == null) {
      inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propFileName)
      if (inputStream == null)
        inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(propFileName)
      if (inputStream == null)
        throw new FileNotFoundException(f"property file $propFileName was not found!")

    }
    props.load(inputStream)

    val portalUser = props.getProperty("dmk.service.portal.login.portalUser")
    var portalPass = props.getProperty("dmk.service.portal.login.portalPass")
    if (portalPass.startsWith("ENC(") && portalPass.endsWith(")")) {
      portalPass = portalPass.substring(4, portalPass.length - 1)
    }
    portalPass = standardPBEStringEncryptor.decrypt(portalPass)

    service.setUser(portalUser)
    service.setPass(portalPass)
    service
  }
}