package modules.parser
import model._

trait ArticleParser {
  def parse(data: String): Either[String, Article]
}
