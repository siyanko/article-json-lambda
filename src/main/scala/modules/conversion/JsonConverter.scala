package modules.conversion

import io.circe.Json

trait JsonConverter {
  def toJson(data: String): Either[String, Json]
}
