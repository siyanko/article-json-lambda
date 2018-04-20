import io.circe.generic.auto._
import io.circe.syntax._
import model.{Article, Paragraph, Title}
import modules.aws.S3
import modules.conversion.JsonConverter
import modules.parser.ArticleParser
import modules.validation.ArticleValidator

import scala.util.{Failure, Success, Try}

object Main {

  def main(args: Array[String]): Unit = {

    //TODO: implement article validation
    val av: ArticleValidator = new ArticleValidator {
      override def validate[T](data: T) = Right(data)
    }

    val ap: ArticleParser = new ArticleParser {
      def skip(symbols: Char*): String => String = _.toList.filterNot(symbols.contains).mkString

      def occurrenceOf(symbol: Char): String => Int = _.toList.filter(_ == symbol).size

      override def parse(data: String): Either[String, Article] = data.split("\n").toList match {
        case Nil => Left("Could not parse data")

        case h :: tail =>
          val title = skip('#')(h)
          val titleLevel = occurrenceOf('#')(h)
          val content = tail.filterNot(_.isEmpty).map(s => Paragraph(s))

          Right(Article(Title(title, titleLevel), content))
      }

    }

    val jc: JsonConverter = new JsonConverter {
      override def toJson(data: Article) = Try(data.asJson) match {
        case Success(json) => Right(json)
        case Failure(err: Throwable) => Left(err.getMessage)
        case Failure(_) => Left("Could not make json out of " + data)
      }
    }

    //TODO: implement saving files to s3
    val s3: S3 = new S3 {
      override def save[T](data: T) = Right("OK")
    }

    val lambda: (String) => Either[String, String] = Lambda.stringArticleLambda.run(av, ap, jc, s3)

    println(lambda("#Article \n This is my small article"))
  }

}
