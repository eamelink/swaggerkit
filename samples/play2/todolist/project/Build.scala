import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName = "todolist"
  val appVersion = "1.1"

  val appDependencies = Seq(
    "org.squeryl" %% "squeryl" % "0.9.5-7"
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    routesImport ++= Seq("models._", "util.binding.binders._", "api.sorting._")) dependsOn (swaggerkitPlay2)

  lazy val swaggerkitPlay2 = ProjectRef(file("../../.."), "swaggerkit-play2")
}
