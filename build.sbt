
//lazy val root = project.in(file(".")).
//  aggregate(appJS, appJVM).
//  settings(
//    publish := {},
//    publishLocal := {}
//  )

lazy val app = crossProject.in(file(".")).
  settings(
    scalaVersion := "2.12.7",
    name := "app",
    version := "0.1-SNAPSHOT",
    unmanagedSourceDirectories in Compile += baseDirectory.value  / "shared" / "src" / "main" / "scala",
    libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.6.7",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.6.6"
  ).
  jvmSettings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"   % "10.1.5",
      "com.typesafe.akka" %% "akka-stream" % "2.5.12"
    )
  ).
  jsSettings(
    // Add JS-specific settings here
  )

lazy val appJS = app.js

lazy val appJVM = app.jvm.settings(
  (resources in Compile) += (fastOptJS in (appJS, Compile)).value.data
)
