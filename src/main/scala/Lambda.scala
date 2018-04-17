import modules.aws.S3
import modules.conversion.JsonConverter
import modules.validation.ArticleValidator

trait Lambda {
  def run(av: ArticleValidator, jc: JsonConverter, s3: S3): String => Either[String, String] =
    article => for {
      a <- av.validate(article)
      aJson <- jc.toJson(a)
      saved <- s3.save(aJson)
    } yield saved

}

object Lambda{
  val stringArticleLambda = new Lambda{}
}


