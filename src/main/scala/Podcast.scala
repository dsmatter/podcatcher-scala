import java.io.File
import scala.io.Source

class Podcast(val dir: File, val feedUrl: String) {
  val name = dir.getName
  val localEpisodes = dir.listFiles
}

object Podcast {
  def collect(dirName: String): Array[Podcast] = {
    def mkPodcast(dir: File) = {
      val feedUrlFile = new File(dir, "feed.url")

      // Try to read the feed url
      try {
        val feedUrl = Source.fromFile(feedUrlFile).getLines.next
        Some(new Podcast(dir, feedUrl))
      } catch {
        case e: Exception => None
      }
    }

    val dir = new File(dirName)
    dir.listFiles.filter(_.isDirectory).map(mkPodcast).flatten
  }
}
