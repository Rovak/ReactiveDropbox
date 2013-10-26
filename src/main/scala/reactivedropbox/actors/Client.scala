package reactivedropbox.actors

import akka.actor.Actor
import com.dropbox.core.{DbxEntry, DbxWriteMode, DbxClient, DbxRequestConfig}
import reactivedropbox.core._
import java.io.{FileOutputStream, File, FileInputStream}
import reactivedropbox.core.Entry
import reactivedropbox.core.AddFile
import reactivedropbox.core.DownloadFile
import reactivedropbox.core.LocalFile

/**
 * Client which is connected to a dropbox account
 *
 * @param config Configuration
 * @param accessToken Access Token
 */
class Client(config: DbxRequestConfig, accessToken: String) extends Actor {

  val client = new DbxClient(config, accessToken)

  /**
   * Upload a file to dropbox
   *
   * @param localPath local file which to upload
   * @param remotePath remote path where to upload
   * @param mode writeMode
   */
  def uploadFile(localPath: String, remotePath: String, mode: DbxWriteMode) = {
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
  def downloadFile(remoteFile: String, localFile: String, revision: Option[String] = None) = {
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
  def listFiles(remotePath: String) = {
    DirectoryListing(client.getMetadataWithChildren(remotePath), remotePath)
  }

  def receive = {
    case AddFile(source, target, mode) =>
      sender ! uploadFile(source, target, mode)
    case DownloadFile(remote, local, revision) =>
      sender ! downloadFile(remote, local, revision)
    case ListFiles(remotePath) =>
      sender ! listFiles(remotePath)
  }
}
