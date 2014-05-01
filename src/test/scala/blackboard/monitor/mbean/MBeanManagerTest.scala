package blackboard.monitor.mbean

import com.typesafe.config.ConfigFactory
import blackboard.cloud.spring.ApplicationContextProvider
import blackboard.monitor.provider.{PostgresMonitorProvider, MongoDbMonitorProvider}
import org.springframework.context.ApplicationContext
import org.scalatest._
import org.scalatest.Matchers._

class MBeanManagerTest extends FlatSpec with BeforeAndAfter {
  var context: ApplicationContext = null

  before {
    if (context == null) {
      val config = ConfigFactory.load("application.conf")
      MBeanManager.get.registerMBeans(config)
      context = ApplicationContextProvider.getApplicationContext
    }
  }

  "Four registered MBeans" should "exists" in {
    val beans = context.getBeansOfType(classOf[MongoDbMonitorProvider])
    beans should have size 3
  }

  "One of the four registered MBeans" should "be an instance of PostgresStatisticsProvider" in {
    val bean = context.getBeansOfType(classOf[PostgresMonitorProvider])
    bean should have size 1
  }
}
