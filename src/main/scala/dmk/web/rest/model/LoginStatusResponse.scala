package dmk.web.rest.model

class LoginStatusResponse(var loggedIn: Array[String], var notLoggedIn: Array[String]) {
  
  def getLoggedIn():Array[String] = {
    this.loggedIn
  }
  
  def ArrayLoggedIn(s: Array[String]){
    this.loggedIn = s
  }
  
  def getNotLoggedIn():Array[String] = {
    this.notLoggedIn
  }
  
  def ArrayNotLoggedIn(s: Array[String]){
    this.notLoggedIn = s
  }
  
}