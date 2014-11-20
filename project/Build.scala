import sbt._
import Keys._

object Settings {
  val name = "swaggerkit" 

  val buildSettings = Defaults.coreDefaultSettings ++ Seq(
    version := "0.4-SNAPSHOT",
    crossScalaVersions := Seq("2.10.4", "2.11.4"),
    organization := "net.eamelink"
  )
}

object Resolvers {
  val typesafeRepo = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
}

object Dependencies {
  lazy val play = "com.typesafe.play" %% "play" % "2.3.6"
  lazy val specs = "org.specs2" %% "specs2" % "2.4.11" % "test"
}

object ApplicationBuild extends Build {
  import Settings._
  import Resolvers._
  import Dependencies._

  lazy val root = Project(name, file("."), settings = buildSettings ++ Seq(
    publish := {})
  ) aggregate (core, play2)

  lazy val core = Project(name + "-core", file("core"), settings = buildSettings ++ Seq(
    publishTo <<= version { (v: String) => 
      val path = if(v.trim.endsWith("SNAPSHOT")) "snapshots-public" else "releases-public"
      Some(Resolver.url("Lunatech Artifactory", new URL("http://artifactory.lunatech.com/artifactory/%s/" format path)))
    },
    resolvers := Seq(typesafeRepo),
    libraryDependencies ++= Seq(specs)))

  lazy val play2 = Project(name + "-play2", file("play2"), settings = buildSettings ++ Seq(
    publishTo <<= version { (v: String) => 
      val path = if(v.trim.endsWith("SNAPSHOT")) "snapshots-public" else "releases-public"
      Some(Resolver.url("Lunatech Artifactory", new URL("http://artifactory.lunatech.com/artifactory/%s/" format path)))
    },
    resolvers := Seq(typesafeRepo),
    libraryDependencies ++= Seq(play, specs))) dependsOn (core)

}
