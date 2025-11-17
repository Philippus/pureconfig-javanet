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

scalaVersion := "2.13.17"
crossScalaVersions += "3.3.7"

ThisBuild / versionScheme          := Some("semver-spec")
ThisBuild / versionPolicyIntention := Compatibility.BinaryCompatible

Compile / packageBin / packageOptions += Package.ManifestAttributes(
  "Automatic-Module-Name" -> "nl.gn0s1s.pureconfig.module.javanet"
)

Test / unmanagedSourceDirectories ++= {
  (Test / unmanagedSourceDirectories).value.map { dir =>
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 13)) => file(dir.getPath ++ "-2.13")
      case _             => file(dir.getPath ++ "-3+")
    }
  }
}

scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((2, 13)) => Seq("-Xsource:3", "-deprecation")
  case _             => Seq("-deprecation")
})

val pureConfigVersion = "0.17.9"

libraryDependencies += (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((2, 13)) => "com.github.pureconfig" %% "pureconfig"      % pureConfigVersion % Provided
  case _             => "com.github.pureconfig" %% "pureconfig-core" % pureConfigVersion % Provided
})

libraryDependencies ++= Seq(
  "commons-validator" % "commons-validator" % "1.10.1",
  "org.scalameta"    %% "munit"             % "1.2.1" % Test
)
