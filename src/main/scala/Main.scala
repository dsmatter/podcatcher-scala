import scala.sys.process._

object Main {
  val positiveAnswers = List("y", "Y", "")

  def main(args: Array[String]) {
    println("Booted")

    // Use a parallel collection
    val podcasts = Podcast.collect(".").par

    // Collect new episodes
    val episodeUrls = podcasts.map((podcast) =>
      try {
        NewEpisodesCollector.collect(podcast)
      } catch {
        case e: Exception => Seq.empty[String]
      }
    )

    // Check if there are any new episodes
    if (episodeUrls.flatten.isEmpty) {
      println("Maybe next time :)")
      return
    }

    // Print out the new episodes
    val podcastsWithEpisodes = podcasts.zip(episodeUrls)
    podcastsWithEpisodes.foreach{
      case (podcast, urls) => printNewEpisodes(podcast, urls)
    }

    // Ask for download permission
    print("Download all? [Y/n] > ")
    val answer = readLine()

    // Act accordingly
    if (positiveAnswers.contains(answer.trim)) {
      // Spawn a bunch of download processes and wait for all to exit
      podcastsWithEpisodes.map{
        case (podcast, urls) => downloadEpisodes(podcast, urls)
      }.flatten.foreach(_.exitValue)
    } else {
      println("kthxbye")
    }
  }

  private def printNewEpisodes(podcast: Podcast, urls: Seq[String]) {
    if (urls.isEmpty) {
      return
    }
    println("New episodes for " + podcast.name + ":")
    urls.map(Util.basename).foreach(println)
  }

  private def downloadEpisodes(podcast: Podcast, urls: Seq[String]): Seq[Process] = {
    urls.map((url) =>
      Seq("aria2c", "--file-allocation=none", "--seed-time=0", "-d",
          podcast.dir.getAbsolutePath, "-o", Util.basename(url), url).run()
    )
  }
}
