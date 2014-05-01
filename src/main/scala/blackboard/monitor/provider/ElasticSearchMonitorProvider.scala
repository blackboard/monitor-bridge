package blackboard.monitor.provider

import dispatch.Http
import dispatch.as
import dispatch.implyRequestHandlerTuple
import dispatch.url
import sjson.json._
import DefaultProtocol._
import JsonSerialization._
import org.slf4j.LoggerFactory
import blackboard.monitor.mbean.BaseMBean

class ElasticSearchMonitorProvider extends BaseMBean[ElasticSearchMonitorProvider] {
  private val log = LoggerFactory.getLogger(classOf[ElasticSearchMonitorProvider])

  private var ok: Boolean = _

  private var totalShards: Int = _
  private var successfulShards: Int = _
  private var failedShards: Int = _

  private var totalDocs: Long = _
  private var totaldeletedDocs: Long = _

  private var totalStoreSizeInBytes: Long = _
  private var totalThrottleTimeInMillis: Long = _

  private var totalIndexTotal: Long = _
  private var totalIndexTimeInMillis: Long = _
  private var totalIndexCurrent: Long = _
  private var totalIndexDeleteTotal: Long = _
  private var totalIndexDeleteTimeInMillis: Long = _
  private var totalIndexDeleteCurrent: Long = _

  private var totalGetTotal: Long = _
  private var totalGetTimeInMillis: Long = _
  private var totalGetExistsTotal: Long = _
  private var totalGetExistsTimeInMillis: Long = _
  private var totalGetMissingTotal: Long = _
  private var totalGetMissingTimeInMillis: Long = _
  private var totalGetCurrent: Long = _

  private var totalSearchQueryTotal: Long = _
  private var totalSearchQueryTimeInMillis: Long = _
  private var totalSearchQueryCurrent: Long = _
  private var totalSearchFetchTotal: Long = _
  private var totalSearchFetchTimeInMillis: Long = _
  private var totalSearchFetchCurrent: Long = _

  def getOk: Boolean = getData(getMbeanName, getCacheTime).ok

  def getTotalShards: Int = getData(getMbeanName, getCacheTime).totalShards

  def getSuccessfulShards: Int = getData(getMbeanName, getCacheTime).successfulShards

  def getFailedShards: Int = getData(getMbeanName, getCacheTime).failedShards

  def getTotalDocs: Long = getData(getMbeanName, getCacheTime).totalDocs

  def getTotalDeletedDocs: Long = getData(getMbeanName, getCacheTime).totaldeletedDocs

  def getTotalStoreSizeInBytes: Long = getData(getMbeanName, getCacheTime).totalStoreSizeInBytes

  def getTotalThrottleTimeInMillis: Long = getData(getMbeanName, getCacheTime).totalThrottleTimeInMillis

  def getTotalIndexTotal: Long = getData(getMbeanName, getCacheTime).totalIndexTotal

  def getTotalIndexTimeInMillis: Long = getData(getMbeanName, getCacheTime).totalIndexTimeInMillis

  def getTotalIndexCurrent: Long = getData(getMbeanName, getCacheTime).totalIndexCurrent

  def getTotalIndexDeleteTotal: Long = getData(getMbeanName, getCacheTime).totalIndexDeleteTotal

  def getTotalIndexDeleteTimeInMillis: Long = getData(getMbeanName, getCacheTime).totalIndexDeleteTimeInMillis

  def getTotalIndexDeleteCurrent: Long = getData(getMbeanName, getCacheTime).totalIndexDeleteCurrent

  def getTotalGetTotal: Long = getData(getMbeanName, getCacheTime).totalGetTotal

  def getTotalGetTimeInMillis: Long = getData(getMbeanName, getCacheTime).totalGetTimeInMillis

  def getTotalGetExistsTotal: Long = getData(getMbeanName, getCacheTime).totalGetExistsTotal

  def getTotalGetExistsTimeInMillis: Long = getData(getMbeanName, getCacheTime).totalGetExistsTimeInMillis

  def getTotalGetMissingTotal: Long = getData(getMbeanName, getCacheTime).totalGetMissingTotal

  def getTotalGetMissingTimeInMillis: Long = getData(getMbeanName, getCacheTime).totalGetMissingTimeInMillis

  def getTotalGetCurrent: Long = getData(getMbeanName, getCacheTime).totalGetCurrent

  def getTotalSearchQueryTotal: Long = getData(getMbeanName, getCacheTime).totalSearchQueryTotal

  def getTotalSearchQueryTimeInMillis: Long = getData(getMbeanName, getCacheTime).totalSearchQueryTimeInMillis

  def getTotalSearchQueryCurrent: Long = getData(getMbeanName, getCacheTime).totalSearchQueryCurrent

