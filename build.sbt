import sbtassembly.AssemblyPlugin.defaultUniversalScript

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / organization := "com.github.cross-language-cpp"

lazy val djinni = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    name := "djinni",
    Defaults.itSettings,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.10",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "it,test",
    libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.0",
    libraryDependencies += "org.yaml" % "snakeyaml" % "1.29",
    libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.1",
    libraryDependencies += "commons-io" % "commons-io" % "2.11.0",
    assembly / assemblyOutputPath := { file("target/bin") / (assembly / assemblyJarName).value },
    assembly / assemblyJarName := s"${name.value}",
    assembly / assemblyOption := (assembly / assemblyOption).value.copy(prependShellScript = Some(defaultUniversalScript(shebang = false))),
    assembly / test := {}
  )
  inConfig(IntegrationTest)(org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings)
