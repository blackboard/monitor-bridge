package blackboard.monitor.provider

import com.mongodb.BasicDBObject
import com.mongodb.CommandResult
import com.mongodb.casbah.commons.MongoDBObject
import blackboard.monitor.connection._
import org.slf4j.LoggerFactory
import blackboard.monitor.mbean.BaseMBean

class MongoDbMonitorProvider extends BaseMBean[MongoDbMonitorProvider] {
  private val log = LoggerFactory.getLogger(classOf[MongoDbMonitorProvider])

  private var host: String = _
  private var version: String = _

  private var upTime: Long = _

  private var globalLockTotalTime: Long = _
  private var globalLockLockTime: Long = _
  private var globalLockCurrentQueueTotal: Long = _
  private var globalLockCurrentQueueReaders: Long = _
  private var globalLockCurrentQueueWriters: Long = _
  private var globalLockActiveClientsTotal: Long = _
  private var globalLockActiveClientsReaders: Long = _
  private var globalLockActiveClientsWriters: Long = _

  private var memBits: Long = _
  private var memResident: Long = _
  private var memVirtual: Long = _
  private var memSupported: Boolean = _
  private var memMapped: Long = _

  private var connectionsCurrent: Long = _
  private var connectionsAvailable: Long = _

  private var extraInfoHeapUsageBytes: Long = _
  private var extraInfoPageFaults: Long = _

  private var indexCountersBtreeAccesses: Long = _
  private var indexCountersBtreeHits: Long = _
  private var indexCountersBtreeMisses: Long = _
  private var indexCountersBtreeResets: Long = _

  private var backgroundFlushingFlushes: Long = _
  private var backgroundFlushingTotal: Long = _
  private var backgroundFlushingLast: Long = _

  private var cursorsTotalOpen: Long = _
  private var cursorsClientCursors: Long = _
  private var cursorsTimedOut: Long = _

  private var networkBytesIn: Long = _
  private var networkBytesOut: Long = _
  private var networkNumRequests: Long = _

  private var assertsRegular: Long = _
  private var assertsWarning: Long = _
  private var assertsMsg: Long = _
  private var assertsUser: Long = _
  private var assertsRollovers: Long = _

  private var opcountersInsert: Long = _
  private var opcountersQuery: Long = _
  private var opcountersUpdate: Long = _
  private var opcountersDelete: Long = _
  private var opcountersGetmore: Long = _
  private var opcountersCommand: Long = _

  def getHost: String = getData(getMbeanName, getCacheTime).host

  def getVersion: String = getData(getMbeanName, getCacheTime).version

  def getUpTime: Long = getData(getMbeanName, getCacheTime).upTime

  def getGlobalLockTotalTime: Long = getData(getMbeanName, getCacheTime).globalLockTotalTime

  def getGlobalLockLockTime: Long = getData(getMbeanName, getCacheTime).globalLockLockTime

  def getGlobalLockCurrentQueueTotal: Long = getData(getMbeanName, getCacheTime).globalLockCurrentQueueTotal

  def getGlobalLockCurrentQueueReaders: Long = getData(getMbeanName, getCacheTime).globalLockCurrentQueueReaders

  def getGlobalLockCurrentQueueWriters: Long = getData(getMbeanName, getCacheTime).globalLockCurrentQueueWriters

  def getGlobalLockActiveClientsTotal: Long = getData(getMbeanName, getCacheTime).globalLockActiveClientsTotal

  def getGlobalLockActiveClientsReaders: Long = getData(getMbeanName, getCacheTime).globalLockActiveClientsReaders

  def getGlobalLockActiveClientsWriters: Long = getData(getMbeanName, getCacheTime).globalLockActiveClientsWriters

  def getMemBits: Long = getData(getMbeanName, getCacheTime).memBits

  def getMemResident: Long = getData(getMbeanName, getCacheTime).memResident

  def getMemVirtual: Long = getData(getMbeanName, getCacheTime).memVirtual

  def getMemSupported: Boolean = getData(getMbeanName, getCacheTime).memSupported

  def getMemMapped: Long = getData(getMbeanName, getCacheTime).memMapped

  def getConnectionsCurrent: Long = getData(getMbeanName, getCacheTime).connectionsCurrent

  def getConnectionsAvailable: Long = getData(getMbeanName, getCacheTime).connectionsAvailable

  def getExtraInfoHeapUsageBytes: Long = getData(getMbeanName, getCacheTime).extraInfoHeapUsageBytes

