package dmk.web.conf

import org.glassfish.jersey.server.ResourceConfig
import javax.ws.rs.ApplicationPath
import org.slf4j.LoggerFactory
import dmk.web.provider.JacksonMessageProvider
import org.glassfish.jersey.jackson.JacksonFeature


@ApplicationPath("/*")
class JaxRsInit extends ResourceConfig{
	val logger = LoggerFactory.getLogger(classOf[JaxRsInit])
	logger.debug("new @ApplicationPath")

	packages("dmk.web.rest;dmk.web.conf")
	register(classOf[JacksonFeature])
	register(classOf[JacksonMessageProvider])
}