  def getTotalSearchFetchTotal: Long = getData(getMbeanName, getCacheTime).totalSearchFetchTotal

  def getTotalSearchFetchTimeInMillis: Long = getData(getMbeanName, getCacheTime).totalSearchFetchTimeInMillis

  def getTotalSearchFetchCurrent: Long = getData(getMbeanName, getCacheTime).totalSearchFetchCurrent

  override protected def retrieveDataFromComponent(): ElasticSearchMonitorProvider = {
    log.debug("Get ElasticSearch Statistics Data via REST CALL")
    val basicUrl = getProperties.get("url")
    //Rest Call Request
    val svc = url(basicUrl + "/_stats")

    val promise = Http(svc OK as.String)

    //The Request is asynchronous, we need to wait until it complete. The max timeout is 20s
    val maxWait = 200
    var n: Int = 0
    while (!promise.isComplete && n < maxWait) {
      n += 1
      Thread.sleep(100)
    }

    if (!promise.isComplete) {
      log.debug("Get ElasticSearch Statistics Data via REST CALL didn't complete after 20s.")
      return this
    }

    val promiseStr = promise.toString()
    val jsonStr = promiseStr.substring(8, promiseStr.length() - 1)
    val stats = fromjson[Stats](tojson(jsonStr))

    ok = stats.ok
    totalShards = stats.shards.total
    successfulShards = stats.shards.successful
    failedShards = stats.shards.failed

    totalDocs = stats.all.total.docs.count
    totaldeletedDocs = stats.all.total.docs.deleted

    totalStoreSizeInBytes = stats.all.total.store.sizeInBytes
    totalThrottleTimeInMillis = stats.all.total.store.throttleTimeInMillis

    totalIndexTotal = stats.all.total.indexing.indexTotal
    totalIndexTimeInMillis = stats.all.total.indexing.indexTimeInMillis
    totalIndexCurrent = stats.all.total.indexing.indexCurrent
    totalIndexDeleteTotal = stats.all.total.indexing.deleteTotal
    totalIndexDeleteTimeInMillis = stats.all.total.indexing.deleteTimeInMillis
    totalIndexDeleteCurrent = stats.all.total.indexing.deleteCurrent

    totalGetTotal = stats.all.total.get.total
    totalGetTimeInMillis == stats.all.total.get.timeInMillis
    totalGetExistsTotal = stats.all.total.get.existsTotal
    totalGetExistsTimeInMillis = stats.all.total.get.existsTimeInMillis
    totalGetMissingTotal = stats.all.total.get.missingTotal
    totalGetMissingTimeInMillis = stats.all.total.get.missingTimeInMillis
    totalGetCurrent = stats.all.total.get.current

    totalSearchQueryTotal == stats.all.total.search.queryTotal
    totalSearchQueryTimeInMillis = stats.all.total.search.queryTimeInMillis
    totalSearchQueryCurrent = stats.all.total.search.queryCurrent
    totalSearchFetchTotal = stats.all.total.search.fetchTotal
    totalSearchFetchTimeInMillis = stats.all.total.search.fetchTimeInMillis
    totalSearchFetchCurrent = stats.all.total.search.fetchCurrent

    this
  }