  def getExtraInfoPageFaults: Long = getData(getMbeanName, getCacheTime).extraInfoPageFaults

  def getIndexCountersBtreeAccesses: Long = getData(getMbeanName, getCacheTime).indexCountersBtreeAccesses

  def getIndexCountersBtreeHits: Long = getData(getMbeanName, getCacheTime).indexCountersBtreeHits

  def getIndexCountersBtreeMisses: Long = getData(getMbeanName, getCacheTime).indexCountersBtreeMisses

  def getIndexCountersBtreeResets: Long = getData(getMbeanName, getCacheTime).indexCountersBtreeResets

  def getBackgroundFlushingFlushes: Long = getData(getMbeanName, getCacheTime).backgroundFlushingFlushes

  def getBackgroundFlushingTotal: Long = getData(getMbeanName, getCacheTime).backgroundFlushingTotal

  def getBackgroundFlushingLast: Long = getData(getMbeanName, getCacheTime).backgroundFlushingLast

  def getCursorsTotalOpen: Long = getData(getMbeanName, getCacheTime).cursorsTotalOpen

  def getCursorsClientCursors: Long = getData(getMbeanName, getCacheTime).cursorsClientCursors

  def getCursorsTimedOut: Long = getData(getMbeanName, getCacheTime).cursorsTimedOut

  def getNetworkBytesIn: Long = getData(getMbeanName, getCacheTime).networkBytesIn

  def getNetworkBytesOut: Long = getData(getMbeanName, getCacheTime).networkBytesOut

  def getNetworkNumRequests: Long = getData(getMbeanName, getCacheTime).networkNumRequests

  def getAssertsRegular: Long = getData(getMbeanName, getCacheTime).assertsRegular

  def getAssertsWarning: Long = getData(getMbeanName, getCacheTime).assertsWarning

  def getAssertsMsg: Long = getData(getMbeanName, getCacheTime).assertsMsg

  def getAssertsUser: Long = getData(getMbeanName, getCacheTime).assertsUser

  def getAssertsRollovers: Long = getData(getMbeanName, getCacheTime).assertsRollovers

  def getOpcountersInsert: Long = getData(getMbeanName, getCacheTime).opcountersInsert

  def getOpcountersQuery: Long = getData(getMbeanName, getCacheTime).opcountersQuery

  def getOpcountersUpdate: Long = getData(getMbeanName, getCacheTime).opcountersUpdate

  def getOpcountersDelete: Long = getData(getMbeanName, getCacheTime).opcountersDelete

  def getOpcountersGetmore: Long = getData(getMbeanName, getCacheTime).opcountersGetmore

  def getOpcountersCommand: Long = getData(getMbeanName, getCacheTime).opcountersCommand

  override protected def retrieveDataFromComponent(): MongoDbMonitorProvider = {
    log.debug("Get mongodb server status from mongodb")

    val uriStr = getProperties.get("uri")

    val dataSource = MongodbDataSource(uriStr)
    val connPool = ConnectionPoolManager.getConnectionPool[MongoConnectionPool](dataSource)
    val admin = connPool.getConnection()("admin")
    val getStatusCmd = MongoDBObject("serverStatus" -> 1)
    val result = admin.command(getStatusCmd)
    if (result.ok()) {
      upTime = result.getLong("uptime")
      host = result.getString("host")
      version = result.getString("version")

      extractGlobalLockInfo(result)
      extractMemInfo(result)
      extractExtraInfo(result)
      extractConnInfo(result)
      extractIndexInfo(result)
      extractBackgroundFlushingInfo(result)
      extractCursorInfo(result)
      extractNetworkInfo(result)
      extractAssertInfo(result)
      extractOpcounterInfo(result)
    }
    MongoDbMonitorProvider.this
  }

  private def extractOpcounterInfo(cmdResults: CommandResult) = {
    val opcounters = cmdResults.get("opcounters").asInstanceOf[BasicDBObject]

    if (opcounters != null) {
      opcountersInsert = opcounters.getLong("insert")
      opcountersQuery = opcounters.getLong("query")
      opcountersUpdate = opcounters.getLong("update")
      opcountersDelete = opcounters.getLong("delete")
      opcountersGetmore = opcounters.getLong("getmore")
      opcountersCommand = opcounters.getLong("command")
    }
  }

