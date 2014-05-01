package blackboard.monitor.provider

import blackboard.monitor.connection.ConnectionPoolManager
import blackboard.monitor.connection.PostgresqlDataSource
import blackboard.monitor.connection.PostgresqlConnectionPool
import blackboard.monitor.mbean.BaseMBean

class PostgresMonitorProvider extends BaseMBean[PostgresMonitorProvider] {
  private var numbackends: Long = _
  private var xactCommit: Long = _
  private var blksHit: Long = _
  private var blksFetch: Long = _
  private var bufWrittenClean: Long = _
  private var maxwrittenClean: Long = _
  private var bufWrittenBackend: Long = _
  private var bufAlloc: Long = _

  def getNumbackends: Long = getData(getMbeanName, getCacheTime).numbackends

  def getXactCommit: Long = getData(getMbeanName, getCacheTime).xactCommit

  def getBlksHit: Long = getData(getMbeanName, getCacheTime).blksHit

  def getBlksFetch: Long = getData(getMbeanName, getCacheTime).blksFetch

  def getBufWrittenClean: Long = getData(getMbeanName, getCacheTime).bufWrittenClean

  def getMaxwrittenClean: Long = getData(getMbeanName, getCacheTime).maxwrittenClean

  def getBufWrittenBackend: Long = getData(getMbeanName, getCacheTime).bufWrittenBackend

  def getBufAlloc: Long = getData(getMbeanName, getCacheTime).bufAlloc

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
      val pstat = conn.prepareStatement(GET_STATS_QUERY)
      val rs = pstat.executeQuery()

      if (rs.next()) {
        numbackends = rs.getLong(1)
        xactCommit = rs.getLong(2)
        blksHit = rs.getLong(3)
        blksFetch = rs.getLong(4)
        bufWrittenClean = rs.getLong(5)
        maxwrittenClean = rs.getLong(6)
        bufWrittenBackend = rs.getLong(7)
        bufAlloc = rs.getLong(8)
      }
    } finally {
      connPool.releaseConnection(conn)
    }
    this
  }

  private val GET_STATS_QUERY = """SELECT 
  psd.numbackends
  ,psd.xact_commit
  ,psd.blks_hit
  ,psd.blks_fetch
  ,psb.buffers_clean
  ,psb.maxwritten_clean
  ,psb.buffers_backend
  ,psb.buffers_alloc
  FROM 
   (SELECT
      SUM(pg_stat_database.numbackends) as numbackends
     ,SUM(pg_stat_database.xact_commit) as xact_commit
     ,SUM(pg_stat_database.blks_hit) as blks_hit
     ,SUM(pg_stat_database.blks_read+pg_stat_database.blks_hit) as blks_fetch
     FROM pg_stat_database) psd, 
   (SELECT 
     pg_stat_bgwriter.buffers_clean as buffers_clean
     ,pg_stat_bgwriter.maxwritten_clean as maxwritten_clean
     ,pg_stat_bgwriter.buffers_backend as buffers_backend
     ,pg_stat_bgwriter.buffers_alloc as buffers_alloc
     FROM pg_stat_bgwriter) psb"""
}