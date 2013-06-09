object Util {
  def basename(url: String) = {
    val regex = "[^/]+$".r
    regex.findFirstIn(url) getOrElse url
  }
}
