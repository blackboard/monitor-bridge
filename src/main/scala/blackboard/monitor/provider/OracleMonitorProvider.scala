package blackboard.monitor.provider

import blackboard.monitor.connection._
import blackboard.monitor.mbean.BaseMBean

class OracleMonitorProvider extends BaseMBean[OracleMonitorProvider] {
  private var logons: Long = _
  private var userCommits: Long = _
  private var consistentGets: Long = _
  private var dbBlockGets: Long = _
  private var physicalReads: Long = _
  private var consistentChanges: Long = _
  private var dbBlockChanges: Long = _
  private var redoBufferAllocRetries: Long = _

  def getLogons = getData(getMbeanName, getCacheTime).logons

  def getUserCommits = getData(getMbeanName, getCacheTime).userCommits

  def getConsistenGets = getData(getMbeanName, getCacheTime).consistentGets

  def getDbBlockGets = getData(getMbeanName, getCacheTime).dbBlockGets

  def getPhysicalReads = getData(getMbeanName, getCacheTime).physicalReads

  def getConsistentChanges = getData(getMbeanName, getCacheTime).consistentChanges

  def getDbBlockChanges = getData(getMbeanName, getCacheTime).dbBlockChanges

  def getRedoBufferAllocRetries = getData(getMbeanName, getCacheTime).redoBufferAllocRetries

  def retrieveDataFromComponent(): OracleMonitorProvider = {
    val host = getProperties.get("host")
    val port = getProperties.get("port")
    val sid = getProperties.get("sid")
    val username = getProperties.get("username")
    val password = getProperties.get("password")

    val connPool = ConnectionPoolManager.getConnectionPool[OracleConnectionPool](OracleDataSource(host, port, username, password, sid))
    val conn = connPool.getConnection()

    try {
      val pstat = conn.prepareStatement(QUERY)
      val rs = pstat.executeQuery()

      if (rs.next()) {
        consistentChanges = rs.getLong(1)
      }
      if (rs.next()) {
        consistentGets = rs.getLong(1)
      }
      if (rs.next()) {
        dbBlockChanges = rs.getLong(1)
      }
      if (rs.next()) {
        dbBlockGets = rs.getLong(1)
      }
      if (rs.next()) {
        logons = rs.getLong(1)
      }
      if (rs.next()) {
        physicalReads = rs.getLong(1)
      }
      if (rs.next()) {
        redoBufferAllocRetries = rs.getLong(1)
      }
      if (rs.next()) {
        userCommits = rs.getLong(1)
      }
    } catch {
      case e: Throwable =>
        throw e
    } finally {
      connPool.releaseConnection(conn)
    }
    this
  }

  private val QUERY: String = """select value 
		from v$sysstat
		where name in (
			'consistent changes',
			'consistent gets',
			'db block changes',
			'db block gets',
			'logons current',
			'physical reads',
			'redo buffer allocation retries',
			'user commits'
			)
		order by name"""
}