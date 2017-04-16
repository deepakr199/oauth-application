name := """resource-provider"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.mongodb.morphia"      % "morphia-logging-slf4j"     % "1.0.1"     exclude("org.slf4j", "slf4j-log4j12"),
  "org.mongodb.morphia"      % "morphia"                   % "1.0.1"     exclude("org.slf4j", "slf4j-log4j12"),
  "org.projectlombok"        % "lombok"                    % "1.16.12",
  "com.typesafe.play"        % "play-test_2.11"            % play.core.PlayVersion.current,
  "junit"                    % "junit"                     % "4.12",
  "org.mockito"              % "mockito-core"              % "1.10.19"
)


sbtPlugin := true

routesGenerator := InjectedRoutesGenerator

resolvers += Resolver.mavenLocal
resolvers += "central-maven" at "http://central.maven.org/maven2/"
resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"
