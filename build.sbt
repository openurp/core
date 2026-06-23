import org.openurp.parent.Dependencies.*
import org.openurp.parent.Settings.*

ThisBuild / version := "0.4.22-SNAPSHOT"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/openurp/core"),
    "scm:git@github.com:openurp/core.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "chaostone",
    name = "Tihua Duan",
    email = "duantihua@gmail.com",
    url = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "OpenURP Core Library"
ThisBuild / homepage := Some(url("http://openurp.github.io/core/index.html"))

val apiVer = "1.4.7"

val openurp_edu_api = "org.openurp.edu" % "openurp-edu-api" % apiVer
val openurp_std_api = "org.openurp.std" % "openurp-std-api" % apiVer

lazy val root = (project in file("."))
  .settings(
    common,
    name := "openurp-root",
    organization := "org.openurp")
  .aggregate(edu, std)

lazy val edu = (project in file("edu"))
  .settings(
    name := "openurp-edu-core",
    organization := "org.openurp.edu",
    common,
    libraryDependencies ++= Seq(openurp_edu_api, openurp_std_api),
    libraryDependencies ++= Seq(beangle_ems_app),
    libraryDependencies ++= Seq(beangle_cdi, beangle_security)
  )

lazy val std = (project in file("std"))
  .settings(
    name := "openurp-std-core",
    organization := "org.openurp.std",
    common,
    libraryDependencies ++= Seq(openurp_std_api, openurp_edu_api),
    libraryDependencies ++= Seq(beangle_ems_app)
  ).dependsOn(edu)

publish / skip := true
