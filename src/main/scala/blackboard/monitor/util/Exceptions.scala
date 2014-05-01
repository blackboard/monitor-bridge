package blackboard.monitor.util

case class PropertyNotSetException(key: String) extends Exception("Property: " + key + " is not set")