package blackboard.monitor.connection

abstract trait DataSource {}

sealed case class PostgresqlDataSource(host: String, port: String, db: String, username: String, password: String)
  extends DataSource {
  override def equals(that: Any): Boolean = {
    if (that.isInstanceOf[PostgresqlDataSource]) {
      val dst = that.asInstanceOf[PostgresqlDataSource]
      host == dst.host && port == dst.port && db == dst.db
    } else {
      false
    }
  }

  override def hashCode: Int = {
    val key = "postgresql_" + host + "_" + port + "_" + db
    key.hashCode()
  }
}

sealed case class MongodbDataSource(uri: String) extends DataSource {
  override def equals(that: Any): Boolean = {
    if (that.isInstanceOf[MongodbDataSource]) {
      val dst = that.asInstanceOf[MongodbDataSource]
      uri == dst.uri
    } else {
      false
    }
  }

  override def hashCode: Int = {
    val key = "mongodb_" + uri
    key.hashCode()
  }
}

sealed case class OracleDataSource(host: String, port: String, username: String, password: String, sid: String)
  extends DataSource {
  override def equals(that: Any): Boolean = {
    if (that.isInstanceOf[OracleDataSource]) {
      val dst = that.asInstanceOf[OracleDataSource]
      host == dst.host && port == dst.port && sid == dst.sid && username == dst.username
    } else {
      false
    }
  }

  override def hashCode: Int = {
    val key = "oracle_" + host + "_" + port + "_" + sid + "_" + username
    key.hashCode()
  }
}

sealed case class RedisDataSource(host: String, port: String, password: String)
  extends DataSource {
  override def equals(that: Any): Boolean = {
    if (that.isInstanceOf[RedisDataSource]) {
      val dst = that.asInstanceOf[RedisDataSource]
      host == dst.host && port == dst.port
    } else {
      false
    }
  }

  override def hashCode: Int = {
    val key = "redis_" + host + "_" + port
    key.hashCode()
  }
}
