package blackboard.monitor.connection

object ConnectionPoolManager {
  private var pools: Map[DataSource, Any] = Map()

  def getConnectionPool[T](dataSource: DataSource): T = {
    pools.get(dataSource) match {
      case Some(pool) => pool.asInstanceOf[T]
      case None => {
        synchronized {
          if (!pools.contains(dataSource)) {
            val pool = dataSource match {
              case data: PostgresqlDataSource => {
                val pool = new PostgresqlConnectionPool(data)
                pool.initPool()
                pools += data -> pool
              }
              case data: OracleDataSource => {
                val pool = new OracleConnectionPool(data)
                pool.initPool()
                pools += data -> pool
              }
              case data: MongodbDataSource => {
                val pool = new MongoConnectionPool(data)
                pool.initPool()
                pools += data -> pool
              }
              case data: RedisDataSource => {
                val pool = new JedisConnectionPool(data)
                pool.initPool()
                pools += data -> pool
              }
            }
          }
          pools.get(dataSource).get.asInstanceOf[T]
        }
      }
    }
  }
}