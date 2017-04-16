import de.johoop.jacoco4sbt._

import JacocoPlugin._

jacoco.settings

parallelExecution in jacoco.Config := false

jacoco.outputDirectory in jacoco.Config := file("target/jacoco")

jacoco.reportFormats in jacoco.Config := Seq(HTMLReport("utf-8"))

jacoco.excludes in jacoco.Config := Seq("views*", "*Routes*", "controllers*routes*", "controllers*Reverse*", "controllers*javascript*", "controller*ref*", "model*")

jacoco.thresholds in jacoco.Config := Thresholds(instruction = 40, method = 40, branch = 40, complexity = 40, line = 40, clazz = 40)