  private def extractAssertInfo(cmdResults: CommandResult) = {
    val asserts = cmdResults.get("asserts").asInstanceOf[BasicDBObject]
    if (asserts != null) {
      assertsMsg = asserts.getLong("msg")
      assertsUser = asserts.getLong("user")
      assertsRegular = asserts.getLong("regular")
      assertsWarning = asserts.getLong("warning")
      assertsRollovers = asserts.getLong("rollovers")
    }
  }

  private def extractNetworkInfo(cmdResults: CommandResult) = {
    val network = cmdResults.get("network").asInstanceOf[BasicDBObject]
    if (network != null) {
      networkBytesIn = network.getLong("bytesIn")
      networkBytesOut = network.getLong("bytesOut")
      networkNumRequests = network.getLong("numRequests")
    }
  }

  private def extractCursorInfo(cmdResults: CommandResult) = {
    val cursors = cmdResults.get("cursors").asInstanceOf[BasicDBObject]
    if (cursors != null) {
      cursorsTotalOpen = cursors.getLong("totalOpen")
      cursorsClientCursors = cursors.getLong("clientCursors_size")
      cursorsTimedOut = cursors.getLong("timedOut")
    }
  }

  private def extractBackgroundFlushingInfo(cmdResults: CommandResult) = {
    val backgroundFlushing = cmdResults.get("backgroundFlushing").asInstanceOf[BasicDBObject]
    if (backgroundFlushing != null) {
      backgroundFlushingFlushes = backgroundFlushing.getLong("flushes")
      backgroundFlushingTotal = backgroundFlushing.getLong("total_ms")
      backgroundFlushingLast = backgroundFlushing.getLong("last_ms")
    }
  }

  private def extractIndexInfo(cmdResults: CommandResult) = {
    val indexCounters = cmdResults.get("indexCounters").asInstanceOf[BasicDBObject]
    if (indexCounters != null) {
      val btree = indexCounters.get("btree").asInstanceOf[BasicDBObject]
      if (btree != null) {
        indexCountersBtreeAccesses = btree.getLong("accesses")
        indexCountersBtreeHits = btree.getLong("hits")
        indexCountersBtreeMisses = btree.getLong("misses")
        indexCountersBtreeResets = btree.getLong("resets")
      }
    }
  }

  private def extractConnInfo(cmdResults: CommandResult) = {
    val connections = cmdResults.get("connections").asInstanceOf[BasicDBObject]
    if (connections != null) {
      connectionsCurrent = connections.getLong("current")
      connectionsAvailable = connections.getLong("available")
    }
  }

  private def extractExtraInfo(cmdResults: CommandResult) = {
    val extraInfo = cmdResults.get("extra_info").asInstanceOf[BasicDBObject]
    if (extraInfo != null) {
      extraInfoHeapUsageBytes = extraInfo.getLong("heap_usage_bytes")
      extraInfoPageFaults = extraInfo.getLong("page_faults")
    }
  }

  private def extractMemInfo(cmdResults: CommandResult) = {
    val mem = cmdResults.get("mem").asInstanceOf[BasicDBObject]
    if (mem != null) {
      memBits = mem.getLong("bits")
      memResident = mem.getLong("resident")
      memVirtual = mem.getLong("virtual")
      memSupported = mem.getBoolean("supported")
      memMapped = mem.getLong("mapped")
    }
  }

  private def extractGlobalLockInfo(cmdResults: CommandResult) = {
    val globalLock = cmdResults.get("globalLock").asInstanceOf[BasicDBObject]
    if (globalLock != null) {
      globalLockTotalTime = globalLock.getLong("totalTime")
      globalLockLockTime = globalLock.getLong("lockTime")

      val globalLockCurrentQueue = globalLock.get("currentQueue").asInstanceOf[BasicDBObject]
      if (globalLockCurrentQueue != null) {
        globalLockCurrentQueueTotal = globalLockCurrentQueue.getLong("total")
        globalLockCurrentQueueReaders = globalLockCurrentQueue.getLong("readers")
        globalLockCurrentQueueWriters = globalLockCurrentQueue.getLong("writers")
      }

      val globalLockActiveClients = globalLock.get("activeClients").asInstanceOf[BasicDBObject]
      if (globalLockActiveClients != null) {
        globalLockActiveClientsTotal = globalLockActiveClients.getLong("total")
        globalLockActiveClientsReaders = globalLockActiveClients.getLong("readers")
        globalLockActiveClientsWriters = globalLockActiveClients.getLong("writers")
      }
    }
  }
}