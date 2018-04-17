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
      override def toJson[T](data: T): Json = ???
    }

    val articleValidator = new ArticleValidator {
      override def validate[T](data: T): Either[String, T] = ???
    }

    val program: Article => Either[String, String] = article => for {
      a <- articleValidator.validate(article)
      aJson <- jsonConverter.toJson(a)
      saved <- s3.save(aJson)
    } yield saved

    val result = program("my small article")

    println(result)
  }
}
