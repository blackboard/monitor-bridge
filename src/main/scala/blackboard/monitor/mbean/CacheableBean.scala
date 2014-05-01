package blackboard.monitor.mbean

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import scala.collection.mutable.SynchronizedMap
import scala.compat.Platform

trait CacheableBean[T] {

  object MapMaker {
    def makeMap: Map[String, (Long, T)] = {
      new HashMap[String, (Long, T)] with SynchronizedMap[String, (Long, T)]
    }
  }

  private val beans: Map[String, (Long, T)] = MapMaker.makeMap

  def get(name: String, validateTime: Long): Option[T] = {
    val result = beans.get(name)
    result match {
      case Some(name) => {
        val duration = Platform.currentTime - result.get._1
        if (duration >= validateTime * 1000) {
          None
        } else {
          Some(result.get._2)
        }
      }
      case None => {
        None
      }
    }
  }

  def put(name: String, value: T) = {
    beans += name ->(Platform.currentTime, value)
  }
}