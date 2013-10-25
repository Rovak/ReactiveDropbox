package reactivedropbox.core

import com.dropbox.core.{DbxEntry, DbxWriteMode}
import java.io.File

/**
 * @param key Application Key
 * @param secret Application Secret
 */
case class AppInfo(key: String, secret: String)

case class AddFile(source: String, target: String, writeMode: DbxWriteMode = DbxWriteMode.add())

case class Entry(entry: DbxEntry)

case class LocalFile(path: String, entry: DbxEntry) {
  def toFile = new File(path)
}

case class DownloadFile(remotePath: String, localPath: String, revision: Option[String] = None)