package blackboard.monitor

import blackboard.monitor.mbean.MBeanManager
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object MonitorApplication extends App {
  private val log = LoggerFactory.getLogger(MonitorApplication.getClass())

  val config = ConfigFactory.load()
  MBeanManager.get.registerMBeans(config)
  log.info("monitor-bridge Started")
  loop()

  private def loop() = {
    while (true) {
      Thread.sleep(100000000L)
    }
  }
}