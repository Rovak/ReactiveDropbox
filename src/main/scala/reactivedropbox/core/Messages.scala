package reactivedropbox.core

import com.dropbox.core.{DbxEntry, DbxWriteMode}
import java.io.File

/**
 * @param key Application Key
 * @param secret Application Secret
 */
case class AppInfo(key: String, secret: String)

/**
 * Add a new file
 * @param localPath local filepath
 * @param remotePath remote filepath
 * @param writeMode add, overwrite or specify a revision to add
 */
case class AddFile(localPath: String, remotePath: String, writeMode: DbxWriteMode = DbxWriteMode.add())

/**
 * Wrapper around dropbox entry
 * @param entry dropbox entry
 */
case class Entry(entry: DbxEntry)

/**
 * Local file with related dropbox entry
 * @param path local file path
 * @param entry dropbox entry
 */
case class LocalFile(path: String, entry: DbxEntry) {
  /**
   * @return java.io.File of the local filepath
   */
  def toFile = new File(path)
}

/**
 * Download file to local path
 * @param remotePath Remote path to download
 * @param localPath Local path to download
 * @param revision optional revision which to download
 */
case class DownloadFile(remotePath: String, localPath: String, revision: Option[String] = None)

/**
 * Requests a list of files for the given remote path
 * @param remotePath Path which to list
 */
case class ListFiles(remotePath: String)

/**
 * Directory listing
 * @param files
 * @param path
 */
case class DirectoryListing(files: DbxEntry.WithChildren, path: String)