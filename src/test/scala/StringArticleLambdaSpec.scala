import io.circe.Json
import modules.aws.S3
import modules.conversion.JsonConverter
import modules.validation.ArticleValidator
import org.scalatest.{Matchers, WordSpec}

class StringArticleLambdaSpec extends WordSpec with Matchers {
  val av = new ArticleValidator {
    override def validate[T](data: T): Either[String, T] = Right(data)
  }

  val jc = new JsonConverter {
    override def toJson(data: String): Either[String, Json] = Right(Json.fromString(data))
  }

  val s3 = new S3 {
    override def save[T](data: T): Either[String, String] = Right("OK")
  }

  "Lambda" should {
    "save article" in {
      Lambda.stringArticleLambda.run(av, jc, s3)("test article") shouldBe Right("OK")
    }

    "give an error on invalid input" in {
      val localAv = new ArticleValidator {
        override def validate[T](data: T) = Left("Invalid article input")
      }

      Lambda.stringArticleLambda.run(localAv, jc, s3)("some invalid article") shouldBe Left("Invalid article input")
    }

    "give an error if json cannot be created" in {
      val localJc = new JsonConverter {
        override def toJson(data: String) = Left("Cannot create json from article")
      }

      Lambda.stringArticleLambda.run(av, localJc, s3)("some invalid article") shouldBe Left("Cannot create json from article")
    }

    "give an error if cannot save to s3" in {
      val localS3 = new S3 {
        override def save[T](data: T) = Left("cannot save the data")
      }

      Lambda.stringArticleLambda.run(av, jc, localS3)("some invalid article") shouldBe Left("cannot save the data")
    }
  }

}
