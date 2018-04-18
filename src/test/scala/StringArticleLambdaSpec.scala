import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import model._
import modules.aws.S3
import modules.conversion.JsonConverter
import modules.parser.ArticleParser
import modules.validation.ArticleValidator
import org.scalatest.{Matchers, WordSpec}

class StringArticleLambdaSpec extends WordSpec with Matchers {
  val av = new ArticleValidator {
    override def validate[T](data: T): Either[String, T] = Right(data)
  }

  val ap = new ArticleParser {
    override def parse[A](data: A) = Right(Article(Title("some-title", 1), Nil))
  }

  val jc = new JsonConverter {
    override def toJson(data: Article): Either[String, Json] = Right(data.asJson)
  }

  val s3 = new S3 {
    override def save[T](data: T): Either[String, String] = Right("OK")
  }

  "Lambda" should {
    "save article" in {
      Lambda.stringArticleLambda.run(av, ap, jc, s3)("test article") shouldBe Right("OK")
    }

    "give an error on invalid input" in {
      val localAv = new ArticleValidator {
        override def validate[T](data: T) = Left("Invalid article input")
      }

      Lambda.stringArticleLambda.run(localAv, ap, jc, s3)("some invalid article") shouldBe Left("Invalid article input")
    }

    "give an error if json cannot be created" in {
      val localJc = new JsonConverter {
        override def toJson(data: Article) = Left("Cannot create json from article")
      }

      Lambda.stringArticleLambda.run(av, ap, localJc, s3)("some invalid article") shouldBe Left("Cannot create json from article")
    }

    "give an error if cannot save to s3" in {
      val localS3 = new S3 {
        override def save[T](data: T) = Left("cannot save the data")
      }

      Lambda.stringArticleLambda.run(av, ap, jc, localS3)("some invalid article") shouldBe Left("cannot save the data")
    }

    "give an error if cannot parse the article string" in {
      val localAp = new ArticleParser {
        override def parse[A](data: A) = Left("Cannot parse the article string")
      }

      Lambda.stringArticleLambda.run(av, localAp, jc, s3)("some invalid article") shouldBe Left("Cannot parse the article string")
    }
  }

}
