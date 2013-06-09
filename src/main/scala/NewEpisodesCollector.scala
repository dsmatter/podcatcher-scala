import scala.xml._
import java.net.URL

object NewEpisodesCollector {

  def collect(podcast: Podcast): Seq[String] = {
    def isNewEpisode(url: String) = {
      val fileName = Util.basename(url)
      val locals = podcast.localEpisodes.map(_.getName)
      !locals.contains(fileName) && !locals.contains(fileName.replace(".torrent", ""))
    }

    val doc = XML.load(new URL(podcast.feedUrl))
    println("Checking " + podcast.name)
    val urls = (doc \\ "enclosure").map(_.attribute("url")).flatten
    urls.map(_.toString()).takeWhile(isNewEpisode)
  }
}
