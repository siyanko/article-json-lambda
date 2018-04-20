import modules.aws.S3
import modules.conversion.JsonConverter
import modules.parser.ArticleParser
import modules.validation.ArticleValidator

trait Lambda {
  def run(av: ArticleValidator, ap: ArticleParser, jc: JsonConverter, s3: S3): String => Either[String, String] =
    articleString => for {
      as <- av.validate(articleString)
      article <- ap.parse(as)
      aJson <- jc.toJson(article)
      saved <- s3.save(aJson)
    } yield saved

}

object Lambda {
  val stringArticleLambda = new Lambda {}
}


