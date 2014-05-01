package blackboard.monitor.connection

import org.scalatest._
import Matchers._

class ConnectionPoolManagerTest extends FlatSpec {

  "ConnectionPoolManager" should "return MongoConnectionPool if MongodbDataSource is passed" in {
    val instance = ConnectionPoolManager.getConnectionPool[MongoConnectionPool](MongodbDataSource("mongodb://localhost"))
    instance shouldBe a[MongoConnectionPool]
  }

  "ConnectionPoolManager" should "return JedisConnectionPool if RedisDataSource is passed" in {
    val instance = ConnectionPoolManager.getConnectionPool[JedisConnectionPool](RedisDataSource("localhost", "6379", ""))
    instance shouldBe a[JedisConnectionPool]
  }

  "ConnectionPoolManager" should "return OracleConnectionPool if OracleDataSource is passed" in {
    val instance = ConnectionPoolManager.getConnectionPool[OracleConnectionPool](OracleDataSource("localhost", "1521", "test", "test", "test"))
    instance shouldBe a[OracleConnectionPool]
  }

  "ConnectionPoolManager" should "return PostgresqlConnectionPool if PostgresqlDataSource is passed" in {
    val instance = ConnectionPoolManager.getConnectionPool[PostgresqlConnectionPool](PostgresqlDataSource("localhost", "5432", "test", "test", "test"))
    instance shouldBe a[PostgresqlConnectionPool]
  }
}