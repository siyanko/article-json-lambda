package modules.parser
import model._

trait ArticleParser {
  def parse[A](data: A): Either[String, Article]
}
