package com.minosiants.pencil
import java.nio.file.{ Path, Paths }

import com.minosiants.pencil.data._
import com.minosiants.pencil.protocol._
import org.specs2.mutable.Specification
import com.minosiants.pencil.protocol.ContentType
import org.specs2.matcher.MatchResult

class ContentTypeFinderSpec extends Specification {

  def path(filename: String): Path = {
    Paths.get(getClass.getClassLoader.getResource(filename).toURI)
  }

  def find(
      filename: String,
      expected: ContentType
  ): MatchResult[Either[Throwable, ContentType]] = {

    Files
      .inputStream(path(filename))
      .use { is =>
        ContentTypeFinder
          .findType(is)
      }
      .attempt
      .unsafeRunSync() must beRight(expected)

  }
  "ContentTypeFinder" should {

    "find ascii content type" in {
      find("files/ascii-sample.txt", ContentType.`text/plain`)
    }

    "find html content type" in {
      find("files/html-sample.html", ContentType.`text/html`)
    }

    "find png content type" in {
      find("files/image-sample.png", ContentType.`image/png`)
    }

    "find gif content type" in {
      find("files/gif-sample.gif", ContentType.`image/gif`)
    }

    "find jpg content type" in {
      find("files/jpeg-sample.jpg", ContentType.`image/jpeg`)
    }

    "find pdf content type" in {
      find("files/rfc2045.pdf", ContentType.`application/pdf`)
    }

    "not find file" in {

      val f = Paths.get("files/!!!jpeg-sample.jpg")

      Files
        .inputStream(f)
        .use { is =>
          ContentTypeFinder
            .findType(is)
        }
        .attempt
        .unsafeRunSync() must beLeft(Error.ResourceNotFound(f.toString))
    }
  }

}
