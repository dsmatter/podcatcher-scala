import org.specs2.mutable._
import org.specs2.mock._

import java.io.File

class NewEpisodesCollectorSpec extends Specification with Mockito {

  "newEpisodesCollector" should {
    "#newURLs" should {

      def generateURL(i: Int) = f"http://podcast.io/$i%02d.mp3"
      def generateTorrentURL(i: Int) = generateURL(i) + ".torrent"
      def generateFile(i: Int) = new File(f"$i%02d.mp3")

      "return all URLs if there are no local files" in {
        val podcast = mock[Podcast]
        podcast.localEpisodes returns Array.empty[File]

        val urls = (3 to 1 by -1) map generateURL
        val newUrls = NewEpisodesCollector.newURLs(podcast, urls)
        newUrls must_==(urls)
      }

      "return nothing if the newest file is already there" in {
        val podcast = mock[Podcast]
        podcast.localEpisodes returns Array(generateFile(3), generateFile(1))

        var urls = (3 to 1 by -1) map generateURL
        var newUrls = NewEpisodesCollector.newURLs(podcast, urls)
        newUrls must have length(0)

        urls = urls map { _ + ".torrent" }
        newUrls = NewEpisodesCollector.newURLs(podcast, urls)
        newUrls must have length(0)
      }

      "return 2 new URLs if necessary" in {
        val podcast = mock[Podcast]
        podcast.localEpisodes returns Array(generateFile(1), generateFile(2))

        var urls = (4 to 1 by -1) map generateURL
        var newUrls = NewEpisodesCollector.newURLs(podcast, urls)
        newUrls must_== urls.take(2)

        urls = urls map { _ + ".torrent" }
        newUrls = NewEpisodesCollector.newURLs(podcast, urls)
        newUrls must_== urls.take(2)
      }

      "return 2 new URLs if necessary (torrent locals)" in {
        val podcast = mock[Podcast]
        podcast.localEpisodes returns Array(generateFile(1), generateFile(2), new File("04.mp3.torrent"))

        var urls = (4 to 1 by -1) map generateURL
        var newUrls = NewEpisodesCollector.newURLs(podcast, urls)
        newUrls must_== urls.take(2)

        urls = urls map { _ + ".torrent" }
        newUrls = NewEpisodesCollector.newURLs(podcast, urls)
        newUrls must_== urls.take(2)
      }
    }
  }

}
