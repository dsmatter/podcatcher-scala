import scala.xml._
import java.net.URL

case class NewEpisodes(podcast: Podcast, urls: Seq[String]) {
  def isEmpty = urls.isEmpty
}

object NewEpisodesCollector {

  def collect(podcast: Podcast): NewEpisodes = {
    val doc = XML.load(new URL(podcast.feedUrl))
    println("Checking " + podcast.name)

    val urls = (doc \\ "enclosure").flatMap(_.attribute("url")).map(_.toString)
    new NewEpisodes(podcast, newURLs(podcast, urls))
  }

  def newURLs(podcast: Podcast, urls: Seq[String]): Seq[String] = {
    val localFileNames = podcast.localEpisodes.map(_.getName)
    urls.takeWhile { url =>
      val urlFileName = Util.basename(url)
      !localFileNames.exists { _ == urlFileName.replace(".torrent", "") }
    }
  }
}
