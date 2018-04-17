import modules.aws.S3
import modules.conversion.JsonConverter
import modules.validation.ArticleValidator
import cats.syntax.either._
import io.circe.Json

object Main {

  type Article = String

  def main(args: Array[String]): Unit = {

    val s3 = new S3 {
      override def save[T](data: T) = ???
    }

    val jsonConverter = new JsonConverter {
      override def toJson(data: String): Either[String, Json] = ???
    }

    val articleValidator = new ArticleValidator {
      override def validate[T](data: T): Either[String, T] = ???
    }

    import Lambda._
    val result = stringArticleLambda.run(articleValidator, jsonConverter, s3)("my small article")

    println(result)
  }
}
