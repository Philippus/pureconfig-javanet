name := "pureconfig-javanet"
organization := "nl.gn0s1s"
version := "0.0.1"
startYear := Some(2021)
homepage := Some(url("https://github.com/philippus/pureconfig-javanet"))
licenses += ("Mozilla Public License, version 2.0", url("https://www.mozilla.org/MPL/2.0/"))

crossScalaVersions := List("2.13.5")
scalaVersion := crossScalaVersions.value.last

scalacOptions += "-deprecation"

libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig" % "0.14.1" % Provided,
  "commons-validator" % "commons-validator" % "1.7",
  "org.scalameta" %% "munit" % "0.7.23" % Test
)

testFrameworks += new TestFramework("munit.Framework")

pomExtra :=
  <scm>
    <url>git@github.com:Philippus/pureconfig-javanet.git</url>
    <connection>scm:git@github.com:Philippus/pureconfig-javanet.git</connection>
  </scm>
  <developers>
    <developer>
      <id>philippus</id>
      <name>Philippus Baalman</name>
      <url>https://github.com/philippus</url>
    </developer>
  </developers>
