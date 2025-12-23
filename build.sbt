import sbtassembly.AssemblyPlugin.defaultUniversalScript

ThisBuild / scalaVersion := "2.12.20"
ThisBuild / organization := "com.github.cross-language-cpp"

ThisBuild / semanticdbEnabled := true // enable SemanticDB
ThisBuild / semanticdbVersion := "4.13.10" // latest available version

// Keep this as a reminder, we will use that later
// ThisBuild / scalacOptions += "-Ywarn-unused"
// us: sbt 'set ThisBuild / scalacOptions += "-Ywarn-unused"' scalafix
// until the last 2 unused warnings are fixed
ThisBuild / scalacOptions ++= Seq("-deprecation", "-unchecked")

val binExt = if (System.getProperty("os.name").startsWith("Windows")) ".bat" else ""
lazy val djinni = (project in file("."))
  .configs(IntegrationTest)
  .enablePlugins(ScalafixPlugin) // enable Scalafix for this project
  .settings(
    name := "djinni",
    Defaults.itSettings,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.10",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "it,test",
    libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.0",
    libraryDependencies += "org.yaml" % "snakeyaml" % "1.29",
    libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.1",
    libraryDependencies += "commons-io" % "commons-io" % "2.11.0",
    assembly / assemblyOutputPath := {
      val dir = file("target/bin")
      IO.createDirectory(dir)
      dir / s"${(assembly / assemblyJarName).value}${binExt}"
    },
    assembly / assemblyJarName := s"${name.value}",
    assembly / assemblyPrependShellScript := Some(defaultUniversalScript(shebang = binExt.isEmpty)),
    assembly / test := {}
  )
