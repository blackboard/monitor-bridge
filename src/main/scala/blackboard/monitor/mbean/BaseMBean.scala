package blackboard.monitor.mbean

import blackboard.monitor.util.PropertyNotSetException
import org.slf4j.LoggerFactory

abstract class BaseMBean[T] extends CacheableBean[T] {
  private val log = LoggerFactory.getLogger(classOf[BaseMBean[T]])

  private var properties: java.util.Map[String, String] = _
  private var cacheTime: Long = _
  private var mbeanName: String = _

  def setProperties(prop: java.util.Map[String, String]) = {
    beforePropertiesSet()
    properties = prop
    afterPropertiesSet()
  }

  def getProperties: java.util.Map[String, String] = properties

  def setCacheTime(time: Long) = {
    beforeCacheTimeSet()
    cacheTime = time
    afterCacheTimeSet()
  }

  def getCacheTime: Long = cacheTime

  def setMbeanName(name: String) = {
    beforeMbeanNameSet()
    mbeanName = name
    afterMBeanNameSet()
  }

  def getMbeanName: String = mbeanName

  def getData(name: String, time: Long): T = {
    val data = get(name, time)
    data match {
      case Some(data) => {
        data
      }
      case None => {
        try {
          val newData = retrieveDataFromComponent()
          put(name, newData)
          newData
        } catch {
          case ex: Throwable => {
            log.error("Get data from component failed", ex)
            throw ex
          }
        }
      }
    }
  }

  protected def getPropertiesWithCheck(key: String): String = {
    if (!getProperties.containsKey(key)) {
      throw new PropertyNotSetException(key)
    }
    getProperties.get(key)
  }

  protected def retrieveDataFromComponent(): T

  protected def beforePropertiesSet() = {}

  protected def afterPropertiesSet() = {}

  protected def beforeCacheTimeSet() = {}

  protected def afterCacheTimeSet() = {}

  protected def beforeMbeanNameSet() = {}

  protected def afterMBeanNameSet() = {}
}