  /**
   * {"ok":true,
   * "_shards":{"total":4,"successful":2,"failed":0},
   * "_all":{
   * "primaries":
   * {
   * "docs":{"count":5,"deleted":1},
   * "store":{"size":"9kb","size_in_bytes":9259,"throttle_time":"0s","throttle_time_in_millis":0},
   * "indexing":{"index_total":0,"index_time":"0s","index_time_in_millis":0,"index_current":0,"delete_total":0,"delete_time":"0s","delete_time_in_millis":0,"delete_current":0},
   * "get":{"total":0,"time":"0s","time_in_millis":0,"exists_total":0,"exists_time":"0s","exists_time_in_millis":0,"missing_total":0,"missing_time":"0s","missing_time_in_millis":0,"current":0},
   * "search":{"query_total":0,"query_time":"0s","query_time_in_millis":0,"query_current":0,"fetch_total":0,"fetch_time":"0s","fetch_time_in_millis":0,"fetch_current":0}
   * },
   * "total":
   * {
   * "docs":{"count":5,"deleted":1},
   * "store":{"size":"9kb","size_in_bytes":9259,"throttle_time":"0s","throttle_time_in_millis":0},
   * "indexing":{"index_total":0,"index_time":"0s","index_time_in_millis":0,"index_current":0,"delete_total":0,"delete_time":"0s","delete_time_in_millis":0,"delete_current":0},
   * "get":{"total":0,"time":"0s","time_in_millis":0,"exists_total":0,"exists_time":"0s","exists_time_in_millis":0,"missing_total":0,"missing_time":"0s","missing_time_in_millis":0,"current":0},
   * "search":{"query_total":0,"query_time":"0s","query_time_in_millis":0,"query_current":0,"fetch_total":0,"fetch_time":"0s","fetch_time_in_millis":0,"fetch_current":0}
   * },
   * "indices": {"folklor":{"primaries":{"docs":{"count":5,"deleted":1},"store":{"size":"9kb","size_in_bytes":9259,"throttle_time":"0s","throttle_time_in_millis":0},"indexing":{"index_total":0,"index_time":"0s","index_time_in_millis":0,"index_current":0,"delete_total":0,"delete_time":"0s","delete_time_in_millis":0,"delete_current":0},"get":{"total":0,"time":"0s","time_in_millis":0,"exists_total":0,"exists_time":"0s","exists_time_in_millis":0,"missing_total":0,"missing_time":"0s","missing_time_in_millis":0,"current":0},"search":{"query_total":0,"query_time":"0s","query_time_in_millis":0,"query_current":0,"fetch_total":0,"fetch_time":"0s","fetch_time_in_millis":0,"fetch_current":0}},"total":{"docs":{"count":5,"deleted":1},"store":{"size":"9kb","size_in_bytes":9259,"throttle_time":"0s","throttle_time_in_millis":0},"indexing":{"index_total":0,"index_time":"0s","index_time_in_millis":0,"index_current":0,"delete_total":0,"delete_time":"0s","delete_time_in_millis":0,"delete_current":0},"get":{"total":0,"time":"0s","time_in_millis":0,"exists_total":0,"exists_time":"0s","exists_time_in_millis":0,"missing_total":0,"missing_time":"0s","missing_time_in_millis":0,"current":0},"search":{"query_total":0,"query_time":"0s","query_time_in_millis":0,"query_current":0,"fetch_total":0,"fetch_time":"0s","fetch_time_in_millis":0,"fetch_current":0}}}}
   * }
   * }
   */
  case class Docs(count: Long, deleted: Long)

  implicit val DocsFormat: Format[Docs] = asProduct2("count", "deleted")(Docs)(Docs.unapply(_).get)

  case class Store(sizeInBytes: Long, throttleTimeInMillis: Long)

  implicit val StoreFormat: Format[Store] = asProduct2("size_in_bytes", "throttle_time_in_millis")(Store)(Store.unapply(_).get)

  case class Indexing(indexTotal: Long, indexTimeInMillis: Long, indexCurrent: Long, deleteTotal: Long, deleteTimeInMillis: Long, deleteCurrent: Long)

  implicit val IndexingFormat: Format[Indexing] = asProduct6("index_total", "index_time_in_millis", "index_current", "delete_total", "delete_time_in_millis", "delete_current")(Indexing)(Indexing.unapply(_).get)

  case class Get(total: Long, timeInMillis: Long, existsTotal: Long, existsTimeInMillis: Long, missingTotal: Long, missingTimeInMillis: Long, current: Long)

  implicit val GetFormat: Format[Get] = asProduct7("total", "time_in_millis", "exists_total", "exists_time_in_millis", "missing_total", "missing_time_in_millis", "current")(Get)(Get.unapply(_).get)

  case class Search(queryTotal: Long, queryTimeInMillis: Long, queryCurrent: Long, fetchTotal: Long, fetchTimeInMillis: Long, fetchCurrent: Long)

  implicit val SearchFormat: Format[Search] = asProduct6("query_total", "query_time_in_millis", "query_current", "fetch_total", "fetch_time_in_millis", "fetch_current")(Search)(Search.unapply(_).get)

  case class Total(docs: Docs, store: Store, indexing: Indexing, get: Get, search: Search)

  implicit var TotalFormat: Format[Total] = asProduct5("docs", "store", "indexing", "get", "search")(Total)(Total.unapply(_).get)

  case class Primaries(docs: Docs, store: Store, indexing: Indexing, get: Get, search: Search)

  implicit var PrimariesFormat: Format[Primaries] = asProduct5("docs", "store", "indexing", "get", "search")(Primaries)(Primaries.unapply(_).get)

  case class All(total: Total, primaries: Primaries)

  implicit var AllFormat: Format[All] = asProduct2("total", "primaries")(All)(All.unapply(_).get)

  case class Shards(total: Int, successful: Int, failed: Int)

  implicit val ShardsFormat: Format[Shards] = asProduct3("total", "successful", "failed")(Shards)(Shards.unapply(_).get)

  case class Stats(ok: Boolean, shards: Shards, all: All)

  implicit var StatsFormat: Format[Stats] = asProduct3("ok", "_shards", "_all")(Stats)(Stats.unapply(_).get)

}
 