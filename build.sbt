// build.sbt adapted from https://github.com/pbassiner/sbt-multi-project-example/blob/master/build.sbt

import scala.util.Properties.envOrElse


name := "rd-validation-service"
ThisBuild / organization := "de.dnpm.dip"
ThisBuild / scalaVersion := "2.13.16"
ThisBuild / version      := envOrElse("VERSION","1.1.0")

val ownerRepo  = envOrElse("REPOSITORY","dnpm-dip/rd-validation-service").split("/")
ThisBuild / githubOwner      := ownerRepo(0)
ThisBuild / githubRepository := ownerRepo(1)


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
      dependencies.orphanet,
      dependencies.alpha_id_se,
      dependencies.hgnc,
      dependencies.atc_impl,
      dependencies.atc_catalogs
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
    val scalatest      = "org.scalatest"  %% "scalatest"              % "3.2.19" % Test
    val service_base   = "de.dnpm.dip"    %% "service-base"           % "1.1.0"
    val rd_model       = "de.dnpm.dip"    %% "rd-dto-model"           % "1.1.0"
    val rd_generators  = "de.dnpm.dip"    %% "rd-dto-generators"      % "1.1.0" % Test
    val icd10gm        = "de.dnpm.dip"    %% "icd10gm-impl"           % "1.1.1" % Test
    val icd_catalogs   = "de.dnpm.dip"    %% "icd-claml-packaged"     % "1.1.1" % Test
    val hgnc           = "de.dnpm.dip"    %% "hgnc-gene-set-impl"     % "1.1.0" % Test
    val hpo            = "de.dnpm.dip"    %% "hp-ontology"            % "1.1.0" % Test
    val orphanet       = "de.dnpm.dip"    %% "orphanet-ordo"          % "1.1.0" % Test
    val alpha_id_se    = "de.dnpm.dip"    %% "alpha-id-se"            % "1.1.0" % Test
    val atc_impl       = "de.dnpm.dip"    %% "atc-impl"               % "1.1.0" % Test
    val atc_catalogs   = "de.dnpm.dip"    %% "atc-catalogs-packaged"  % "1.1.0" % Test
  }


//-----------------------------------------------------------------------------
// SETTINGS
//-----------------------------------------------------------------------------

lazy val settings = commonSettings


// Compiler options from: https://alexn.org/blog/2020/05/26/scala-fatal-warnings/
lazy val compilerOptions = Seq(
  // Feature options
  "-encoding", "utf-8",
  "-explaintypes",
  "-feature",
  "-language:existentials",
  "-language:experimental.macros",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Ymacro-annotations",

  // Warnings as errors!
  "-Xfatal-warnings",

  // Linting options
  "-unchecked",
  "-Xcheckinit",
  "-Xlint:adapted-args",
  "-Xlint:constant",
  "-Xlint:delayedinit-select",
  "-Xlint:deprecation",
  "-Xlint:doc-detached",
  "-Xlint:inaccessible",
  "-Xlint:infer-any",
  "-Xlint:missing-interpolator",
  "-Xlint:nullary-unit",
  "-Xlint:option-implicit",
  "-Xlint:package-object-classes",
  "-Xlint:poly-implicit-overload",
  "-Xlint:private-shadow",
  "-Xlint:stars-align",
  "-Xlint:type-parameter-shadow",
  "-Wdead-code",
  "-Wextra-implicit",
  "-Wnumeric-widen",
  "-Wunused:imports",
  "-Wunused:locals",
  "-Wunused:patvars",
  "-Wunused:privates",
  "-Wunused:implicits",
  "-Wvalue-discard",
)


lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.githubPackages("dnpm-dip"),
    Resolver.githubPackages("KohlbacherLab"),
    Resolver.sonatypeCentralSnapshots
  )

)

