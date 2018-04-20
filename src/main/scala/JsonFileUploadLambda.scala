import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import model.{Article, Paragraph, Title}
import modules.aws.S3
import modules.conversion.JsonConverter
import modules.parser.ArticleParser
import modules.validation.ArticleValidator

import scala.util.{Failure, Success, Try}


class JsonFileUploadLambda {
  val av: ArticleValidator = new ArticleValidator {
    override def validate(data: String) =
      if (data.isEmpty) Left("Article is empty")
      else Right(data)
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

  val s3: S3 = new S3 {
    val s3Client: AmazonS3 =
      AmazonS3ClientBuilder.standard()
        .withRegion(Regions.EU_WEST_1)
        .build()

    override def save(data: Json) = {
      Try {
        s3Client.putObject("test-articles", "test-file.json", data.noSpaces)
      } match {
        case Success(_) => Right("OK")
        case Failure(err: Throwable) => Left(err.getMessage)
        case Failure(_) => Left("Something went wrong during saving to s3")
      }
    }
  }

  val lambda: (String) => Either[String, String] = Lambda.stringArticleLambda.run(av, ap, jc, s3)

  def handle(context: Context): Unit = {
    lambda("#Article \n This is my small article") match {
      case Right(_) => println("File saved successfully")
      case Left(err) => println(s"ERROR: $err")
    }
  }

}
