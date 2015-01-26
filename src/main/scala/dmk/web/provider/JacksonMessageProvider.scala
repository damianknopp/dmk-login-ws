package dmk.web.provider

import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.map.SerializationConfig
import org.codehaus.jackson.map.SerializationConfig.Feature
import org.slf4j.LoggerFactory

import javax.ws.rs.Produces
import javax.ws.rs.ext.ContextResolver
import javax.ws.rs.ext.Provider


/**
 * @deprecated
 * I replaced for the default jackson marshallers found with 
 * <code>register(classOf[JacksonFeature])</code> in ResourceConfig
 * disregard this class
 */
@Deprecated
@Provider
class JacksonMessageProvider() extends ContextResolver[ObjectMapper] {
	var logger = LoggerFactory.getLogger(classOf[JacksonMessageProvider])
    var defaultObjectMapper: ObjectMapper = createDefaultMapper()
    var combinedObjectMapper: ObjectMapper = createCombinedObjectMapper()
    
    logger.debug("JacksonMessageProvider init")
 
    override def getContext(clazz: Class[_]): ObjectMapper = {
      logger.debug("getContext for " + clazz)
//        if (clazz == classOf[CombinedAnnotationBean]) {
//            return combinedObjectMapper
//        } else {
            return defaultObjectMapper
//        }
    }
    
    def createDefaultMapper(): ObjectMapper = {
    	var result: ObjectMapper = new ObjectMapper()
    	result.configure(Feature.INDENT_OUTPUT, true)
    	.configure(Feature.FAIL_ON_EMPTY_BEANS, false)
    	.configure(Feature.USE_ANNOTATIONS, false)
    	.configure(Feature.AUTO_DETECT_FIELDS, true)
    	return result
    }
    
    def createCombinedObjectMapper(): ObjectMapper = {
    	var result: ObjectMapper = new ObjectMapper()
    	result.configure(Feature.INDENT_OUTPUT, true)
    	return result
    }
 
}