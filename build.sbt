name         := "pureconfig-javanet"
organization := "nl.gn0s1s"
startYear    := Some(2021)
homepage     := Some(url("https://github.com/philippus/pureconfig-javanet"))
licenses += ("MPL-2.0", url("https://www.mozilla.org/MPL/2.0/"))

developers := List(
  Developer(
    id = "philippus",
    name = "Philippus Baalman",
    email = "",
    url = url("https://github.com/philippus")
  )
)

crossScalaVersions := List("2.13.14")
scalaVersion       := crossScalaVersions.value.last

ThisBuild / versionScheme          := Some("semver-spec")
ThisBuild / versionPolicyIntention := Compatibility.BinaryCompatible

Compile / packageBin / packageOptions += Package.ManifestAttributes(
  "Automatic-Module-Name" -> "nl.gn0s1s.pureconfig.module.javanet"
)

scalacOptions += "-deprecation"

libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig"        % "0.17.6" % Provided,
  "commons-validator"      % "commons-validator" % "1.8.0",
  "org.scalameta"         %% "munit"             % "1.0.0"  % Test
)
