package blackboard.monitor.mbean

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.context.support.{ClassPathXmlApplicationContext, GenericApplicationContext}
import org.springframework.jmx.export.MBeanExporter

import com.typesafe.config.Config

import blackboard.cloud.scala.util.ServiceFactory
import blackboard.cloud.spring.ApplicationContextProvider

class MBeanManager {
  private def supportedTypeMapping(config: Config): Map[String, String] = {
    var mappings: Map[String, String] = Map()
    val mappingsConfig = config.getConfig("monitor.class.mapping")
    val itr = mappingsConfig.entrySet().iterator()

    while (itr.hasNext()) {
      val mapping = itr.next()
      val key = mapping.getKey()
      val value = mappingsConfig.getString(key)
      mappings += key -> value
    }
    mappings
  }

  private def getMbeanNames(config: Config): Map[String, (String, String)] = {
    val mbeans = config.getConfig("monitor.mbean")
    var mbeanNames: Map[String, (String, String)] = Map()

    // we need know the bean names before other operations
    for ((typ, clazz) <- supportedTypeMapping(config)) {
      try {
        val instances = mbeans.getConfig(typ)
        val keys = instances.entrySet().iterator()
        while (keys.hasNext()) {
          val key = keys.next().getKey()
          val name = key.split("\\.")(0)
          mbeanNames += (name ->(typ, supportedTypeMapping(config).get(typ).get))
        }
      } catch { case _: Throwable => }
    }
    mbeanNames
  }

  def registerMBeans(config: Config) = {
    new ClassPathXmlApplicationContext("appContext.xml")
    val mbeanNames = getMbeanNames(config)
    registerBeans(mbeanNames, config)
  }

  private def registerBeans(beanNames: Map[String, (String, String)], config: Config) = {
    val mbeansConfig = config.getConfig("monitor.mbean")
    val ctx = new GenericApplicationContext(ApplicationContextProvider.getApplicationContext())
    val mbeans = new java.util.HashMap[String, Object]()

    beanNames.foreach((entry: (String, (String, String))) => {
      val name = entry._1
      val typ = entry._2._1
      val clazz = entry._2._2

      val mbeanConfig = mbeansConfig.getConfig(typ + "." + name)
      val cacheTime = mbeanConfig.getString("cacheTime")

      val prop = new java.util.HashMap[String, String]()
      val itr = mbeanConfig.entrySet().iterator()

      while (itr.hasNext) {
        val each = itr.next()
        if (!each.getKey().equals("cacheTime")) {
          prop.put(each.getKey(), mbeanConfig.getString(each.getKey()))
        }
      }

      val beanBuilder = BeanDefinitionBuilder.rootBeanDefinition(clazz)
      beanBuilder.addPropertyValue("mbeanName", name)
      beanBuilder.addPropertyValue("cacheTime", cacheTime)
      beanBuilder.addPropertyValue("properties", prop)
      beanBuilder.setScope(BeanDefinition.SCOPE_SINGLETON)

      ctx.registerBeanDefinition(name, beanBuilder.getBeanDefinition())
      mbeans.put("monitor-bridge:name=" + name, ctx.getBean(name))
    })

    ctx.refresh()
    val mbeanExporter = new MBeanExporter()
    mbeanExporter.setBeans(mbeans)
    mbeanExporter.afterPropertiesSet()
  }
}

object MBeanManager {
  val serviceFactory = ServiceFactory(new MBeanManager())

  def get = serviceFactory.get
}
