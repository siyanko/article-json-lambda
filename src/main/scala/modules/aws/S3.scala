package modules.aws

import io.circe.Json

/**
  * Models interaction with AWS S3
  */
trait S3 {
  def save(data: Json): Either[String, String]
}
