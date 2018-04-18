package modules.conversion
import model._
import io.circe.Json

trait JsonConverter {
  def toJson(data: Article): Either[String, Json]
}
