object Util {
  def basename(url: String) = {
    val regex = "[^/]+/*$".r
    regex.findFirstIn(url) match {
      case Some(name) => name.replace("/", "")
      case None => url
    }
  }
}
