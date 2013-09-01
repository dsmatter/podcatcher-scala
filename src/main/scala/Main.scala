import scala.sys.process._

object Main {
  val positiveAnswers = List("y", "Y", "")

  def main(args: Array[String]) {
    println("Booted")
    val nameFilter = if (args.length > 0) args(0) else ""

    // Use a parallel collection
    val podcasts = Podcast.collect(".", nameFilter).par

    // Collect new episodes
    val newEpisodes = podcasts.map { (podcast) =>
      try {
        NewEpisodesCollector.collect(podcast)
      } catch {
        case e: Exception => {
          e.printStackTrace
          new NewEpisodes(podcast, Seq.empty[String])
        }
      }
    }

    // Check if there are any new episodes
    if (newEpisodes.forall(_.isEmpty)) {
      println("Maybe next time :)")
      return
    }

    // Print out the new episodes
    newEpisodes.filterNot(_.isEmpty).foreach(printEpisodes)

    // Ask for download permission
    print("Download all? [Y/n] > ")
    val answer = readLine()

    if (positiveAnswers.contains(answer.trim)) {
      // Spawn a bunch of download processes and wait for all to exit
      newEpisodes.flatMap(downloadEpisodes).foreach(_.exitValue)
    } else {
      println("kthxbye")
    }
  }

  private def printEpisodes(episodes: NewEpisodes) {
    println("New episodes for " + episodes.podcast.name + ":")
    episodes.urls.map(Util.basename).foreach(println)
  }

  private def downloadEpisodes(episodes: NewEpisodes): Seq[Process] = {
    episodes.urls.map((url) =>
      Seq("aria2c", "--file-allocation=none", "--seed-time=0", "-d",
          episodes.podcast.dir.getAbsolutePath, "-o", Util.basename(url), url).run()
    )
  }
}
