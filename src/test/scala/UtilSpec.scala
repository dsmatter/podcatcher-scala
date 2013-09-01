import org.specs2.mutable._

class UtilSpec extends Specification {
  "basename extractor" should {
    def runSamples(samples: Map[String, String]) = {
      foreachWhen(samples) { case (testee, expected) =>
        Util.basename(testee) must be_==(expected)
      }
    }

    "work with absolute paths" in {
      runSamples(Map(
        "/usr/local/bin/test" -> "test",
        "/home/user/ls.dat" -> "ls.dat"
      ))
    }

    "work with relative paths" in {
      runSamples(Map(
        "home/ls.txt" -> "ls.txt",
        "local/bin/foo" -> "foo"
      ))
    }

    "work with file names" in {
      runSamples(Map(
        "test" -> "test",
        "foo.bar" -> "foo.bar"
      ))
    }

    "work with URIs" in {
      runSamples(Map(
        "http://test.example.com/path/to/file" -> "file",
        "ftp://test.example.com/path/to/file" -> "file"
      ))
    }

    "work with trailing slashes" in {
      runSamples(Map(
        "/test/foo/" -> "foo",
        "test/foo/" -> "foo",
        "foo/" -> "foo",
        "http://example.com/foo/" -> "foo"
      ))
    }
  }
}

