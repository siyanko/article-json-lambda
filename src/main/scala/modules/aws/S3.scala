package modules.aws

/**
  * Models interaction with AWS S3
  */
trait S3 {
  def save[T](data: T): Either[String, String]
}
