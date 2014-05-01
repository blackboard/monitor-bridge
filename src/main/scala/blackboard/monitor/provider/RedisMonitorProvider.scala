package blackboard.monitor.provider

import blackboard.monitor.connection._
import blackboard.monitor.mbean.BaseMBean

class RedisMonitorProvider extends BaseMBean[RedisMonitorProvider] {
  private var uptimeInSeconds: Long = _

  private var connectedClients: Int = _
  private var blockedClients: Int = _

  private var usedMemory: Long = _
  private var usedMemoryRss: Long = _

  private var usedCpuSys: Float = _
  private var usedCpuUser: Float = _
  private var usedCpuSysChildren: Float = _
  private var usedCpuUserChildren: Float = _

  private var totalConnectionsReceived: Long = _
  private var totalCommandsProcessed: Long = _

  private var expiredKeys: Long = _
  private var evictedKeys: Long = _
  private var keyspaceHits: Long = _
  private var keyspaceMisses: Long = _

  private var role: String = _
  private var connectedSlaves: Long = _
  private var masterHost: String = "me"

  def getUptimeInSeconds: Long = getData(getMbeanName, getCacheTime).uptimeInSeconds

  def getConnectedClients: Int = getData(getMbeanName, getCacheTime).connectedClients

  def getBlockedClients: Int = getData(getMbeanName, getCacheTime).blockedClients

  def getUsedMemory: Long = getData(getMbeanName, getCacheTime).usedMemory

  def getUsedMemoryRss: Long = getData(getMbeanName, getCacheTime).usedMemoryRss

  def getUsedCpuSys: Float = getData(getMbeanName, getCacheTime).usedCpuSys

  def getUsedCpuUser: Float = getData(getMbeanName, getCacheTime).usedCpuUser

  def getUsedCpuSysChildren: Float = getData(getMbeanName, getCacheTime).usedCpuSysChildren

  def getUsedCpuUserChildren: Float = getData(getMbeanName, getCacheTime).usedCpuUserChildren

  def getTotalConnectionsReceived: Long = getData(getMbeanName, getCacheTime).totalConnectionsReceived

  def getTotalCommandsProcessed: Long = getData(getMbeanName, getCacheTime).totalCommandsProcessed

  def getExpiredKeys: Long = getData(getMbeanName, getCacheTime).expiredKeys

  def getEvictedKeys: Long = getData(getMbeanName, getCacheTime).evictedKeys

  def getKeyspaceHits: Long = getData(getMbeanName, getCacheTime).keyspaceHits

  def getKeyspaceMisses: Long = getData(getMbeanName, getCacheTime).keyspaceMisses

  def getRole: String = getData(getMbeanName, getCacheTime).role

  def getMasterHost: String = getData(getMbeanName, getCacheTime).masterHost

  def getConnectedSlaves: Long = getData(getMbeanName, getCacheTime).connectedSlaves

  override protected def retrieveDataFromComponent(): RedisMonitorProvider = {
    val host = getProperties.get("host")
    val port = getProperties.get("port")
    val password = getProperties.get("password")

    val connPool = ConnectionPoolManager.getConnectionPool[JedisConnectionPool](RedisDataSource(host, port, password))
    val jedis = connPool.getConnection()
    try {
      val info = jedis.info
      parser(info)
      this
    } finally {
      connPool.releaseConnection(jedis)
    }
  }

  private def parser(info: String) = {
    if (info == null || info.trim == "") {
      throw new Exception("Can't get info from redis")
    }

    for (line <- info.split("\r\n")) {
      line match {
        case Extractor(line) =>
        case _ =>
      }
    }

  }

  object Extractor {
    def unapply(str: String): Option[String] = {
      val parts = str.split(":")
      parts.length match {
        case 2 =>
          val property = parts(0).trim
          val value = parts(1).trim
          property match {
            case "uptime_in_seconds" =>
              uptimeInSeconds = value.toLong
              Some(property)
            case "used_cpu_sys" =>
              usedCpuSys = value.toFloat
              Some(property)
            case "used_cpu_user" =>
              usedCpuUser = value.toFloat
              Some(property)
            case "used_cpu_sys_children" =>
              usedCpuSysChildren = value.toFloat
              Some(property)
            case "used_cpu_user_children" =>
              usedCpuUserChildren = value.toFloat
              Some(property)
            case "connected_clients" =>
              connectedClients = value.toInt
              Some(property)
            case "blocked_clients" =>
              blockedClients = value.toInt
              Some(property)
            case "used_memory" =>
              usedMemory = value.toLong
              Some(property)
            case "used_memory_rss" =>
              usedMemoryRss = value.toLong
              Some(property)
            case "total_connections_received" =>
              totalConnectionsReceived = value.toLong
              Some(property)
            case "total_commands_processed" =>
              totalCommandsProcessed = value.toLong
              Some(property)
            case "expired_keys" =>
              expiredKeys = value.toLong
              Some(property)
            case "evicted_keys" =>
              evictedKeys = value.toLong
              Some(property)
            case "keyspace_hits" =>
              keyspaceHits = value.toLong
              Some(property)
            case "keyspace_misses" =>
              keyspaceMisses = value.toLong
              Some(property)
            case "role" =>
              role = value.toString
              Some(property)
            case "connected_slaves" =>
              connectedSlaves = value.toLong
              Some(property)
            case "master_host" =>
              masterHost = value.toString
              Some(property)
            case _ => None
          }
        case _ => None
      }
    }
  }

}