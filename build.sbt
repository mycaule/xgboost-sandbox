import scalariform.formatter.preferences._

scalariformPreferences := scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(DanglingCloseParenthesis, Preserve)

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Ywarn-unused-import"
)

lazy val root = (project in file("."))
  .enablePlugins(GitVersioning, GitBranchPrompt, BuildInfoPlugin,
    ScalaUnidocPlugin, ParadoxPlugin, TutPlugin)
  .settings(
    inThisBuild(List(
      organization := "com.sandbox",
      scalaVersion := "2.12.4",
      version      := "0.2.0-SNAPSHOT"
    )),
    name := "xgboost-sandbox",
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.sandbox",
    buildInfoOptions += BuildInfoOption.BuildTime,
    libraryDependencies ++= Seq(
      "ml.dmlc" % "xgboost4j" % "0.72",
      "org.scalatest" %%  "scalatest" % "3.0.5" % Test,
      "com.spotify" %% "featran-core" % "0.2.0"
    )
  )
