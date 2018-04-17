package modules.conversion

import io.circe.Json

trait JsonConverter {
  def toJson[T](data: T): Json
}
