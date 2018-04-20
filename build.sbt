name := "article-json-lambda"

version := "1.0"

scalaVersion := "2.12.5"

libraryDependencies ++= Seq(
  //circe
  "io.circe" %% "circe-core" % "0.9.3",
  "io.circe" %% "circe-generic" % "0.9.3",
  "io.circe" %% "circe-parser" % "0.9.3",

  //cats
  "org.typelevel" %% "cats-core" % "1.0.1",

  //aws
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.313",
  "com.amazonaws" % "aws-lambda-java-core" % "1.2.0",

"org.scalatest" %% "scalatest" % "3.0.5" % Test
)

scalacOptions += "-Ypartial-unification"
    