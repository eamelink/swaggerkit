import sbt._
import Keys._

object Settings {
  val name = "swaggerkit" 

  val buildSettings = Project.defaultSettings ++ Seq(
    version := "0.1.1",
    scalaVersion := "2.9.1",
    organization := "net.eamelink"
  )
}

object Resolvers {
  val typesafeRepo = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
}

object Dependencies {
  lazy val play = "play" %% "play" % "2.0"
  lazy val specs = "org.specs2" %% "specs2" % "1.9" % "test"
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
