package modules.validation


trait ArticleValidator {
  def validate[T](data: T): Either[String, T]
}
