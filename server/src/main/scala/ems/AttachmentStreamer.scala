package no.java.ems

import storage.MongoDBStorage
import unfiltered.response.ResponseStreamer
import java.io.{InputStream, OutputStream}
import unfilteredx.{DispositionType, ContentDisposition}

object AttachmentStreamer {
  def apply(attachment: Attachment, storage: MongoDBStorage) = {
    ContentDisposition(DispositionType.ATTACHMENT, Some(attachment.name)).toResponseHeader ~> new ResponseStreamer {
      def stream(os: OutputStream) {
        val stream = storage.getStream(attachment)
        Streaming.copy(stream, os, closeOS = false)
      }
    }
  }
}

object Streaming {
  def copy(is: InputStream, os: OutputStream, closeOS: Boolean = true) {
    try {
      val buffer = new Array[Byte](1024 * 4)
      var read = 0
      while({read = is.read(buffer); read != -1}) {
        os.write(buffer, 0, read)
      }
    }
    finally {
      if (is != null) is.close()
      if (os != null && closeOS) os.close()
    }
  }
}