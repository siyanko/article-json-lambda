
package object model {

  case class Article(title: Title, content: List[Paragraph])

  sealed trait Paragraph{
    def typeName: String
  }

  case class Text(value: String) extends Paragraph{
    override def typeName: String = "text"
  }

  case class Title(value: String, level: Int) extends Paragraph{
    override def typeName: String = "title"
  }

  object Paragraph {
    import utils._
    def apply(s: String): Paragraph = if (s.contains('#')) {
      val title = skip('#')(s)
      val titleLevel = occurrenceOf('#')(s)
      Title(title, titleLevel)
    } else Text(s)
  }

  object utils {
    def skip(symbols: Char*): String => String = _.toList.filterNot(symbols.contains).mkString

    def occurrenceOf(symbol: Char): String => Int = _.toList.filter(_ == symbol).size
  }
}
