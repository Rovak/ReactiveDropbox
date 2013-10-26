package reactivedropbox.actors

import com.dropbox.core.{DbxWriteMode, DbxClient, DbxRequestConfig}
import reactivedropbox.core._
import java.io.{FileOutputStream, File, FileInputStream}
import reactivedropbox.core.Entry
import reactivedropbox.core.LocalFile
import scala.concurrent.Future

/**
 * Client interface used for typed actor
 */
trait Client {

  /**
   * Upload a file
   *
   * @param localPath local path
   * @param remotePath remote path
   * @param mode writeMode
   * @return Entry
   */
  def uploadFile(localPath: String, remotePath: String, mode: DbxWriteMode): Future[Entry]

  /**
   * Download a file
   *
   * @param localPath local path
   * @param remotePath remote path
   * @param revision revision
   */
  def downloadFile(localPath: String, remotePath: String, revision: Option[String] = None): Future[LocalFile]

  /**
   * List the files
   *
   * @param remotePath remote path
   */
  def listFiles(remotePath: String): Future[DirectoryListing]

  /**
   * Search for files which match the query
   *
   * @param query criteria
   * @param path path where to search, it will recurse into child items
   */
  def search(query: String, path: String = "/")
}

/**
 * Client Implementation
 *
 * @param config Configuration
 * @param accessToken Access Token
 */
class DefaultClient(config: DbxRequestConfig, accessToken: String) extends Client {

  import scala.concurrent.ExecutionContext.Implicits.global

  val client = new DbxClient(config, accessToken)

  /**
   * Upload a file to dropbox
   *
   * @param localPath local file which to upload
   * @param remotePath remote path where to upload
   * @param mode writeMode
   */
  def uploadFile(localPath: String, remotePath: String, mode: DbxWriteMode) = Future {
    val inputFile = new File(localPath)
    val inputStream = new FileInputStream(inputFile)
    val uploadedFile = client.uploadFile(remotePath, mode, inputFile.length(), inputStream)
    Entry(uploadedFile)
  }

  /**
   * Download a file
   * @param remoteFile Remote location where to download the file
   * @param localFile Local location where to place the new file
   * @return New local file
   */
  def downloadFile(remoteFile: String, localFile: String, revision: Option[String] = None) = Future {
    val outputStream = new FileOutputStream(localFile)
    try {
      val downloadedFile = client.getFile(remoteFile, revision.getOrElse(null), outputStream)
      LocalFile(localFile, downloadedFile)
    } finally {
      outputStream.close()
    }
  }

  /**
   * Remote path
   * @param remotePath Path
   */
  def listFiles(remotePath: String) = Future {
    DirectoryListing(client.getMetadataWithChildren(remotePath), remotePath)
  }

  /**
   * Search for files which match the query
   *
   * @param query criteria
   * @param path path where to search, it will recurse into child items
   */
  def search(query: String, path: String) = Future {

  }
}
