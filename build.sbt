scalaVersion := "2.10.3"

organization := "blackboard"

name := "monitor-bridge"

version := "1.0-SNAPSHOT"

libraryDependencies += "org.slf4j" % "log4j-over-slf4j" % "1.7.2"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.7" % "compile"

libraryDependencies += "ch.qos.logback" % "logback-core" % "1.0.7" % "compile"

libraryDependencies += "redis.clients" % "jedis" % "2.1.0"

libraryDependencies += "org.mongodb" %% "casbah" % "2.5.0"

libraryDependencies += "postgresql" % "postgresql" % "9.1-901.jdbc4"

libraryDependencies += "com.mchange" % "c3p0" % "0.9.2-pre4"

libraryDependencies += "com.oracle" % "oracle-jdbc" % "11.2.0.2"

libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.9.2"

libraryDependencies += "net.debasishg" %% "sjson" % "0.19"

libraryDependencies += "org.bouncycastle" % "bcprov-ext-jdk15on" % "1.47"

libraryDependencies += "com.typesafe" % "config" % "1.0.0"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.1.3" % "test"

libraryDependencies += "org.springframework" % "spring-beans" % "3.1.0.RELEASE"

libraryDependencies += "org.springframework" % "spring-context" % "3.1.0.RELEASE"

libraryDependencies += "org.springframework" % "spring-web" % "3.1.0.RELEASE"

libraryDependencies += "blackboard.cloud" % "cloud-common" % "3.2.1" notTransitive()

libraryDependencies += "blackboard.cloud" %% "scala-common" % "14.01.1.1" notTransitive()

libraryDependencies += "blackboard.cloud" % "cloud-services-base-rest-api" % "14.01.1.1" notTransitive()

unmanagedSourceDirectories in Compile <+= baseDirectory( _ / "conf" )

unmanagedSourceDirectories in Compile <+= baseDirectory( _ / "src" / "main" / "resources" )

unmanagedSourceDirectories in Compile <+= baseDirectory( _ / "src" / "test" / "resources" )

resolvers += "sonatype releases"  at "https://oss.sonatype.org/content/repositories/releases"

resolvers += "sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "typesafe snapshots" at "http://repo.typesafe.com/typesafe/snapshots"

resolvers += "typesafe releases" at "http://repo.typesafe.com/typesafe/releases"

resolvers += "repo.codahale.com" at "http://repo.codahale.com"

resolvers += "Bb Internal - releases" at "http://maven.pd.local/content/repositories/releases"

resolvers += "Bb Internal - Snapshots" at "http://maven.pd.local/content/repositories/snapshots"

resolvers += "Bb Internal - Third Party" at "http://maven.pd.local/content/repositories/thirdparty"