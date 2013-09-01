import java.io.File
import scala.io.Source

class Podcast(val dir: File, val feedUrl: String) {
  val name = dir.getName
  def localEpisodes = dir.listFiles
}

object Podcast {
  def collect(dirName: String, nameFilter: String = ""): Array[Podcast] = {
    val dir = new File(dirName)
    dir.listFiles
       .filter(_.isDirectory)
       .filter(dir => nameFilter.toLowerCase.r.findFirstIn(dir.getName.toLowerCase).isDefined)
       .flatMap(fromDir)
  }

  def fromDir(dir: File): Option[Podcast] = {
    val feedUrlFile = new File(dir, "feed.url")

    // Try to read the feed url
    try {
      val feedUrl = Source.fromFile(feedUrlFile).getLines.next
      Some(new Podcast(dir, feedUrl))
    } catch {
      case e: Exception => None
    }
  }
}
