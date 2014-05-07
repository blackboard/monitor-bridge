package blackboard.monitor.provider

import blackboard.monitor.connection.ConnectionPoolManager
import blackboard.monitor.connection.PostgresqlDataSource
import blackboard.monitor.connection.PostgresqlConnectionPool
import blackboard.monitor.mbean.BaseMBean

class PostgresMonitorProvider extends BaseMBean[PostgresMonitorProvider] {
  private var totalConnections: Long = _
  private var maxConnections: Long = _
  private var totalSessionTimeHours: Long = _
  private var longestSessionTimeHours: Long = _
  private var instanceSize: Long = _
  private var totalLiveSize: Long = _
  private var totalDeadSize: Long = _
  private var totalTableScans: Long = _
  private var totalIndexScans: Long = _
  private var totalInserts: Long = _
  private var totalUpdates: Long = _
  private var totalDeletes: Long = _

  def getTotalConnections: Long = getData(getMbeanName, getCacheTime).totalConnections

  def getMaxConnections: Long = getData(getMbeanName, getCacheTime).maxConnections

  def getTotalSessionTimeHours: Long = getData(getMbeanName, getCacheTime).totalSessionTimeHours

  def getLongestSessionTimeHours: Long = getData(getMbeanName, getCacheTime).longestSessionTimeHours

  def getInstanceSize: Long = getData(getMbeanName, getCacheTime).instanceSize

  def getTotalLiveSize: Long = getData(getMbeanName, getCacheTime).totalLiveSize

  def getTotalDeadSize: Long = getData(getMbeanName, getCacheTime).totalDeadSize

  def getTotalTableScans: Long = getData(getMbeanName, getCacheTime).totalTableScans

  def getTotalIndexScans: Long = getData(getMbeanName, getCacheTime).totalIndexScans

  def getTotalInserts: Long = getData(getMbeanName, getCacheTime).totalInserts

  def getTotalUpdates: Long = getData(getMbeanName, getCacheTime).totalUpdates

  def getTotalDeletes: Long = getData(getMbeanName, getCacheTime).totalDeletes

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
      val pstat1 = conn.prepareStatement(GET_SESSION_INFO)
      val rs1 = pstat1.executeQuery()

      if (rs1.next()) {
        totalConnections = rs1.getLong(1)
        maxConnections = rs1.getLong(2)
        totalSessionTimeHours = rs1.getLong(3)
        longestSessionTimeHours = rs1.getLong(4)
      }
      pstat1.close()
      rs1.close()

      val pstat2 = conn.prepareStatement(GET_STORAGE_AND_ACCESS_PATTERNS)
      val rs2 = pstat2.executeQuery()
      if (rs2.next()) {
        instanceSize = rs2.getLong(1)
        totalLiveSize = rs2.getLong(2)
        totalDeadSize = rs2.getLong(3)
        totalTableScans = rs2.getLong(4)
        totalIndexScans = rs2.getLong(5)
        totalInserts = rs2.getLong(6)
        totalUpdates = rs2.getLong(7)
        totalDeletes = rs2.getLong(8)
      }
      pstat2.close()
      rs2.close()
    } finally {
      connPool.releaseConnection(conn)
    }

    this
  }

  private val GET_SESSION_INFO = "select Count(1) Total_Connections,\n(select setting from pg_settings where name = 'max_connections') " +
    "Max_Connections,\nEXTRACT(epoch from (sum(now() - backend_start))) / 3600 Total_Session_Time_Hours, \nEXTRACT(epoch from (max(now() - backend_start))) / " +
    "3600 Longest_Session_Time_Hours\nfrom pg_stat_activity"

  private val GET_STORAGE_AND_ACCESS_PATTERNS = "SELECT (SELECT pg_catalog.pg_database_size(d.datname) FROM pg_catalog.pg_database d " +
    "where d.datname = current_database()) / 1048576 DATABASE_SIZE,\n  case when sum(sat.n_dead_tup) = 0 then 0\n  " +
    "else (sum(pg_total_relation_size(C.oid)) / (sum(reltuples) + sum(sat.n_dead_tup))) * sum(reltuples) / 1048576 end as Live_Size,\n  " +
    "case when sum(sat.n_dead_tup) = 0 then 0\n  " +
    "else (sum(pg_total_relation_size(C.oid)) / (sum(reltuples) + sum(sat.n_dead_tup))) * sum(sat.n_dead_tup) / 1048576 end as Dead_Size,\n  " +
    "sum(seq_scan) Table_Scans, \n  sum(idx_scan) Index_Scans, \n  sum(n_tup_ins) Number_of_Inserts, \n  sum(n_tup_upd) Number_of_Updates, \n  " +
    "sum(n_tup_del) Number_of_Deletes\n  FROM pg_class C\n  LEFT JOIN pg_namespace N ON N.oid = C.relnamespace\n  " +
    "inner join pg_stat_all_tables sat on sat.schemaname = N.nspname  \n            and sat.relname = C.relname          \n  " +
    "inner join pg_tables pt on pt.schemaname = N.nspname  \n        and pt.tablename = C.relname \n  WHERE nspname NOT IN ('pg_catalog', 'information_schema')\n  " +
    "AND C.relkind <> 'i'\n  AND nspname !~ '^pg_toast'"
}