package modules.validation


trait ArticleValidator {
  def validate(data: String): Either[String, String]
}
