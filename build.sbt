
/*
 build.sbt adapted from https://github.com/pbassiner/sbt-multi-project-example/blob/master/build.sbt
*/


name := "rd-validation-service"
ThisBuild / organization := "de.dnpm.dip"
ThisBuild / scalaVersion := "2.13.13"
ThisBuild / version      := "1.0-SNAPSHOT"


//-----------------------------------------------------------------------------
// PROJECTS
//-----------------------------------------------------------------------------

lazy val global = project
  .in(file("."))
  .settings(
    settings,
    publish / skip := true
  )
  .aggregate(
     api,
     impl
  )


lazy val api = project
  .settings(
    name := "rd-validation-service-api",
    settings,
    libraryDependencies ++= Seq(
      dependencies.scalatest,
      dependencies.service_base,
      dependencies.rd_model
    )
  )


lazy val impl = project
  .settings(
    name := "rd-validation-service-impl",
    settings,
    libraryDependencies ++= Seq(
      dependencies.scalatest,
      dependencies.rd_generators,
      dependencies.icd10gm,
      dependencies.icd_catalogs,
      dependencies.hpo,
      dependencies.omim,
      dependencies.orphanet,
      dependencies.hgnc
    )
  )
  .dependsOn(
    api
  )



//-----------------------------------------------------------------------------
// DEPENDENCIES
//-----------------------------------------------------------------------------

lazy val dependencies =
  new {
    val scalatest      = "org.scalatest"  %% "scalatest"           % "3.2.17" % Test
    val service_base   = "de.dnpm.dip"    %% "service-base"        % "1.0-SNAPSHOT"
    val rd_model       = "de.dnpm.dip"    %% "rd-dto-model"        % "1.0-SNAPSHOT"
    val rd_generators  = "de.dnpm.dip"    %% "rd-dto-generators"   % "1.0-SNAPSHOT" % Test
    val icd10gm        = "de.dnpm.dip"    %% "icd10gm-impl"        % "1.0-SNAPSHOT" % Test
    val icd_catalogs   = "de.dnpm.dip"    %% "icd-claml-packaged"  % "1.0-SNAPSHOT" % Test
    val hgnc           = "de.dnpm.dip"    %% "hgnc-gene-set-impl"  % "1.0-SNAPSHOT" % Test
    val hpo            = "de.dnpm.dip"    %% "hp-ontology"         % "1.0-SNAPSHOT" % Test
    val omim           = "de.dnpm.dip"    %% "omim-catalog"        % "1.0-SNAPSHOT" % Test
    val orphanet       = "de.dnpm.dip"    %% "orphanet-ordo"       % "1.0-SNAPSHOT" % Test
  }


//-----------------------------------------------------------------------------
// SETTINGS
//-----------------------------------------------------------------------------

lazy val settings = commonSettings


lazy val compilerOptions = Seq(
  "-encoding", "utf8",
  "-unchecked",
  "-feature",
  "-language:postfixOps",
  "-Xfatal-warnings",
  "-deprecation",
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq("Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository") ++
    Resolver.sonatypeOssRepos("releases") ++
    Resolver.sonatypeOssRepos("snapshots")
)

