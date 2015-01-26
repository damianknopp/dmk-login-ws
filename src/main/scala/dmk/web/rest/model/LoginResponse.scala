package dmk.web.rest.model

import javax.xml.bind.annotation.XmlRootElement

/**
 * @deprecated
 * I tried to case classes and marshall to json w/ jackson or even jaxb, but no luck
 * disregard
 * @see LoginStatusResponse
 * it has a better name and marshalls to json as it should
 */
@XmlRootElement
@Deprecated
case class LoginResponse(loggedIn: Array[String], notLoggedIn: Array[String]) {
}