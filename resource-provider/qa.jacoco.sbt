import de.johoop.jacoco4sbt._

import JacocoPlugin._

jacoco.settings

parallelExecution in jacoco.Config := false

jacoco.outputDirectory in jacoco.Config := file("target/jacoco")

jacoco.reportFormats in jacoco.Config := Seq(HTMLReport("utf-8"))

jacoco.excludes in jacoco.Config := Seq("views*", "*Routes*", "controllers*routes*", "controllers*Reverse*", "controllers*javascript*", "controller*ref*", "model*")

jacoco.thresholds in jacoco.Config := Thresholds(instruction = 30, method = 30, branch = 0, complexity = 30, line = 30, clazz = 30)
