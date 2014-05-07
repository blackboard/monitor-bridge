package blackboard.monitor.provider

import blackboard.monitor.connection.ConnectionPoolManager
import blackboard.monitor.connection.PostgresqlDataSource
import blackboard.monitor.connection.PostgresqlConnectionPool
import blackboard.monitor.mbean.BaseMBean

class PostgresMonitorProvider extends BaseMBean[PostgresMonitorProvider] {
  private var totalConnections: Long = _
  private var maxConnections: Long = _
  private var totalSessionTimeHours: Long = _
  private var oldestSessionHours: Long = _

  def getTotalConnections: Long = getData(getMbeanName, getCacheTime).totalConnections

  def getMaxConnections: Long = getData(getMbeanName, getCacheTime).maxConnections

  def getTotalSessionTimeHours: Long = getData(getMbeanName, getCacheTime).totalSessionTimeHours

  def getOldestSessionHours: Long = getData(getMbeanName, getCacheTime).oldestSessionHours

  override protected def retrieveDataFromComponent(): PostgresMonitorProvider = {
    val host = getProperties.get("host")
    val port = getProperties.get("port")
    val db = getProperties.get("db")
    val username = getProperties.get("username")
    val password = getProperties.get("password")

    val dataSource = PostgresqlDataSource(host, port, db, username, password)
    val connPool = ConnectionPoolManager.getConnectionPool[PostgresqlConnectionPool](dataSource)
    val conn = connPool.getConnection()

    try {
      val pstat = conn.prepareStatement(GET_SESSION_INFO)
      val rs = pstat.executeQuery()

      if (rs.next()) {
        totalConnections = rs.getLong(1)
        maxConnections = rs.getLong(2)
        totalSessionTimeHours = rs.getLong(3)
        oldestSessionHours = rs.getLong(4)
      }
    } finally {
      connPool.releaseConnection(conn)
    }

    this
  }

  private val GET_SESSION_INFO = "select Count(1) Total_Connections,\n(select setting from pg_settings where name = 'max_connections') " +
    "Max_Connections,\nEXTRACT(epoch from (sum(now() - backend_start))) / 3600 Total_Session_Time_Hours, \nEXTRACT(epoch from (max(now() - backend_start))) / " +
    "3600 Oldest_Session_Hours\nfrom pg_stat_activity"
}