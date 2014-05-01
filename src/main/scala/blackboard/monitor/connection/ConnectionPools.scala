package blackboard.monitor.connection

trait ConnectionPool[T] {
  /**
   * Initialize the connection pool
   */
  def initPool()

  /**
   * Get a connection from the pool
   */
  def getConnection: T

  /**
   * Return a connection to the pool
   */
  def releaseConnection(connection: T)

  /**
   * Close connection pool
   */
  def closePool()
}

class JedisConnectionPool(dataSource: DataSource)
  extends ConnectionPool[redis.clients.jedis.Jedis] {

  import redis.clients.jedis.JedisPool
  import redis.clients.jedis.JedisPoolConfig

  /**
   * Using Jedis
   */
  private var pool: redis.clients.jedis.JedisPool = _

  override def initPool() = {
    try {
      val redisSource = dataSource.asInstanceOf[RedisDataSource]
      val poolConfig = new JedisPoolConfig()
      poolConfig.setMaxActive(10)
      poolConfig.setMaxIdle(1000)
      val host = redisSource.host
      val port = redisSource.port.toInt
      val password = redisSource.password

      pool = password match {
        case null | "" => new JedisPool(poolConfig, host, port, 15000)
        case _ => new JedisPool(poolConfig, host, port, 15000, password)
      }
    }
  }

  override def getConnection(): redis.clients.jedis.Jedis = {
    try {
      pool.getResource()
    } catch {
      case e: Throwable => {
        throw e
      }
    }
  }

  override def releaseConnection(connection: redis.clients.jedis.Jedis) = {
    pool.returnResourceObject(connection)
  }

  override def closePool() = {
    pool.destroy()
  }
}

class MongoConnectionPool(dataSource: DataSource)
  extends ConnectionPool[com.mongodb.casbah.MongoConnection] {

  /**
   * Using Casbah (manages its connection pool automatically)
   */
  private var conn: com.mongodb.casbah.MongoConnection = _

  override def initPool() = {
    try {
      val mongoSource = dataSource.asInstanceOf[MongodbDataSource]

      val uriObj = new com.mongodb.MongoURI(mongoSource.uri)
      conn = com.mongodb.casbah.MongoConnection(uriObj)
    }
  }

  override def getConnection(): com.mongodb.casbah.MongoConnection = conn

  override def releaseConnection(connection: com.mongodb.casbah.MongoConnection) = {
    //do nothing, leave it to MongoDB driver
  }

  override def closePool() = {
    //do nothing, leave it to MongoDB driver
  }
}

class OracleConnectionPool(dataSource: DataSource)
  extends ConnectionPool[java.sql.Connection] {

  import com.mchange.v2.c3p0.ComboPooledDataSource

  /**
   * Using c3p0
   */
  private var pool: ComboPooledDataSource = _

  override def initPool() = {
    try {
      val orclSource = dataSource.asInstanceOf[OracleDataSource]
      val jdbcUrl = "jdbc:oracle:thin:@" + orclSource.host + ":" + orclSource.port + ":" + orclSource.sid

      pool = new ComboPooledDataSource()
      pool.setDriverClass("oracle.jdbc.driver.OracleDriver")
      pool.setJdbcUrl(jdbcUrl)
      pool.setUser(orclSource.username)
      pool.setPassword(orclSource.password)
      pool.setMinPoolSize(1)
      pool.setAcquireIncrement(1)
      pool.setMaxPoolSize(20)
      pool.setLoginTimeout(30)
      pool.setMaxIdleTime(600)
      pool.setNumHelperThreads(20)
    } catch {
      case e: ClassCastException => {
        throw e
      }
      case e: Throwable => {
        throw e
      }
    }
  }

  override def getConnection(): java.sql.Connection = {
    try {
      pool.getConnection()
    } catch {
      case e: Throwable => {
        throw e
      }
    }
  }

  override def releaseConnection(connection: java.sql.Connection) = {
    try {
      connection.close()
    } catch { case _: Throwable => }
  }

  override def closePool() = {
    pool.close()
  }
}

class PostgresqlConnectionPool(dataSource: DataSource)
  extends ConnectionPool[java.sql.Connection] {

  import com.mchange.v2.c3p0.ComboPooledDataSource

  /**
   * Using c3p0
   */
  private var pool: ComboPooledDataSource = _

  override def initPool() = {
    try {
      val pgSource = dataSource.asInstanceOf[PostgresqlDataSource]
      val jdbcUrl = "jdbc:postgresql://" + pgSource.host + ":" + pgSource.port + "/" + pgSource.db

      pool = new ComboPooledDataSource()
      pool.setDriverClass("org.postgresql.Driver")
      pool.setJdbcUrl(jdbcUrl)
      pool.setUser(pgSource.username)
      pool.setPassword(pgSource.password)
      pool.setMinPoolSize(1)
      pool.setAcquireIncrement(1)
      pool.setMaxPoolSize(20)
      pool.setLoginTimeout(30)
      pool.setNumHelperThreads(20)
    } catch {
      case e: ClassCastException => {
        throw e
      }
      case e: Throwable => {
        throw e
      }
    }
  }

  override def getConnection(): java.sql.Connection = {
    try {
      pool.getConnection()
    } catch {
      case e: Throwable => {
        throw e
      }
    }
  }

  override def releaseConnection(connection: java.sql.Connection) = {
    try {
      connection.close()
    } catch { case _: Throwable => }
  }

  override def closePool() = {
    pool.close()
  }
}
